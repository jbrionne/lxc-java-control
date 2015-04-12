package fr.operation.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.command.CommandResult;
import fr.operation.core.command.SSHCommandExecutor;
import fr.operation.core.command.ShellCommandExecutor;
import fr.operation.core.lxc.LxcCommand;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws InterruptedException {

		String containerName = "test-container";
		String template = "ubuntu";
		
		String username = "ubuntu";
		String password = "ubuntu";

		ShellCommandExecutor exShell = new ShellCommandExecutor("XXXXX");

		LxcCommand ex = new LxcCommand(exShell);
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

		String passPhrase = "passPhrase";

		ex.lxcAttach(containerName,
				"rm -rf /etc/ssh/ssh_host_dsa_key");
		ex.lxcAttach(containerName,
				"rm -rf /etc/ssh/ssh_host_dsa_key.pub");
		ex.lxcAttach(containerName,
				"rm -rf /etc/ssh/ssh_host_rsa_key");
		ex.lxcAttach(containerName,
				"rm -rf /etc/ssh/ssh_host_rsa_key.pub");
		ex.lxcAttachPrivileges(containerName,
				"ssh-keygen -t dsa -f /etc/ssh/ssh_host_dsa_key -N \'"
						+ passPhrase + "\'");
		ex.lxcAttachPrivileges(containerName,
				"ssh-keygen -t rsa1 -f /etc/ssh/ssh_host_rsa_key -N \'"
						+ passPhrase + "\'");
		 ex.lxcAttach(containerName, "ls -l /etc/ssh/");

		// int resExe7 = ex.lxcAttachPrivileges(containerName,
		// "/etc/init.d/ssh restart");
		ex.lxcStop(containerName);
		ex.lxcStart(containerName);				
		
		CommandResult resExeInfo = ex.lxcInfo(containerName);		
		
		String ipRes = resExeInfo.getOutputStream();
		LOG.info(ipRes);
		
		
		String ipContainer = Util.extract(ipRes);	
		LOG.info("ipContainer " + ipContainer);
		SSHCommandExecutor exSsh = new SSHCommandExecutor(ipContainer,
				"ubuntu", "ubuntu");
		exSsh.connect();
		
		exSsh.c("ls -l /etc/ssh/");		
		exSsh.c("ifconfig");
		exSsh.c("echo " + password + " | sudo -S apt update");
		
		exSsh.disconnect();
		
		// on host
		// ex.lxcCustomCommand("iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 25001 -j DNAT --to 10.x.x.x:25001");

	}
	
	
	

	 

}
