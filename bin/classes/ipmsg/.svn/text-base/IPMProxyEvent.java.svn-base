/*
 *	IP Messenger Proxy Event Class
 *		1997/10/16 (C) Copyright T.Kazawa (Digitune)
 */

package ipmsg;

import JP.digitune.util.ByteBuffer;

import java.net.InetAddress;
import java.util.EventObject;

public class IPMProxyEvent extends EventObject {
	protected IPMPack pack = null;
	protected IPMAddress fromaddr = null;
	protected IPMAddress toaddr = null;
	
	public IPMProxyEvent(Object argsrc, IPMPack argpack
		, IPMAddress argfrom, IPMAddress argto) {
		super(argsrc);
		pack = argpack;
		fromaddr = argfrom;
		toaddr = argto;
	}
	
	public IPMProxyEvent(Object argsrc, byte[] buf) {
		super(argsrc);
		int colons = 0, index = 0, begin = 0;
		int[] indexes = new int[4];
		while (buf[begin] == 0)
			begin++;
		while (colons < 4) {
			if (buf[index] == ":".getBytes()[0]) {
				indexes[colons] = index;
				colons++;
			}
			index++;
		}
		try {
			fromaddr = new IPMAddress(Integer.valueOf(
				new String(buf, indexes[0]+1, indexes[1]-indexes[0]-1))
				.intValue(), InetAddress.getByName(
				new String(buf, begin, indexes[0] - begin)));
			toaddr = new IPMAddress(Integer.valueOf(
				new String(buf, indexes[2]+1, indexes[3]-indexes[2]-1))
				.intValue(), InetAddress.getByName(
				new String(buf, indexes[1]+1, indexes[2]-indexes[1]-1)));
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		byte[] packbuf = new byte[buf.length - indexes[3] - 1];
		System.arraycopy(buf, indexes[3] + 1, packbuf, 0, packbuf.length);
		pack = new IPMPack(packbuf);
	}
	
	public byte[] getBytes() {
		ByteBuffer bb = new ByteBuffer();
		String prefix = fromaddr.toString() + ":" + toaddr.toString() + ":";
		bb.append(prefix.getBytes());
		bb.append(pack.getBytes());
		byte[] suffix = new byte[]{0, 0, 0};
		bb.append(suffix);
		return bb.getBytes();
	}
	
	public IPMPack getPack() {
		return pack;
	}
	
	public IPMAddress getFromIPMAddress() {
		return fromaddr;
	}
	
	public IPMAddress getToIPMAddress() {
		return toaddr;
	}
}
