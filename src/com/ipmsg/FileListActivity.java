package com.ipmsg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class FileListActivity extends ListActivity
{
	private static final int DIALOG_LIST = 3;
	private List<String> items = null;
	private List<String> paths = null;
	private List<String> itemFiles = null;
	private List<String> pathFiles = null;
	private boolean isOpenFile = true;
	private boolean isOnlyDirBrowse = false;
	private boolean isSelectFile = false;
	private String rootPath = "";
	private String curPath = "";
	private TextView mPath;
	private int position = -1;

	@Override
	protected void onCreate(Bundle icicle)
	{

		super.onCreate(icicle);
		setContentView(R.layout.file_list);
		mPath = (TextView) findViewById(R.id.mPath);
		Intent intent = this.getIntent();
		isOpenFile = intent.getBooleanExtra("isOpenFile", true);
		isOnlyDirBrowse = intent.getBooleanExtra("isOnlyDirBrowse", false);
		isSelectFile = intent.getBooleanExtra("isSelectFile", false);
		rootPath = intent.getStringExtra("rootPath");
		if (rootPath == null || rootPath.equals(""))
		{
			rootPath = "/sdcard";
		}
		getFileDir(rootPath);
	}

	/* ȡ���ļ��ܹ��ķ��� */
	private void getFileDir(String filePath)
	{
		/* ����Ŀǰ����·�� */
		mPath.setText(filePath);
		curPath = filePath;
		File f = new File(filePath);
		File[] files = f.listFiles();
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		itemFiles = new ArrayList<String>();
		pathFiles = new ArrayList<String>();
		if (!filePath.equals(rootPath))
		{
			items.add("b1");
			paths.add(rootPath);
			items.add("b2");
			paths.add(f.getParent());
		}

		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			if (file.isDirectory())
			{
				items.add(file.getName());
				paths.add(file.getPath());

			} else
			{
				itemFiles.add(file.getName());
				pathFiles.add(file.getPath());
			}
		}

		if (!isOnlyDirBrowse)
		{
			items.addAll(itemFiles);
			paths.addAll(pathFiles);
		}
		setListAdapter(new FileListAdapter(this, items, paths));
	}

	/* ����ListItem�����ʱҪ���Ķ��� */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		this.position = position;
		if (isSelectFile)
		{
			File file = new File(paths.get(position));
			if (file.isDirectory())
			{
				/* ������ļ��о�������getFileDir() */
				getFileDir(paths.get(position));
			}else
			{
				result();
			}

		} else
		{
			showDialog(DIALOG_LIST);
		}

	}

	/* ���ֻ��ϴ��ļ��ķ��� */
	private void openFile(File f)
	{
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* ����getMIMEType()��ȡ��MimeType */
		String type = getMIMEType(f);
		/* ����intent��file��MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	/* �ж��ļ�MimeType�ķ��� */
	private String getMIMEType(File f)
	{
		String type = "";
		String fName = f.getName();
		/* ȡ����չ�� */
		String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

		/* ������������;���MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
				|| end.equals("wav"))
		{
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4"))
		{
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp"))
		{
			type = "image";
		} else
		{
			/* ����޷�ֱ�Ӵ򿪣����������б���û�ѡ�� */
			type = "*";
		}
		type += "/*";
		return type;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu)
	// {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.file_list_menu, menu);
	// return true;
	// }

	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{

			case DIALOG_LIST:
				CharSequence[] items = this.getResources().getTextArray(R.array.dialog_list_menu);

				return new AlertDialog.Builder(this).setItems(items, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						if (which == 0)
						{
							File file = new File(paths.get(position));
							if (file.isDirectory())
							{
								/* ������ļ��о�������getFileDir() */
								getFileDir(paths.get(position));
							} else
							{
								/* ������ļ�������openFile() */
								openFile(file);
							}
						} else if (which == 1)
						{
							if (position == -1)
							{
								return;
							}

							result();

						}
					}
				}).create();
		}

		return null;
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item)
	// {
	// // TODO Auto-generated method stub
	// switch (item.getItemId())
	// {
	// case (R.id.select):
	//                
	// break;
	//
	// }
	// return super.onOptionsItemSelected(item);
	// }

	private void result()
	{
		Intent intent = new Intent();
		// if (getSelectedItemPosition() == -1)
		// {
		// Toast.makeText(this, R.string.dir_empty_info,
		// Toast.LENGTH_LONG).show();
		// return super.onOptionsItemSelected(item);
		// }
		intent.putExtra("path", paths.get(position));
		this.setResult(RESULT_OK, intent);
		this.finish();

	}
}
