/*
 * IP Messenger Address Class
 * 1998/01/26 (C)Copyright T.Kazawa(Digitune)
 */

package ipmsg;

import java.net.InetAddress;

public class IPMAddress implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2908953051055607575L;
	int port;
	InetAddress addr;
	
	public IPMAddress(int argport, InetAddress argaddr) {
		port = argport;
		addr = argaddr;
	}
	
	public IPMAddress(byte[] argbuf) {
		int sep = 0;
		while (argbuf[sep] != ":".getBytes()[0] && sep < argbuf.length - 1)
			sep++;
		byte[] ipbuf = new byte[sep];
		byte[] portbuf = new byte[argbuf.length - sep - 1];
		System.arraycopy(argbuf, 0, ipbuf, 0, sep);
		System.arraycopy(argbuf, sep + 1, portbuf, 0, argbuf.length - sep - 1);
		try {
			addr = InetAddress.getByName(new String(ipbuf));
		} catch (Exception ex) {
			ex.printStackTrace();
			addr = null;
		}
		try {
			port = Integer.valueOf(new String(portbuf)).intValue();
		} catch (Exception ex) {
			ex.printStackTrace();
			port = 0;
		}
	}
	
	public int getPort() {
		return port;
	}
	
	public InetAddress getInetAddress() {
		return addr;
	}
	
	public String toString() {
		return addr.getHostAddress() + ":" + port;
	}
}
