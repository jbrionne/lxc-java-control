package fr.operation.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.command.CommandResult;
import fr.operation.core.command.SSHCommandExecutor;
import fr.operation.core.command.ShellCommandExecutor;
import fr.operation.core.lxc.LxcCommand;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private static final String CONFIG = "/home/jerome/git/lxc-java-control/operation-core/src/main/config/";

	public static void main(String[] args) throws InterruptedException {

		String containerName = "test-container";
		String template = "ubuntu";
		
		String username = "ubuntu";
		String password = "ubuntu";

		ShellCommandExecutor exShell = new ShellCommandExecutor("XXXXXX");

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
		
		String eclipse = "eclipse.tar.gz";
		String jdk = "jdk1.8.0_25.tar.gz";
		
		scp(CONFIG + "eclipse.tar.gz", "/home/" + username, username, password, ex, ipContainer);
		scp(CONFIG + "jdk1.8.0_25.tar.gz", "/home/" + username, username, password, ex, ipContainer);
		
		
		SSHCommandExecutor exSsh = new SSHCommandExecutor(ipContainer,
				"ubuntu", "ubuntu");
		exSsh.connect();
		
		exSsh.c("tar -xvf " + eclipse);
		exSsh.c("tar -xvf " + jdk);		
		exSsh.c("sudo -u ubuntu mkdir -p /home/ubuntu/.pulse/");
		exSsh.c("chmod +x /home/ubuntu/eclipse/eclipse");	
		
		exSsh.c("rm ~/.swt/lib/linux/x86_64");	
		exSsh.c("ln -s /usr/lib/jni ~/.swt/lib/linux/x86_64");	
		
		
		scp(CONFIG + "client.conf", "/home/ubuntu/.pulse/", username, password, ex, ipContainer);
		
		exSsh.disconnect();
		
					
		ex.lxcCustomCommand("cp -r " + CONFIG + "test-container /home/jerome/.local/share/lxc/");
		ex.lxcCustomCommand("chown -R jerome /home/jerome/.local/share/lxc/test-container");
		ex.lxcCustomCommand("chmod +x /home/jerome/.local/share/lxc/test-container/setup-pulse.sh");
		ex.lxcCustomCommand("chmod +x /home/jerome/.local/share/lxc/test-container/start-eclipse");
		ex.lxcCustomCommand("sudo chown -R 1000:1000 ~/.local/share/lxc/test-container/rootfs/home/ubuntu");
		
		
		// on host
		// ex.lxcCustomCommand("iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 25001 -j DNAT --to 10.x.x.x:25001");

	}

	private static void scp(String filePath, String destPath, String username, String password, LxcCommand ex,
			String ipContainer) {
		CommandResult c = ex.lxcCustomCommand("sshpass -p '" + password + "' scp " + filePath + " " + username + "@" + ipContainer + ":" + destPath);
		LOG.info("" + c.getExitValue());
	}
	
	
	
	
	

	 

}
