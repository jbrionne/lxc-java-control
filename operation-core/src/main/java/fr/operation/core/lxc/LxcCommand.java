package fr.operation.core.lxc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.command.Command;
import fr.operation.core.command.CommandResult;

public class LxcCommand {

	private static final Logger LOG = LoggerFactory.getLogger(LxcCommand.class);

	// /usr/bin/lxc-attach
	// /usr/bin/lxc-autostart
	// /usr/bin/lxc-cgroup
	// /usr/bin/lxc-checkconfig
	// /usr/bin/lxc-clone
	// /usr/bin/lxc-config
	// /usr/bin/lxc-console
	// /usr/bin/lxc-create
	// /usr/bin/lxc-destroy
	// /usr/bin/lxc-device
	// /usr/bin/lxc-execute
	// /usr/bin/lxc-freeze
	// /usr/bin/lxc-info
	// /usr/bin/lxc-ls
	// /usr/bin/lxc-monitor
	// /usr/bin/lxc-snapshot
	// /usr/bin/lxc-start
	// /usr/bin/lxc-start-ephemeral
	// /usr/bin/lxc-stop
	// /usr/bin/lxc-unfreeze
	// /usr/bin/lxc-unshare
	// /usr/bin/lxc-usernsexec
	// /usr/bin/lxc-wait

	private Command ex;

	public LxcCommand(Command command) {
		super();
		this.ex = command;
	}

	public CommandResult lxcStart(String containerName) {
		CommandResult cR = ex.c(s("lxc-start -n %s -d", containerName));
		// wait for initialization...the ip !
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOG.error("" + containerName, e);
		}
		return cR;
	}

	public CommandResult lxcExecute(String containerName, String nestedCommand) {
		return ex.c(s("lxc-execute -n %s -- %s", containerName, nestedCommand));
	}

	public CommandResult lxcAttachPrivileges(String containerName,
			String nestedCommand) {
		return ex
				.c(s("lxc-attach -n %s -e -- %s", containerName, nestedCommand));
	}

	public CommandResult lxcAttach(String containerName, String nestedCommand) {
		return ex.c(s("lxc-attach -n %s -- %s", containerName, nestedCommand));
	}

	public CommandResult lxcCreate(String containerName, String template) {
		return ex.c(s("lxc-create -n  %s -t %s", containerName, template));
	}

	public CommandResult lxcInfo(String containerName) {
		return ex.c(s("lxc-info -n  %s", containerName));
	}

	public CommandResult lxcLsFancy() {
		return ex.c(s("lxc-ls --fancy"));
	}

	public CommandResult lxcCustomCommand(String command) {
		return ex.c(command);
	}

	public CommandResult lxcStop(String containerName) {
		return ex.c(s("lxc-stop -n %s", containerName));
	}

	private String s(String format, Object... args) {
		return String.format(format, args);
	}

}
