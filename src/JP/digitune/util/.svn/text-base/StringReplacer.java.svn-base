/*
 *	String Replace Class
 *		T.Kazawa 1997/03/04
 */

package JP.digitune.util;


public class StringReplacer
{
	private String str;

	public StringReplacer(String src, String before, String after)
	{
		str = replaceString(src, before, after);
	}

	public String toString()
	{
		return str;
	}

	public static String replaceString(String src, String before, String after)
	{
		StringBuffer sb = new StringBuffer();
		int oldidx = 0;
		int idx = src.indexOf(before);
		while (idx != -1)
		{
			sb.append(src.substring(oldidx, idx)).append(after);
			oldidx = idx + before.length();
			idx = src.indexOf(before, oldidx);
		}
		if (oldidx < src.length())
			sb.append(src.substring(oldidx));
		return sb.toString();
	}
}
