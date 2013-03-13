/*
 * Byte Buffer Class
 *		1998/01/28 (C)Copyright T.Kazawa(Digitune)
 */

package ipmsg;

import JP.digitune.util.ByteBuffer;

public class IPMByteBuffer extends ByteBuffer {
	private int eopindex = 0;
	
	public IPMByteBuffer(byte[] argbuf) {
		super(argbuf);
	}
	
	public IPMByteBuffer() {
		super();
	}
	
	public boolean eop() {
		if (end > 3) {
			for (int i = 2; i < end; ) {
				if (buf[i] == 0) {
					if (buf[i - 1] == 0) {
						if (buf[i - 2] == 0) {
							eopindex = i - 2;
							return true;
						} else {
							i++;
						}
					} else {
						i += 2;
					}
				} else {
					i += 3;
				}
			}
		}
		return false;
	}
	
	public byte[] getBytes() {
		byte[] tmp = new byte[eopindex];
		end -= (eopindex + 3);
		System.arraycopy(buf, 0, tmp, 0, eopindex);
		System.arraycopy(buf, eopindex + 3, buf, 0, end);
		return tmp;
	}
}
