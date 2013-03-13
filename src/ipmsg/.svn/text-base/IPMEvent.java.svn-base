/*
 *	IP Messenger Event Class
 *		1997/10/16 (C) Copyright T.Kazawa (Digitune)
 */

package ipmsg;

import java.net.InetAddress;
import java.util.Date;

public class IPMEvent extends IPMComEvent {
	public static final int NO_EVENT = 0;
	public static final int UPDATELIST_EVENT = 1;
	public static final int RECEIVEMSG_EVENT = 2;
	public static final int READMSG_EVENT = 3;
	public static final int DELETEMSG_EVENT = 4;
	public static final int CANTSENDMSG_EVENT = 5;
	
	public static final int RECEIVEFILEMSG_EVENT = 6;

	int id = 0;
	Date now;

	public IPMEvent(Object argsrc, int argid, Date argnow, int argport
		, IPMPack argpack, IPMAddress argaddr) {
		super(argsrc, argport, argpack, argaddr);
		id = argid;
		now = argnow;
	}

	public IPMEvent(Object argsrc, int argid, Date argnow, IPMComEvent argce) {
		this(argsrc, argid, argnow, argce.getLocalPort(), argce.getPack()
			, argce.getIPMAddress());
	}

	public int getID() {
		return id;
	}

	public Date getDate() {
		return now;
	}
}
