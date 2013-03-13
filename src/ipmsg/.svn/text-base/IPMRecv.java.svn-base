/*
 *	IP Messenger Packet Receive Class
 *		1997/10/16 (C) Copyright T.Kazawa (Digitune)
 */

package ipmsg;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;

import com.ipmsg.listener.IPMComListener;

import android.util.Log;

public class IPMRecv extends Thread
{
	private String tag="IPMRecv";
	private static final int MEMORY = 100;
	private static final int MAXBUF = 8192;
	private DatagramSocket dsock;
	private Hashtable listeners = new Hashtable();
	private IPMPack[] recent = new IPMPack[MEMORY];
	private Hashtable flag = new Hashtable();
	private boolean isStop = false;

	public void setStop(boolean isStop)
	{
		this.isStop = isStop;
	}

	public IPMRecv(DatagramSocket argds)
	{
		Log.d(tag, "IPMRecv create");
		dsock = argds;

	}

	public synchronized void addIPMComListener(IPMComListener listener)
	{
		listeners.put(listener, listener);
	}

	public synchronized void removeIPMComListener(IPMComListener listener)
	{
		listeners.remove(listener);
	}

	public void run()
	{
		int counter = 0;
		byte[] tmpbuf = new byte[MAXBUF];
		while (true)
		{
			DatagramPacket dpack = new DatagramPacket(tmpbuf, tmpbuf.length);
			try
			{
				dsock.receive(dpack);
			} catch (IOException ex)
			{
				ex.printStackTrace();
				return;
			}
			if (isStop)
			{
				break;
			}
			int port = dpack.getPort();
			InetAddress addr = dpack.getAddress();
			byte[] buf = new byte[dpack.getLength()];
			System.arraycopy(tmpbuf, 0, buf, 0, buf.length);
			IPMPack tmppack = new IPMPack(buf);
			boolean b = false;
			if (flag.containsKey(tmppack.getKey()))
				b = true;
			counter = (counter + 1) % MEMORY;
			if (recent[counter] != null)
				flag.remove(recent[counter].getKey());
			recent[counter] = tmppack;
			flag.put(tmppack.getKey(), tmppack);
			if (b)
				continue;
			IPMAddress tmpaddr = new IPMAddress(port, addr);
			// System.out.println("Recv(" + dsock.getLocalPort() + "):" +
			// tmpaddr.toString() + ":"
			// + new String(tmppack.getBytes()));
			Log.e("IPMRecv", "Recv(" + dsock.getLocalPort() + "):" + tmpaddr.toString() + ":"
					+ new String(tmppack.getBytes()));
			tmppack.setFromHost(addr);
			tmppack.setFromPort(port);
			IPMComEvent tmpevent = new IPMComEvent(this, dsock.getLocalPort(), tmppack, tmpaddr);
			synchronized (this)
			{
				for (Enumeration enume = listeners.elements(); enume.hasMoreElements();)
				{
					IPMComListener listener = (IPMComListener) enume.nextElement();
					listener.receive(tmpevent);
				}
			}
		}
	}
}
