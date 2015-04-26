package fr.operation.core.init;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.Util;
import fr.operation.core.lxcLsFancy;
import fr.operation.core.command.Command;
import fr.operation.core.command.CommandResult;
import fr.operation.core.command.ShellCommandExecutor;
import fr.operation.core.lxc.LxcCommand;

public class MainPrepareLxc {

	private static final Logger LOG = LoggerFactory
			.getLogger(MainPrepareLxc.class);

	public static final String CONFIGETCLXC1 = "/etc/lxc/default.conf";
	public static final String CONFIGETCLXC2 = "/etc/lxc/lxc-usernet";
	public static final String CONFIGEDEFAULTTCLXC1 = "/etc/default/lxc";
	public static final String CONFIGEDEFAULTTCLXC2 = "/etc/default/lxc-net";

	public static void main(String[] args) throws InterruptedException {
		ShellCommandExecutor exShell = new ShellCommandExecutor("XXX");
		prepareLxc(exShell);
	}

	public static void prepareLxc(Command exShell) {
		// exShell.c("apt-get update");
		// exShell.c("apt-get install lxc");
		LxcCommand ex = new LxcCommand(exShell);
		CommandResult resExeInfo = ex.lxcLsFancy();
		String ipRes = resExeInfo.getOutputStream();
		List<lxcLsFancy> lstFancy = Util.extractTab(ipRes);
		for (lxcLsFancy c : lstFancy) {
			ex.lxcStopTimeout(c.getName(), "300");
		}

		LOG.info(exShell.c("service lxc-net stop").toString());

		LOG.info(exShell.c("rm -rf " + Enviro.CONFIGVARLXC + "*").toString());

		LOG.info(exShell.c(
				"cp -f " + Enviro.CONFIGLXC + CONFIGETCLXC1 + " "
						+ CONFIGETCLXC1).toString());

		LOG.info(exShell.c(
				"cp -f " + Enviro.CONFIGLXC + CONFIGETCLXC2 + " "
						+ CONFIGETCLXC2).toString());

		LOG.info(exShell.c(
				"cp -f " + Enviro.CONFIGLXC + CONFIGEDEFAULTTCLXC1 + " "
						+ CONFIGEDEFAULTTCLXC1).toString());

		LOG.info(exShell.c(
				"cp -f " + Enviro.CONFIGLXC + CONFIGEDEFAULTTCLXC2 + " "
						+ CONFIGEDEFAULTTCLXC2).toString());

		LOG.info(exShell.c("service lxc-net start").toString());
	}

}
