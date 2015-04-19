package fr.operation.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.Util;
import fr.operation.core.command.Command;
import fr.operation.core.command.CommandResult;
import fr.operation.core.lxc.LxcCommand;

public class UtilLxcInit {

	private static final Logger LOG = LoggerFactory
			.getLogger(UtilLxcInit.class);

	public static String initSsh(String containerName, String template,
			Command exShell, boolean overrideSshConfig, String passPhrase,
			String sshConfigPath) throws AssertionError, InterruptedException {

		LxcCommand ex = new LxcCommand(exShell);

		createAndStartContainer(containerName, template, ex);

		ex.lxcAttach(containerName, "rm -rf /etc/ssh/ssh_host_dsa_key");
		ex.lxcAttach(containerName, "rm -rf /etc/ssh/ssh_host_dsa_key.pub");
		ex.lxcAttach(containerName, "rm -rf /etc/ssh/ssh_host_rsa_key");
		ex.lxcAttach(containerName, "rm -rf /etc/ssh/ssh_host_rsa_key.pub");
		ex.lxcAttachPrivileges(containerName,
				"ssh-keygen -t dsa -f /etc/ssh/ssh_host_dsa_key -N \'"
						+ passPhrase + "\'");
		ex.lxcAttachPrivileges(containerName,
				"ssh-keygen -t rsa1 -f /etc/ssh/ssh_host_rsa_key -N \'"
						+ passPhrase + "\'");
		ex.lxcAttach(containerName, "ls -l /etc/ssh/");

		CommandResult resExe7 = ex.lxcAttachPrivileges(containerName,
				"/etc/init.d/ssh stop");
		LOG.info("ssh stop " + resExe7.getExitValue());

		CommandResult resExe8 = ex.lxcAttachPrivileges(containerName,
				"/etc/init.d/ssh start");
		LOG.info("ssh start " + resExe8.getExitValue());

		String ipContainer = getIpOfContainer(containerName, ex);

		UtilInit.addIpToSshConfigToAvoidChecking(exShell, overrideSshConfig,
				ipContainer, sshConfigPath);

		return ipContainer;
	}

	public static String getIpOfContainer(String containerName, LxcCommand ex) {
		String ip = null;
		int index = 0;
		while ((ip == null || ip.isEmpty()) && index < 60) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new AssertionError("ip", e);
			}
			CommandResult resExeInfo = ex.lxcInfo(containerName);
			String log = resExeInfo.getOutputStream();
			ip = Util.extract(log);
			index++;
		}

		if (ip == null || ip.isEmpty()) {
			throw new AssertionError("No ip found !");
		}
		return ip;
	}

	public static void createAndStartContainer(String containerName,
			String template, LxcCommand ex) throws AssertionError {

		CommandResult resInfo = ex.lxcInfo(containerName);
		if (resInfo.getExitValue() == 0) {
			LOG.debug("OK, already exists");
		} else if (resInfo.getExitValue() == 1) {
			LOG.debug("KO -> create");
			CommandResult resCreate = ex.lxcCreate(containerName, template);
			if (resCreate.getExitValue() != 0) {
				throw new AssertionError();
			}
		} else {
			throw new AssertionError();
		}

		CommandResult resStart = ex.lxcStart(containerName);
		if (resStart.getExitValue() == 0) {
			LOG.debug("OK");
		} else {
			throw new AssertionError();
		}
	}

}
