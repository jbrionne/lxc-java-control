package fr.operation.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.command.Command;
import fr.operation.core.command.CommandResult;
import fr.operation.core.command.SSHCommandExecutor;

public class UtilInit {

	private static final Logger LOG = LoggerFactory.getLogger(UtilInit.class);		

	public static void addIpToSshConfigToAvoidChecking(Command exShell,
			boolean overrideSshConfig, String ipContainer, String sshConfigPath) {
		LOG.info("addIpToSsh " + ipContainer);
		LOG.info("overrideSshConfig " + overrideSshConfig);
		if(overrideSshConfig) {			
			exShell.c("cp " + sshConfigPath + " /etc/ssh/ssh_config");
		}
		
		exShell.c("sed -i '1iUserKnownHostsFile=/dev/null' /etc/ssh/ssh_config");
		exShell.c("sed -i '1iStrictHostKeyChecking no' /etc/ssh/ssh_config");
		exShell.c("sed -i '1iHost " + ipContainer + "' /etc/ssh/ssh_config");		
	}	
	
	public static void addUser(Command ex, String username, String userpassword) {
		ex.c("adduser --disabled-login --gecos '' " + username);
		ex.c("usermod -a -G sudo " + username);	
		ex.c("echo " + username + ":" + userpassword + " | chpasswd");		
	}

	
	public static void removeUser(Command ex, String username) {
		ex.c("deluser " + username);		
	}
	
	public static void removeGroup(Command ex, String groupName) {
		ex.c("delgroup " + groupName);		
	}	

	public static void scp(String filePath, String destPath, String username,
			String password, Command ex, String ipContainer) {
		CommandResult c = ex.c("sshpass -p '" + password
				+ "' scp -o 'StrictHostKeyChecking no' " + filePath + " "
				+ username + "@" + ipContainer + ":" + destPath);
		LOG.info("" + c.getExitValue());
	}	
	
	public static void removeOldUserAndAddANew(String oldusername,
			String olduserpassword, String newusername,
			String newuserpassword, String ipContainer) {
		SSHCommandExecutor exSsh = new SSHCommandExecutor(ipContainer,
				oldusername, olduserpassword, true);
		exSsh.connect();
		UtilInit.addUser(exSsh, newusername, newuserpassword);		
		exSsh.disconnect();
		
		
		SSHCommandExecutor exSsh2 = new SSHCommandExecutor(ipContainer,
				newusername, newuserpassword, true);
		exSsh2.connect();
		UtilInit.removeUser(exSsh2, oldusername);
		exSsh2.disconnect();
	}
}
