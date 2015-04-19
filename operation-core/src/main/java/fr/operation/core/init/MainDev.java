package fr.operation.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.command.Command;
import fr.operation.core.command.CommandResult;
import fr.operation.core.command.SSHCommandExecutor;
import fr.operation.core.command.ShellCommandExecutor;

public class MainDev {

	private static final Logger LOG = LoggerFactory.getLogger(MainDev.class);
	private static final String CONFIG = "/home/jerome/git/lxc-java-control/operation-core/src/main/config/";
	private static final String CONFIGLXC = "/var/lib/lxc/";

	// template
	// /usr/local/share/lxc/templates or /usr/lib/lxc/templates/

	public static void main(String[] args) throws InterruptedException {

		String containerName = "test-container";		

		// lxc-attach -n yourlxc adduser username

		ShellCommandExecutor exShell = new ShellCommandExecutor("XXXXXX");
		String passPhrase = "passPhrase";
		
		String ipContainer = UtilLxcInit.initSsh(containerName, Enviro.TEMPLATE_UBUNTU, exShell, false, passPhrase, Enviro.HOME_CONFIG);			

		String eclipse = "eclipse.tar.gz";
		String jdk = "jdk1.8.0_40.tar.gz";
	
		UtilInit.scp(CONFIG + eclipse, "/home/" + Enviro.USERNAME_UBUNTU, Enviro.USERNAME_UBUNTU, Enviro.PASSWORD_UBUNTU, exShell,
				ipContainer);
		UtilInit.scp(CONFIG + jdk, "/home/" + Enviro.USERNAME_UBUNTU, Enviro.USERNAME_UBUNTU, Enviro.PASSWORD_UBUNTU, exShell,
				ipContainer);

		SSHCommandExecutor exSsh = new SSHCommandExecutor(ipContainer,
				Enviro.USERNAME_UBUNTU, Enviro.PASSWORD_UBUNTU, false);
		exSsh.connect();

		exSsh.c("echo " + Enviro.PASSWORD_UBUNTU + " | sudo -S apt update");
		
		exSsh.c("echo "
				+ Enviro.PASSWORD_UBUNTU
				+ " | sudo -S apt-get install --assume-yes xterm openbox firefox");
		
		exSsh.c("tar -xvf " + eclipse);
		exSsh.c("tar -xvf " + jdk);

		exSsh.c("sudo -u ubuntu chmod +x /home/ubuntu/eclipse/eclipse");

	
		
		exSsh.disconnect();
		
		// on host
		// ex.lxcCustomCommand("iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 25001 -j DNAT --to 10.x.x.x:25001");

		// apt-get install xserver-xephyr
		// Xephyr -ac :1 -screen 1024x768

		// export DISPLAY=10.0.3.1:1;
		// ./eclipse/eclipse

	}

	

}
