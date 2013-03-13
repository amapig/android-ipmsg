/*
 *	IP Messenger Proxy Class
 *		1997/10/16 (C) Copyright T.Kazawa (Digitune)
 */

package ipmsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import com.ipmsg.listener.IPMComListener;

import JP.digitune.util.ByteBuffer;

public class IPMProxy implements IPMComListener {
	private static final long KEEPALIVE_INTERVAL = 30000;
	public static final int PROXY_PORT = 2425;
	
	IPMsg ipmsg;
	boolean active = false;
	boolean broadcast_all = false;
	InetAddress localaddr;
	InetAddress proxyaddr;
	DataOutputStream dout;
	Hashtable douts = new Hashtable();
	Hashtable dsocks = new Hashtable();
	Hashtable IPtoPORT = new Hashtable();
	Hashtable PORTtoIP = new Hashtable();
	
	class Accept extends Thread {
		public Accept() {
			this.start();
		}
		
		public void run() {
			ServerSocket ss = null;
			try {
				ss = new ServerSocket(PROXY_PORT, 1024, localaddr);
				while (true) {
					Socket sock = ss.accept();
					connect(sock);
					ipmsg.refreshList();
				}
			} catch (IOException ex) {}
			try {
				if (ss != null)
					ss.close();
			} catch (IOException ex) {}
		}
	}
	
	class Listen extends Thread {
		private DataInputStream din;
		
		public Listen(DataInputStream argin) {
			din = argin;
			this.start();
		}
		
		public void run() {
			byte[] buf = new byte[8192];
			IPMByteBuffer ipmbb = new IPMByteBuffer();
			outer:
			while (!isInterrupted()) {
				while (!ipmbb.eop()) {
					int count = 0;
					try {
						count = din.read(buf);
					} catch (IOException ex) {
						break outer;
					}
					if (count == -1)
						break outer;
					ipmbb.append(buf, 0, count);
				}
				byte[] tmpbuf = ipmbb.getBytes();
				if (tmpbuf.length == 0)
					continue; // keep alive.
				IPMProxyEvent ipmpe = new IPMProxyEvent(this, tmpbuf);
				accept(ipmpe);
			}
			try {
				din.close();
			} catch (IOException ex) {}
		}
	}

	class KeepAlive extends Thread {
		public KeepAlive() {
			this.start();
		}
		public void run() {
			try {
				while (!isInterrupted()) {
					sleep(KEEPALIVE_INTERVAL);
					while (active) {
						try {
							if (dout != null) {
								synchronized (dout) {
									dout.write(new byte[]{0, 0, 0});
									dout.flush();
								}
								break;
							}
						} catch (IOException ex) {
							try {
								if (dout != null)
									dout.close();
							} catch (IOException exx) {}
							dout = null;
						}
						connectProxy();
					}
				}
			} catch (Throwable ex) {}
		}
	}

	void connect(Socket sock) throws IOException {
		DataOutputStream tmpdout
			= new DataOutputStream(sock.getOutputStream());
		douts.put(tmpdout, tmpdout);
		DataInputStream tmpdin
			= new DataInputStream(sock.getInputStream());
		new Listen(tmpdin);
	}
	
	synchronized void accept(IPMProxyEvent ipmpe) {
		DatagramSocket dsock;
		if (IPtoPORT.containsKey(ipmpe.getFromIPMAddress().toString())) {
			dsock = (DatagramSocket) dsocks.get(
				IPtoPORT.get(ipmpe.getFromIPMAddress().toString()));
		} else {
			int port = 0;
			Random rand = new Random(System.currentTimeMillis());
			while (true) {
				/* 49154 - 65534 */
				port = (int) (rand.nextLong() % 8192 + 57342);
				if (PORTtoIP.containsKey(new Integer(port)))
					continue;
				try {
					dsock = new DatagramSocket(port);
				} catch (SocketException ex) {
					continue;
				}
				break;
			}
			IPtoPORT.put(ipmpe.getFromIPMAddress().toString()
				, new Integer(port));
			PORTtoIP.put(new Integer(port)
				, ipmpe.getFromIPMAddress().toString());
			dsocks.put(new Integer(port), dsock);
		 
			IPMRecv tmprecv = new IPMRecv(dsock);
			tmprecv.addIPMComListener(this);
			tmprecv.start();
		}
		try {
			if (ipmpe.getToIPMAddress().getInetAddress().equals(
				InetAddress.getByName("255.255.255.255")) && broadcast_all) {
				IPMAddress[] ipma = ipmsg.getBroadcastAddr();
				for (int i = 0; i < ipma.length; i++) {
					DatagramPacket dp = new DatagramPacket(
						ipmpe.getPack().getBytes(),
						ipmpe.getPack().getBytes().length,
						ipma[i].getInetAddress(),
						ipma[i].getPort());
					dsock.send(dp);
				}
			} else {
				DatagramPacket dp = new DatagramPacket(
					ipmpe.getPack().getBytes(),
					ipmpe.getPack().getBytes().length,
					ipmpe.getToIPMAddress().getInetAddress(),
					ipmpe.getToIPMAddress().getPort());
				dsock.send(dp);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	synchronized void connectProxy() throws Exception {
		if (dout != null)
			return;
		Socket sock = new Socket(proxyaddr, PROXY_PORT, localaddr, PROXY_PORT);
		dout = new DataOutputStream(sock.getOutputStream());
		DataInputStream tmpdin = new DataInputStream(sock.getInputStream());
		new Listen(tmpdin);
	}
	
	synchronized void write(byte[] buf) {
		boolean done = false;
		while (dout != null && !done) {
			synchronized (dout) {
				try {
					dout.write(buf);
					dout.flush();
					done = true;
				} catch (IOException ex) {
					try {
						dout.close();
					} catch (IOException exx) {}
					dout = null;
				}
			}
			if (active && !done) {
				try {
					connectProxy();
				} catch (Exception ex) {
					break;
				}
			}
		}
		Enumeration enume = douts.keys();
		while (enume.hasMoreElements()) {
			DataOutputStream tmpdout = (DataOutputStream) enume.nextElement();
			try {
				tmpdout.write(buf);
				tmpdout.flush();
			} catch (IOException ex) {
				try {
					tmpdout.close();
				} catch (IOException exx) {}
				douts.remove(tmpdout);
			}
		}
	}
	
	public IPMProxy(IPMsg argipmsg, InetAddress argproxy, boolean argball) {
		ipmsg = argipmsg;
		broadcast_all = argball;
		localaddr = ipmsg.getLocalAddress();
		new Accept();
		if (argproxy == null)
			active = false;
		else {
			active = true;
			proxyaddr = argproxy;
			try {
				connectProxy();
			} catch (Exception ex) {}
			new KeepAlive();
		}
	}
	
	public IPMProxy(IPMsg argipmsg) {
		this(argipmsg, null, false);
	}
	
	public void addBroadcastPort(int port) {
		PORTtoIP.put(new Integer(port), "255.255.255.255:"+port);
	}
	
	public void receive(IPMComEvent ipme) {
		try {
			if (ipme.getIPMAddress().getInetAddress()
				.equals(InetAddress.getLocalHost())	&& PORTtoIP
				.get(new Integer(ipme.getIPMAddress().getPort())) != null)
				return;
		} catch (UnknownHostException ex) {}
		if (IPtoPORT.get(ipme.getIPMAddress().toString()) != null)
			return;
		ByteBuffer bb = new ByteBuffer();
		String prefix = ipme.getIPMAddress().toString() + ":"
			+ (String) PORTtoIP.get(new Integer(ipme.getLocalPort())) + ":";
		bb.append(prefix.getBytes());
		bb.append(ipme.getPack().getBytes());
		bb.append(new byte[]{0, 0, 0});
		write(bb.getBytes());
	}
}
