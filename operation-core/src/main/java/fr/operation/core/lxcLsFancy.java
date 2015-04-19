package fr.operation.core;

public class lxcLsFancy {

	private String name;
	private String state;
	private String ipv4;
	private String ipv6;
	private String autostart;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getIpv4() {
		return ipv4;
	}
	public void setIpv4(String ipv4) {
		this.ipv4 = ipv4;
	}
	public String getIpv6() {
		return ipv6;
	}
	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}
	public String getAutostart() {
		return autostart;
	}
	public void setAutostart(String autostart) {
		this.autostart = autostart;
	}
	@Override
	public String toString() {
		return "lxcLsFancy [name=" + name + ", state=" + state + ", ipv4="
				+ ipv4 + ", ipv6=" + ipv6 + ", autostart=" + autostart + "]";
	}
	
	
}
