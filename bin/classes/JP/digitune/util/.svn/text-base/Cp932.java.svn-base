package JP.digitune.util;

/*
 * The Cp932 class contains a utility method for converting Microsoft's
 * Cp 932 into JIS.
 *
 * @author Kazuhiro Kazama
 * @version 1.0 01/06/97
 */
import java.util.*;

public class Cp932 {
	private static boolean isCp932 = false;

	static {
		String p = System.getProperty("iscp932");
		String os = System.getProperty("os.name");
		if (Locale.getDefault().getLanguage().equals("ja")) {
			if (p != null && Boolean.getBoolean(p))
				isCp932 = true;
			else if (os != null
				&& (os.equals("Windows 95") || os.equals("Windows NT")))
				isCp932 = true;
		}
	}

	/*
	 * You can't use this constructor.
	 */
	private Cp932() {
	}

	/*
	 * This method converts Cp932 to JIS.
	 */
	public static String toJIS(String s) {
		if (!isCp932 || s == null)
			return s;
		StringBuffer sb = new StringBuffer();
		char c;
		for (int i = 0; i < s.length(); i++) {
			c  = s.charAt(i);
			switch (c) {
			case 0xff3c:	// FULLWIDTH REVERSE SOLIDUS ->
				c = 0x005c;	// REVERSE SOLIDUS
				break;
			case 0xff5e:	// FULLWIDTH TILDE ->
				c = 0x301c;	// WAVE DASH
				break;
			case 0x2225:	// PARALLEL TO ->
				c = 0x2016;	// DOUBLE VERTICAL LINE
				break;
			case 0xff0d:	// FULLWIDTH HYPHEN-MINUS ->
				c = 0x2212;	// MINUS SIGN
				break;
			case 0xffe0:	// FULLWIDTH CENT SIGN ->
				c = 0x00a2;	// CENT SIGN
				break;
			case 0xffe1:	// FULLWIDTH POUND SIGN ->
				c = 0x00a3;	// POUND SIGN
				break;
			case 0xffe2:	// FULLWIDTH NOT SIGN ->
				c = 0x00ac;	// NOT SIGN
				break;
			}
			sb.append(c);
		}
		return new String(sb);
	}

	/*
	 * This method convert JIS to Cp932.
	 */
	public static String toCp932(String s) {
		if (!isCp932 || s == null)
			return s;
		StringBuffer sb = new StringBuffer();
		char c;
		for (int i = 0; i < s.length(); i++) {
			c  = s.charAt(i);
			switch (c) {
				case 0x005c:	// REVERSE SOLIDUS ->
				c = 0xff3c;	// FULLWIDTH REVERSE SOLIDUS
				break;
			case 0x301c:	// WAVE DASH ->
				c = 0xff5e;	// FULLWIDTH TILDE
				break;
			case 0x2016:	// DOUBLE VERTICAL LINE ->
				c = 0x2225;	// PARALLEL TO
				break;
			case 0x2212:	// MINUS SIGN ->
				c = 0xff0d;	// FULLWIDTH HYPHEN-MINUS
				break;
			case 0x00a2:	// CENT SIGN ->
				c = 0xffe0;	// FULLWIDTH CENT SIGN
				break;
			case 0x00a3:	// POUND SIGN ->
				c = 0xffe1;	// FULLWIDTH POUND SIGN
				break;
			case 0x00ac:	// NOT SIGN ->
				c = 0xffe2;	// FULLWIDTH NOT SIGN
				break;
			}
			sb.append(c);
		}
		return new String(sb);
	}
}
