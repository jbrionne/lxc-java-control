package fr.operation.core.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellCommandExecutor implements Command {

	private static final Logger LOG = LoggerFactory
			.getLogger(ShellCommandExecutor.class);

	private String sudoCommand;
	private Map<String, String> envVariables = new HashMap<>();

	public ShellCommandExecutor() {
	}

	public ShellCommandExecutor(String sudoPassword) {
		this.sudoCommand = "echo \"" + sudoPassword + "\"| sudo -S -- sh -c \"";
	}

	public String putEnv(String key, String value) {
		return envVariables.put(key, value);
	}

	private CommandResult cinternal(String... command) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(command);
			Map<String, String> env = pb.environment();
			if (!envVariables.isEmpty()) {
				env.putAll(envVariables);
			}
			Process p = pb.start();
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			
			Callable<String> outputStream = new DisplayStream(in);			
			Callable<String> errorStream = new DisplayStream(err);
			Set<Future<String>> set = new HashSet<Future<String>>();
			ExecutorService pool = Executors.newFixedThreadPool(2);
			Future<String> futureOutputStream = pool.submit(outputStream);
			Future<String> futureErrorStream = pool.submit(errorStream);
			set.add(futureOutputStream);
			set.add(futureErrorStream);
			int exitValue = p.waitFor();

			String sOutput = futureOutputStream.get();
			String sError = futureErrorStream.get();

			pool.shutdown();
			
			return new CommandResult(exitValue, sOutput, sError);
		} catch (IOException e) {
			LOG.error("" + command, e);
		} catch (InterruptedException e) {
			LOG.error("" + command, e);
		} catch (ExecutionException e) {
			LOG.error("" + command, e);
		}
		return new CommandResult(-1, "", "");
	}

	public CommandResult c(String command) {
		CommandResult res = null;
		if (sudoCommand != null && !sudoCommand.isEmpty()) {
			res = cinternal("/bin/bash", "-c", sudoCommand + command + "\"");
		} else {
			res = cinternal("/bin/bash", "-c", command);
		}
		LOG.debug(command + " : " + res);
		return res;
	}
	
	

}
