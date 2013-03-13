package com.ipmsg.socket;

import ipmsg.IPMsg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import android.util.Log;

public class SocketSendFile extends Thread
{
	private String TAG = "SocketSendFile";
	private ServerSocket server;
	private IPMsg ipmsg;
	private boolean isStop = false;

	public SocketSendFile(ServerSocket server, IPMsg ipmsg)
	{

		this.server = server;
		this.ipmsg = ipmsg;
	}

	@Override
	public void run()
	{

		while (!isStop)
		{
			try
			{
				Socket socket = server.accept();
				new SendFile(socket).start();

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}

		}
	}

	class SendFile extends Thread
	{
		Socket socket = null;
		InputStream is = null;
		OutputStream os = null;
		byte[] but = new byte[400];

		public SendFile(Socket socket)
		{
			this.socket = socket;
		}

		@Override
		public void run()
		{
			try
			{
				StringBuffer sb = new StringBuffer();
				is = socket.getInputStream();
				os = socket.getOutputStream();

				int length = -1;
				while ((length = is.read(but)) != -1)
				{
					sb.append(new String(but, 0, length, "GBK"));
					if (sb.toString().indexOf("0:0:") != -1)
					{
						break;
					}
				}
				String str = sb.toString();
				StringTokenizer tokenizer = new StringTokenizer(str, ":", false);
				tokenizer.nextToken();
				tokenizer.nextToken();
				tokenizer.nextToken();
				tokenizer.nextToken();
				String command = tokenizer.nextToken();
				String no = tokenizer.nextToken();
				Log.d(TAG, "command :" + command);
				Log.d(TAG, "no :" + no);
				Log.d(TAG, "" + ipmsg.getFileSendList().size());
				if (ipmsg.getFileSendList().containsKey(no))
				{
					Log.d(TAG, "have");
					File file = ipmsg.getFileSendList().get(no);
					InputStream inputStream = new FileInputStream(file);
					length = -1;
					while ((length = inputStream.read(but)) != -1)
					{
						os.write(but, 0, length);
					}
					os.flush();

				}
				Log.d(TAG, str);
				Log.d(TAG, "complete");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally
			{

				try
				{
					if (is != null)
					{
						is.close();
					}
					if (socket != null)
					{
						socket.close();
					}

				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
