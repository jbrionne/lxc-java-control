package fr.operation.core;

import java.util.regex.Pattern;

public class Util {

	private Util() {
		throw new AssertionError("static");
	}

	static String extract(String log) {
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

	static boolean isIPAddress(String str) {
		Pattern ipPattern = Pattern
				.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
		return ipPattern.matcher(str).matches();
	}
}
