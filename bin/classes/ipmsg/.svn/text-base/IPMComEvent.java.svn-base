/*
 *	IP Messenger Commnucation Event Class
 *		1997/10/16 (C) Copyright T.Kazawa (Digitune)
 */

package ipmsg;

import java.util.EventObject;

public class IPMComEvent extends EventObject {
	protected int port = 0;
	protected IPMPack pack = null;
	protected IPMAddress addr = null;

	public IPMComEvent(Object argsrc, int argport, IPMPack argpack
		, IPMAddress argaddr) {
		super(argsrc);
		port = argport;
		pack = argpack;
		addr = argaddr;
	}

	public int getLocalPort() {
		return port;
	}

	public IPMPack getPack() {
		return pack;
	}

	public IPMAddress getIPMAddress() {
		return addr;
	}
}
