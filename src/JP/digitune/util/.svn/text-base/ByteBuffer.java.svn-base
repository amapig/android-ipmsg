/*
 * Byte Buffer Class
 * 1998/01/26 (C)Copyright T.Kazawa(Digitune)
 */

package JP.digitune.util;

public class ByteBuffer {
	static final int BUF_SIZE = 8192;
	protected byte[] buf = new byte[BUF_SIZE];
	protected int end = 0;
	
	public ByteBuffer(byte[] argbuf) {
		System.arraycopy(argbuf, 0, buf, 0, argbuf.length);
		end = argbuf.length;
	}
	
	public ByteBuffer() {
	}
	
	public void append(byte[] argbuf, int off, int len) {
		if (len > buf.length - end) {
			byte[] tmp = new byte[buf.length + BUF_SIZE];
			System.arraycopy(buf, 0, tmp, 0, end);
			buf = tmp;
		}
		System.arraycopy(argbuf, off, buf, end, len);
		end += len;
	}
	
	public void append(byte[] argbuf) {
		append(argbuf, 0, argbuf.length);
	}
	
	public byte[] getBytes() {
		byte[] tmp = new byte[end];
		System.arraycopy(buf, 0, tmp, 0, end);
		return tmp;
	}
}
