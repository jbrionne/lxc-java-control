package fr.operation.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.operation.core.command.CommandResult;

public class Util {

	private static final Logger LOG = LoggerFactory.getLogger(Util.class);

	private Util() {
		throw new AssertionError("static");
	}


	public static String extract(String log) {
		// Name: test-container
		// State: RUNNING
		// PID: 23692
		// IP: 10.0.3.142
		// CPU use: 0.76 seconds
		// BlkIO use: 156.00 KiB
		// Memory use: 9.73 MiB
		// KMem use: 0 bytes
		// Link: veth1K9S89
		// TX bytes: 982 bytes
		// RX bytes: 1010 bytes
		// Total bytes: 1.95 KiB
		String lines[] = log.split("\\r?\\n");
		for (String s : lines) {
			if (s != null && !s.isEmpty()) {
				if (s.startsWith("IP")) {
					String[] ls = s.split(" ");
					for (String g : ls) {
						if (g != null && !g.isEmpty()) {
							if (isIPAddress(g)) {
								return g;
							}
						}
					}
				}
			}
		}
		return "";
	}

	public static List<lxcLsFancy> extractTab(String log) {
		List<lxcLsFancy> lst = new ArrayList<lxcLsFancy>();
		// NAME STATE IPV4 IPV6 AUTOSTART
		// ----------------------------------------------------
		// dhcp-container RUNNING 10.0.3.215 - NO
		String lines[] = log.split("\\r?\\n");
		int index = 0;
		for (String s : lines) {
			if (s != null && !s.isEmpty() && index >= 2) {
				String[] ls = s.split(" +");
				lxcLsFancy c = new lxcLsFancy();
				c.setName(ls[0]);
				c.setState(ls[1]);
				c.setIpv4(ls[2]);
				c.setIpv6(ls[3]);
				c.setAutostart(ls[4]);
				lst.add(c);
			}
			index++;
		}
		return lst;
	}

	static boolean isIPAddress(String str) {
		Pattern ipPattern = Pattern
				.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
		return ipPattern.matcher(str).matches();
	}
}
