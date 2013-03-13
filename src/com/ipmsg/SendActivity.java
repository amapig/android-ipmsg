package com.ipmsg;

import ipmsg.IPMAddress;
import ipmsg.IPMsg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author 刘宗安
 * @MSN liuzongan1985@gmail.com
 * @QQ 104981067
 * @EMAIL liuzongan1985@163.com
 */
public class SendActivity extends Activity
{
	IPMsg ipMsg;
	Button addFile;
	Button cancel;
	Button send;
	EditText editText;
	List<String> fileList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ipMsg = IPMsg.getInstance(null);
		setContentView(R.layout.alert_send_message);
		editText = (EditText) this.findViewById(R.id.edit);
		addFile = (Button) this.findViewById(R.id.add_file);
		addFile.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// InputMethodManager imm =
				// (InputMethodManager)getSystemService(SendActivity.this.INPUT_METHOD_SERVICE);
				// imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				// imm.showSoftInput(editText, 0);
				Intent intent = new Intent();
				intent.setClass(SendActivity.this, FileListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("isSelectFile", true);
				intent.putExtras(bundle);
				SendActivity.this.startActivityForResult(intent, 0);
			}
		});
		cancel = (Button) this.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				SendActivity.this.finish();
			}
		});
		send = (Button) this.findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				IPMAddress[] addrs = new IPMAddress[1];
				addrs[0] = (IPMAddress) SendActivity.this.getIntent().getSerializableExtra("ipMAddress");
				if (fileList.size() > 0)
				{
					StringBuffer sb = new StringBuffer();
					byte[] d = new byte[]
					{ 0x00, 0x30 };

					sb.append(new String(d));
					sb.append(":");
					File file = new File(fileList.get(0));
					FileInputStream fileInput = null;
					try
					{
						fileInput = new FileInputStream(file);
						sb.append(file.getName());
						sb.append(":");
						sb.append("0");
						sb.append(Integer.toHexString(fileInput.available()));
						sb.append(":");
						sb.append(Long.toHexString(file.lastModified()));
						sb.append(":");
						sb.append("1");
						sb.append(":");
						sb.append(new String(new byte[]
						{ 0x07 }));
					} catch (FileNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally
					{
						if (fileInput != null)
						{
							try
							{
								fileInput.close();
							} catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					String serial = ipMsg.getSerial();
					
					ipMsg.addFileSend(Long.toHexString(Integer.valueOf(serial)), file);
					ipMsg.sendMsg(addrs, sb.toString(), IPMsg.IPMSG_FILEATTACHOPT, serial);

				} else
				{
					ipMsg.sendMsg(addrs, editText.getText().toString(), 0L, null);
				}
				SendActivity.this.finish();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		switch (resultCode)
		{

		case RESULT_OK:
			/* 取得来自Activity2的数据，并显示于画面上 */
			Bundle bunde = data.getExtras();
			String path = bunde.getString("path");
			if(fileList.size()>0)
			{
				 Toast.makeText(this, getString(R.string.file_only_on), Toast.LENGTH_LONG).show();
				 return ;
			}
			if (path != null && !path.equals(""))
			{
				fileList.add(path);
			    Toast.makeText(this, getString(R.string.file_add), Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}

}
