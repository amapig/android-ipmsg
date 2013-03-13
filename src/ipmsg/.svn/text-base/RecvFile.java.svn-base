package ipmsg;

import ipmsg.IPMPack.FileMsgInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ListIterator;
import java.util.Stack;
import java.util.StringTokenizer;

import android.util.Log;

import com.ipmsg.listener.ProgressListener;
import com.ipmsg.model.FileDirInfo;
import com.ipmsg.util.FileSizeUtil;

public class RecvFile
{
	private String tag = "RecvFile";
	private ProgressListener progressListener = null;
	private boolean isStop = false;

	public void recv(IPMPack udpPack, String user, String host, String rename,String path)
	{
		if (udpPack.getFileMsgInfo() != null && udpPack.getFileMsgInfo().size() > 0)
		{
			isStop = false;
			// Socket socket;

			// socket = new Socket(udpPack.getFromHost(),
			// udpPack.getFromPort());
			char c='/';
			if(!(path.indexOf(path.length()-1)==c))
			{
				path+='/';
			}
			File fileDir = new File(path);
			if (!fileDir.exists())
			{
				fileDir.mkdirs();
			}
			File file = null;
			if (rename == null || rename.equals(""))
			{
				file = new File(path + udpPack.getFileMsgInfo().get(0).getFileName());
			} else
			{
				file = new File(path+ rename);
			}

			ReciFileThread reciFileThread = new ReciFileThread(udpPack, user, host,path);
			// ReciFileThread reciFileThread = new ReciFileThread(socket,
			// tcpIPMPack, file, Integer.parseInt(tmppack
			// .getFileMsgInfo().get(0).getFileSize(), 16));
			reciFileThread.start();

		}
	}

	public ProgressListener getProgressListener()
	{
		return progressListener;
	}

	public void setProgressListener(ProgressListener progressListener)
	{
		this.progressListener = progressListener;
	}

	public class ReciFileThread extends Thread
	{
		private Socket socket;

		private IPMPack udpPack;
		private int fileSize;
		private String user;
		private String host;
		private String path;

		private byte[] buf = new byte[300];
		private File file;

		ReciFileThread(IPMPack udpPack, String user, String host,String path)
		{
			this.user = user;
			this.host = host;
			this.udpPack = udpPack;
			this.path=path;
		}

		public void run()
		{
			OutputStream fileOutputStream = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;

			for (FileMsgInfo fileMsgInfo : udpPack.getFileMsgInfo())
			{
				try
				{
					String hex = Long.toHexString(Long.valueOf(udpPack.getNo()).longValue());
					IPMPack tcpIPMPack = new IPMPack("1", String.valueOf(System.currentTimeMillis()), user, host,
							fileMsgInfo.getFileProperty().equals("1") ? IPMsg.IPMSG_GETFILEDATA
									: IPMsg.IPMSG_GETDIRFILES, hex + ":" + fileMsgInfo.getFileNo() + ":0:", null);
					Log.d(tag, "port:"+udpPack.getFromPort());
					socket = new Socket(udpPack.getFromHost(), udpPack.getFromPort());
					inputStream = socket.getInputStream();
					outputStream = socket.getOutputStream();
					outputStream.write(tcpIPMPack.getBytes());
					outputStream.flush();
					int rLength = 0;

					if (fileMsgInfo.getFileProperty().equals("1"))
					{
						int allow = 0;
						int skip = 0;
						int lastPercent = 0;
						int percent = 0;
						file = new File(path + fileMsgInfo.getFileName());
						if (progressListener != null)
						{
							String sizeMb = FileSizeUtil.toConvertMB(fileSize);
							progressListener.setMessage("正在传输文件:" + file.getName() + " 大小:" + sizeMb);
							progressListener.setPercent(0);
						}

						fileSize = Integer.valueOf(fileMsgInfo.getFileSize(), 16);
						// 普通文件接收
						while (allow < fileSize && ((rLength = inputStream.read(buf)) != -1))
						{
							if (fileOutputStream == null)
							{
								if (!file.exists())
								{
									file.createNewFile();
								}
								fileOutputStream = new FileOutputStream(file);
							}
							fileOutputStream.write(buf, 0, rLength);
							fileOutputStream.flush();
							allow += rLength;
							percent = (int) (((double) allow / fileSize) * 100);
							if (progressListener != null && (percent != lastPercent))
							{
								progressListener.setPercent(percent);
								lastPercent = percent;
								Log.d(tag, "recv file rLength:" + rLength);
								Log.d(tag, "recv file percent:" + percent);
							}
							if (isStop)
							{
								break;
							}
							skip++;
						}
					} else
					{
						Stack<String> stack = new Stack<String>();
						fileSize = Integer.parseInt(fileMsgInfo.getFileSize(), 16);
						file = new File(path + fileMsgInfo.getFileName());
						stack.push(path);
						if (fileSize == 0 && !file.exists())
						{
							file.mkdirs();
						}
						FileDirInfo rootDirInfo=new FileDirInfo();
						boolean isStop=false;
						while(!isStop)
						{
							isStop=readDir(stack, inputStream, rootDirInfo);
						}
						//readDir(stack, inputStream, null);

					}
					if (fileOutputStream != null)
						fileOutputStream.flush();
					Log.e(tag, "recv file complete1");
				} catch (IOException e)
				{
					e.printStackTrace();
					Log.e(tag, "recv file fail");
					progressListener.fail(e.getMessage());
				} finally
				{
					Log.e(tag, "recv file finally");
					try
					{

						outputStream.close();
						inputStream.close();
						if (socket != null)
							socket.close();
						if (fileOutputStream != null)
							fileOutputStream.close();
						if (isStop && file.exists())
						{
							file.delete();
						}
						fileOutputStream = null;
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			if (progressListener != null)
			{
				progressListener.onSuccess();
				progressListener.openDir(path);
			}
			Log.e(tag, "recv file complete2");

		}
	}

	public boolean readDir(Stack<String> stack, InputStream inputStream, FileDirInfo rootDirInfo)
	{
		boolean isReturn = false;
		byte[] bufHeadLength = new byte[4];
		int packLength = 0;
		FileOutputStream outputStream = null;
		try
		{
			int rlength = inputStream.read(bufHeadLength);
			String bufHeadStr = new String(bufHeadLength);
			if (rlength == -1)
			{
				return true;
			} else
			{
				packLength = Integer.valueOf(bufHeadStr, 16);
			}
			byte[] headByte = new byte[packLength - 4];
			rlength = inputStream.read(headByte);
			if (rlength == -1)
			{
				return true;
			}
			String headStr = bufHeadStr + new String(headByte,"GBK");
			StringTokenizer tokenizer = new StringTokenizer(headStr, ":", false);
			int i = 0;
			FileDirInfo tempFileDirInfo = null;
			while (tokenizer.hasMoreTokens())
			{
				String str = tokenizer.nextToken();

				switch (i)
				{
				case 0:
					tempFileDirInfo = new FileDirInfo();
					tempFileDirInfo.setPackLenth(packLength);
					break;
				case 1:
					tempFileDirInfo.setName(str);
					break;
				case 2:
					tempFileDirInfo.setSize(Integer.valueOf(str, 16));
					break;
				case 3:
					tempFileDirInfo.setProperty(str);
					break;
				case 4:
					tempFileDirInfo.setProperty14(str);
					break;
				case 5:
					tempFileDirInfo.setProperty16(str);
					break;
				}
				i++;
			}
			if (rootDirInfo.getProperty14() == null&&rootDirInfo.getProperty16() == null)
			{
				rootDirInfo.setProperty14(tempFileDirInfo.getProperty14()) ;
				rootDirInfo.setProperty16(tempFileDirInfo.getProperty16()) ;
			} else
			{
				if (tempFileDirInfo.getProperty14().equals(rootDirInfo.getProperty14())
						&& tempFileDirInfo.getProperty16().equals(rootDirInfo.getProperty16()))
				{
					isReturn = true;
				}
			}
			if (tempFileDirInfo.getProperty().equals("3"))
			{
				stack.pop();
				if (isReturn)
				{
					return true;
				}
				//readDir(stack, inputStream, rootDirInfo);
				return false;
			} else if (tempFileDirInfo.getProperty().equals("2"))
			{
				stack.push(tempFileDirInfo.getName());
			}

			StringBuffer sb = new StringBuffer();
			ListIterator<String> ite = stack.listIterator();
			while (ite.hasNext())
			{
				String str = ite.next();
				sb.append(str);
				sb.append("/");
			}

			if (tempFileDirInfo.getProperty().equals("2"))
			{
				File file = new File(sb.toString());

				if (!file.exists())
				{
					file.mkdirs();
				}

			} else if (tempFileDirInfo.getProperty().equals("1"))
			{
				File file = new File(sb.toString() + tempFileDirInfo.getName());
				if (!file.exists())
				{
					file.createNewFile();
				}
				outputStream = new FileOutputStream(file);
				//一个一个字节读取太慢了
//				for (i = 0; i < tempFileDirInfo.getSize(); i++)
//				{
//					int c;
//					try
//					{
//						if ((c = inputStream.read()) == -1)
//						{
//							break;
//						}
//						outputStream.write(c);
//						Log.e(tag, String.valueOf(c));
//						if (i % 100 == 0)
//						{
//							outputStream.flush();
//						}
//					} catch (IOException e)
//					{
//
//						Log.e(tag, e.getMessage());
//					}
//
//				}
				
				byte[] buf=new byte[800];
				int length=tempFileDirInfo.getSize();
				rlength=0;
				int bufLength=length>buf.length?buf.length:length;
				while(bufLength!=0&&(rlength=inputStream.read(buf, 0, bufLength))!=-1)
				{
					length-=rlength;
					bufLength=length>buf.length?buf.length:length;
					outputStream.write(buf,0,rlength);
					//Log.e(tag, String.valueOf(rlength));
					outputStream.flush();
				}
				outputStream.flush();
			}
			if (isReturn)
			{
				return true;
			}
			//readDir(stack, inputStream, rootDirInfo);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		} finally
		{
			try
			{
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isStop()
	{
		return isStop;
	}

	public void setStop(boolean isStop)
	{
		this.isStop = isStop;
	}
}
