/*
 *	Broadcast Packet Send Class
 *		1997/10/27 (C) Copyright T.Kazawa(Digitune)
 */

package ipmsg;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.DatagramSocket;

public class BroadcastSend extends IPMSend
{
	private static final int wait = 1000;
	private IPMAddress baddrs[];

	public void run()
	{
		if (baddrs.length > 0)
		{
			int limitedb = 0;
			try
			{
				for (; limitedb < baddrs.length; limitedb++)
				{
					if (baddrs[limitedb] == null)
						continue;
					else if (baddrs[limitedb].getInetAddress().equals(InetAddress.getByName("255.255.255.255")))
						send(dsock, spack, baddrs[limitedb]);
					else
						break;
				}
			} catch (UnknownHostException ex)
			{
			}
			long tmpcomm = spack.getCommand();
			spack.setCommand(IPMsg.IPMSG_NOOPERATION);
			for (int i = limitedb; i < baddrs.length; i++)
				send(dsock, spack, baddrs[i]);
			try
			{
				sleep(wait);
			} catch (InterruptedException ex)
			{
			}
			spack.setCommand(tmpcomm);
			for (int i = limitedb; i < baddrs.length; i++)
				send(dsock, spack, baddrs[i]);
			try
			{
				sleep(wait);
			} catch (InterruptedException ex)
			{
			}
			for (int i = limitedb; i < baddrs.length; i++)
				send(dsock, spack, baddrs[i]);
		}
	}

	public BroadcastSend(DatagramSocket argsock, IPMPack argpack, IPMAddress[] argaddrs)
	{
		super(argsock, argpack, null);
		baddrs = argaddrs;
		start();
	}
}
