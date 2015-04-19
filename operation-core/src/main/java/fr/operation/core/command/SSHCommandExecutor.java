package fr.operation.core.command;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

//TODO
public class SSHCommandExecutor implements Command {

	private static final Logger LOG = LoggerFactory
			.getLogger(SSHCommandExecutor.class);

	private String host;
	private String user;
	private String password;
	private JSch jsch = new JSch();
	private Session session;
	private boolean autoSudo;
	private String sudoCommand;
	
	public SSHCommandExecutor(String host, String user, String password, boolean autoSudo) {
		super();
		this.host = host;
		this.user = user;
		this.password = password;
		this.autoSudo = autoSudo;		
		this.sudoCommand = "echo \"" + password + "\"| sudo -S -- sh -c \"";
	}

	public void connect() {
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();			
			LOG.info("Connected");
		} catch (JSchException e) {
			LOG.error("", e);
		}
	}
	
	private CommandResult cinternal(String command) {
		try {
			LOG.info("COMMAND " + command);
			Channel channel = session.openChannel("exec");			
			((ChannelExec) channel).setCommand(command);
			channel.connect();
			
			InputStream in = channel.getInputStream();
			InputStream err = ((ChannelExec) channel).getErrStream();			
			
			Callable<String> outputStream = new DisplayStream(in);			
			Callable<String> errorStream = new DisplayStream(err);
			Set<Future<String>> set = new HashSet<Future<String>>();
			ExecutorService pool = Executors.newFixedThreadPool(2);
			Future<String> futureOutputStream = pool.submit(outputStream);
			Future<String> futureErrorStream = pool.submit(errorStream);
			set.add(futureOutputStream);
			set.add(futureErrorStream);		
			
			int exitValue = channel.getExitStatus();
			
			String sOutput = futureOutputStream.get();
			String sError = futureErrorStream.get();
			
			pool.shutdown();
			channel.disconnect();
			
			LOG.info("DONE");
			return new CommandResult(exitValue, sOutput, sError);
		} catch (Exception e) {
			LOG.error(command, e);
		}
		return new CommandResult(0, "", "");
	}
	
	public CommandResult c(String command) {
		CommandResult res = null;
		if (autoSudo) {
			res = cinternal(sudoCommand + command + "\"");
		} else {
			res = cinternal(command);
		}
		LOG.debug(command + " : " + res);
		return res;
	}
	
	public void disconnect() {		
		session.disconnect();
	}
}
