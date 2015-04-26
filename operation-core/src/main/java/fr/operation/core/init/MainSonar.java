package fr.operation.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.command.Command;
import fr.operation.core.command.SSHCommandExecutor;
import fr.operation.core.command.ShellCommandExecutor;

public class MainSonar {

	private static final Logger LOG = LoggerFactory.getLogger(MainSonar.class);
	private static final String CONFIG = "/home/jerome/git/lxc-java-control/operation-core/src/main/config/";
	private static final String CONFIGLXC = "/var/lib/lxc/";

	public static void main(String[] args) throws InterruptedException {

		String containerName = "sonarqube-container";

		Command exShell = new ShellCommandExecutor("XXXX");
		String passPhrase = "passPhrase";

		String newusername = "username";
		String newuserpassword = "userpassword";

		String ipContainer = UtilLxcInit.initSsh(containerName,
				Enviro.TEMPLATE_UBUNTU, exShell, true, passPhrase,
				Enviro.HOME_CONFIG);

		UtilInit.removeOldUserAndAddANew(Enviro.USERNAME_UBUNTU,
				Enviro.PASSWORD_UBUNTU, newusername, newuserpassword,
				ipContainer);

		SSHCommandExecutor exSsh = new SSHCommandExecutor(ipContainer,
				newusername, newuserpassword, true);
		exSsh.connect();

		exSsh.c("apt update");
		exSsh.c("apt-get install --assume-yes postgresql");
		exSsh.c("apt-get install --assume-yes zip unzip");
		exSsh.c("apt-get install --assume-yes default-jdk");

		exSsh.c("sudo -u postgres psql -c \\\"CREATE USER sonar WITH PASSWORD 'sonar';\\\"");
		exSsh.c("sudo -u postgres psql -c \\\"CREATE DATABASE sonar;\\\"");
		exSsh.c("sudo -u postgres psql -c \\\"GRANT ALL PRIVILEGES ON DATABASE sonar to sonar;\\\"");

		exSsh.disconnect();

		String sonarqube = "sonarqube-5.0.1";
		String sonarqubezip = sonarqube + ".zip";
		UtilInit.scp(CONFIG + sonarqubezip, "/home/" + newusername,
				newusername, newuserpassword, exShell, ipContainer);

		String sonarqubeconf = "/home/" + newusername + "/" + sonarqube
				+ "/conf";
		String sonarprop = "sonar.properties";
		String wrappperconf = "wrapper.conf";

		SSHCommandExecutor exSsh2 = new SSHCommandExecutor(ipContainer,
				newusername, newuserpassword, false);

		exSsh2.connect();

		exSsh2.c("unzip " + sonarqubezip);
		exSsh2.c("rm -rf " + sonarqubezip);

		exSsh2.c("rm -rf " + sonarqubeconf + "/" + sonarprop);
		exSsh2.c("rm -rf " + sonarqubeconf + "/" + wrappperconf);

		UtilInit.scp(CONFIG + "sonarqube/" + sonarprop, sonarqubeconf,
				newusername, newuserpassword, exShell, ipContainer);

		UtilInit.scp(CONFIG + "sonarqube/" + wrappperconf, sonarqubeconf,
				newusername, newuserpassword, exShell, ipContainer);

		LOG.info(exSsh2.c(sonarqube + "/bin/linux-x86-64/sonar.sh start")
				.toString());
		LOG.info("view sonar on http://" + ipContainer + ":9000/");

		exSsh2.disconnect();

		//

	}

}
