/*
 *	Message Digester Class
 *		1998/02/04 (C)Copyright T.Kazawa(Digitune)
 */

package JP.digitune.util;

import java.security.*;

public class MessageDigester {
	private MessageDigester() {
	}
	
	public static String getMD5(String src) {
		byte[] buf = src.getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			buf = md.digest(buf);
		} catch (NoSuchAlgorithmException e) {}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++)
			sb.append(Integer.toString(new Byte(buf[i]).intValue()
				, Character.MAX_RADIX));
		return sb.toString();
	}
}
