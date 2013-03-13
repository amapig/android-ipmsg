/*
 *	IP Messenger Packet Class
 *		1997/10/14 (C) Copyright T.Kazawa (Digitune)
 */

package ipmsg;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import JP.digitune.util.ByteBuffer;
import JP.digitune.util.StringReplacer;
import android.util.Log;

public class IPMPack
{
	private InetAddress fromHost;
	private int fromPort;
	private byte[] pack;
	private long command = 0;
	private String user = "android ipmsg", version = null, no = null, host = null, extra = null, group = null;
	// private FileMsgInfo fileMsgInfo;
	private List<FileMsgInfo> fileMsgInfo;

	public List<FileMsgInfo> getFileMsgInfo()
	{
		return fileMsgInfo;
	}

	// public void setFileMsgInfo(FileMsgInfo fileMsgInfo)
	// {
	// this.fileMsgInfo = fileMsgInfo;
	// }

	public void addFileMsgInfo(FileMsgInfo object)
	{

		if (this.fileMsgInfo == null)
		{
			this.fileMsgInfo = new ArrayList<FileMsgInfo>();
		}
		this.fileMsgInfo.add(object);
	}

	public IPMPack(String argver, String argno, String arguser, String arghost, long argcommand, String argextra,
			String arggroup)
	{
		version = argver;
		no = argno;
		user = arguser;
		host = arghost;
		command = argcommand;
		extra = argextra;
		group = arggroup;
	}

	private void packed()
	{
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(version).append(":");
		strbuf.append(no).append(":");
		strbuf.append(user).append(":");
		strbuf.append(host).append(":");
		strbuf.append(Long.toString(command)).append(":");
		strbuf.append(extra);
		String tmpstr = new String(strbuf);
		String ls = System.getProperty("line.separator", "\n");
		String cr = "\n";
		tmpstr = StringReplacer.replaceString(tmpstr, ls, cr);
		ByteBuffer bb = new ByteBuffer();
		try
		{
			bb.append(tmpstr.getBytes("GBK"));
		} catch (UnsupportedEncodingException ex)
		{
			ex.printStackTrace();
			return;
		}
		byte[] nullbyte =
		{ 0 };
		bb.append(nullbyte);
		if (group != null && !group.equals(""))
		{
			try
			{
				bb.append(group.getBytes("GBK"));
				bb.append(nullbyte);
			} catch (UnsupportedEncodingException ex)
			{
			}
		}
		pack = bb.getBytes();
	}

	public IPMPack(byte[] buf)
	{
		pack = buf;
		unpacked();
		unpackedGroup();
	}

	private void unpacked()
	{

		byte[] buf = pack;
		int j = buf.length - 1;
		if (buf[j] == 0)
		{
			while (buf[j] == 0)
				j--;
			byte[] tmpbuf = new byte[j + 1];
			System.arraycopy(buf, 0, tmpbuf, 0, tmpbuf.length);
			buf = tmpbuf;
		}
		int i = 0;
		for (i = 0; i < buf.length; i++)
			if (buf[i] == 0)
				break;
		if (i < buf.length)
		{
			j = buf.length - 1;
			byte[] tmpbuf = new byte[j - i];
			System.arraycopy(buf, i + 1, tmpbuf, 0, j - i);
			try
			{
				group = new String(tmpbuf, "GBK");
			} catch (UnsupportedEncodingException ex)
			{
				ex.printStackTrace();
			}
			tmpbuf = new byte[i];
			System.arraycopy(buf, 0, tmpbuf, 0, i);
			buf = tmpbuf;
		}
		String tmpstr;
		try
		{
			tmpstr = new String(buf, 0, buf.length, "GBK");
		} catch (UnsupportedEncodingException ex)
		{
			ex.printStackTrace();
			return;
		}
		String ls = System.getProperty("line.separator", "\n");
		String cr = "\n";
		tmpstr = StringReplacer.replaceString(tmpstr, ls, cr).trim();
		Log.d("IPMPack", tmpstr);
		StringTokenizer tokenizer = new StringTokenizer(tmpstr, ":", false);
		try
		{
			version = tokenizer.nextToken();
			no = tokenizer.nextToken();
			user = tokenizer.nextToken();
			host = tokenizer.nextToken();
			command = Long.parseLong(tokenizer.nextToken());
		} catch (NoSuchElementException ex)
		{
			ex.printStackTrace();
			return;
		} catch (NumberFormatException ex)
		{
			ex.printStackTrace();
			return;
		}
		if (tokenizer.hasMoreTokens())
			extra = tokenizer.nextToken();
		while (tokenizer.hasMoreTokens())
		{

			extra = extra + ':' + tokenizer.nextToken();
		}

	}

	public boolean compare(IPMPack argpack)
	{
		if (user.equals(argpack.getUser()) && host.equals(argpack.getHost()) && no.equals(argpack.getNo())
				&& command == argpack.getCommand())
			return true;
		return false;
	}

	public void unpackedGroup()
	{
		if (group == null || group.equals(""))
		{
			return;
		}
		long fileOpt = getCommand() & IPMsg.IPMSG_OPTFILEMASK;
		if (fileOpt != IPMsg.IPMSG_FILEATTACHOPT)
		{
			return;
		}
		StringTokenizer tokenizer = new StringTokenizer(group, ":", false);
		int i = 0;
		FileMsgInfo tempMsgInfo = null;
		while (tokenizer.hasMoreTokens())
		{
			String str = tokenizer.nextToken();

			switch (i % 5)
			{
			case 0:
				tempMsgInfo = new FileMsgInfo();
				byte[] nullbyte ={ 0x07 };
				str = str.replace(new String(nullbyte), "");
				tempMsgInfo.setFileNo(str);
				break;
			case 1:
				tempMsgInfo.setFileName(str);
				break;
			case 2:
				tempMsgInfo.setFileSize(str);
				break;
			case 3:
				tempMsgInfo.setModifyTime(str);
				break;
			case 4:
				tempMsgInfo.setFileProperty(str);
				if (fileMsgInfo == null)
				{
					fileMsgInfo = new ArrayList<FileMsgInfo>();
				}
				fileMsgInfo.add(tempMsgInfo);
				break;
			}
			i++;
		}

	}

	public String getKey()
	{
		return user + ":" + host + ":" + no + ":" + command;
	}

	public byte[] getBytes()
	{
		packed();
		return pack;
	}

	public void setBytes(byte[] argpack)
	{
		pack = argpack;
		unpacked();
	}

	public void setVersion(String argver)
	{
		version = argver;
	}

	public String getVersion()
	{
		return version;
	}

	public String getNo()
	{
		return no;
	}

	public void setNo(String argno)
	{
		no = argno;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String arguser)
	{
		user = arguser;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String arghost)
	{
		host = arghost;
	}

	public void setCommand(long argcommand)
	{
		command = argcommand;
	}

	public long getCommand()
	{
		return command;
	}

	public String getExtra()
	{
		return extra;
	}

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String arggroup)
	{
		group = arggroup;
	}

	public void setExtra(String argextra)
	{
		extra = argextra;
	}

	public class FileMsgInfo
	{
		private String groupName;

		public String getGroupName()
		{
			return groupName;
		}

		public void setGroupName(String groupName)
		{
			this.groupName = groupName;
		}

		private String fileNo;
		private String fileName;
		private String fileSize;
		private String modifyTime;
		private String fileProperty;

		public String getFileNo()
		{
			return fileNo;
		}

		public String getFileName()
		{
			return fileName;
		}

		public String getFileSize()
		{
			return fileSize;
		}

		public String getModifyTime()
		{
			return modifyTime;
		}

		public String getFileProperty()
		{
			return fileProperty;
		}

		public void setFileNo(String fileNo)
		{
			this.fileNo = fileNo;
		}

		public void setFileName(String fileName)
		{
			this.fileName = fileName;
		}

		public void setFileSize(String fileSize)
		{
			this.fileSize = fileSize;
		}

		public void setModifyTime(String modifyTime)
		{
			this.modifyTime = modifyTime;
		}

		public void setFileProperty(String fileProperty)
		{
			this.fileProperty = fileProperty;
		}

	}

	public int getFromPort()
	{
		return fromPort;
	}

	public void setFromHost(InetAddress fromHost)
	{
		this.fromHost = fromHost;
	}

	public InetAddress getFromHost()
	{
		return fromHost;
	}

	public void setFromPort(int fromPort)
	{
		this.fromPort = fromPort;
	}

}
