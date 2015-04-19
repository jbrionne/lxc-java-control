package fr.operation.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.Util;
import fr.operation.core.command.Command;
import fr.operation.core.command.CommandResult;
import fr.operation.core.command.SSHCommandExecutor;
import fr.operation.core.command.ShellCommandExecutor;
import fr.operation.core.lxc.LxcCommand;

public class MainSonar {

	private static final Logger LOG = LoggerFactory.getLogger(MainSonar.class);
	private static final String CONFIG = "/home/jerome/git/lxc-java-control/operation-core/src/main/config/";
	private static final String CONFIGLXC = "/var/lib/lxc/";
	



	public static void main(String[] args) throws InterruptedException {

		String containerName = "sonarqube-container";		

		Command exShell = new ShellCommandExecutor("XXXXX");
		String passPhrase = "passPhrase";
		
		String newusername = "username";
		String newuserpassword = "userpassword";

		String ipContainer = UtilLxcInit.initSsh(containerName, Enviro.TEMPLATE_UBUNTU, exShell, true, passPhrase, Enviro.HOME_CONFIG);			
						
		UtilInit.removeOldUserAndAddANew(Enviro.USERNAME_UBUNTU, Enviro.PASSWORD_UBUNTU, newusername, newuserpassword, ipContainer);
		
		String sonarqube = "sonarqube-5.0.1";		
		String sonarqubezip = sonarqube + ".zip";
		UtilInit.scp(CONFIG + sonarqubezip, "/home/" + newusername, newusername, newuserpassword, exShell,
				ipContainer);
		
		String jdk = "jdk1.8.0_40.tar.gz";
		UtilInit.scp(CONFIG + jdk, "/home/" + newusername, newusername, newuserpassword, exShell,
				ipContainer);
		
		
		SSHCommandExecutor exSsh = new SSHCommandExecutor(ipContainer,
				newusername, newuserpassword, true);
		exSsh.connect();
		
		exSsh.c("apt update");	
		exSsh.c("apt-get install --assume-yes postgresql");		
		
		exSsh.c("sudo -u postgres psql -c \\\"CREATE USER sonar WITH PASSWORD 'sonar';\\\"");	
		exSsh.c("sudo -u postgres psql -c \\\"CREATE DATABASE sonar;\\\"");	
		exSsh.c("sudo -u postgres psql -c \\\"GRANT ALL PRIVILEGES ON DATABASE sonar to sonar;\\\"");	
					
		
		exSsh.c("apt-get install --assume-yes zip unzip");			
		exSsh.c("unzip " + sonarqubezip);
		exSsh.c("rm -rf " + sonarqubezip);	
		exSsh.c(sonarqube + "/bin/linux-x86-64/sonar.sh start");			
		
		exSsh.c("tar -xvf " + jdk);
		
		//JAVA_HOME !!! 
		//TODO

		//postgres
		//conf postgres
		//sonar..
		
		exSsh.disconnect();	
		

	}


	


	private static void test(Command exSsh) {
		exSsh.c("apt update");
	}




	

}
