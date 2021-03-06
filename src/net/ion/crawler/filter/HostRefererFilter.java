package net.ion.crawler.filter;

import net.ion.crawler.link.Link;

public class HostRefererFilter {

	private String server;

	public HostRefererFilter(String server) {
		this.server = server;
	}

	public boolean accept(Link link) {
		return (link.getReferer() != null && link.getReferer().startsWith(server));
	}
}
