package fr.operation.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Enviro {

	private static final Logger LOG = LoggerFactory.getLogger(Enviro.class);

	private Enviro() {
		throw new AssertionError("static");
	}
	
	public static final String HOME_CONFIG = "/home/jerome/git/lxc-java-control/operation-core/src/main/config/";

	public static final String SHH_CONFIG = HOME_CONFIG + "ssh_config";

	public static final String CONFIGLXC = HOME_CONFIG + "lxc/";

	public static final String CONFIGVARLXC = "/var/lib/lxc/";

	public static final String TEMPLATE_UBUNTU = "ubuntu";
	public static final String USERNAME_UBUNTU = "ubuntu";
	public static final String PASSWORD_UBUNTU = "ubuntu";
	
	

}
