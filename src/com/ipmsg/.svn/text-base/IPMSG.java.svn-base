package com.ipmsg;

import ipmsg.IPMComEvent;
import ipmsg.IPMEvent;
import ipmsg.IPMListener;
import ipmsg.IPMPack;
import ipmsg.IPMsg;
import ipmsg.RecvFile;
import ipmsg.IPMPack.FileMsgInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import JP.digitune.util.Cp932;
import JP.digitune.util.SortVector;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipmsg.listener.ProgressListener;
import com.ipmsg.util.FileSizeUtil;

public class IPMSG extends ListActivity
{
	private String TAG = "ipmsg";
	public IPMsg ipmsg;
	private static final int DIALOG_PROGRESS = 4;
	private boolean refreshing = false, received = false;
	private Hashtable<String, IPMComEvent> NAMEtoINFO;
	private Hashtable ADDRtoINFO;
	private List memberlist;
	private LayoutInflater factory;
	private View receView;
	private Context context = null;
	private ProgressDialog mProgressDialog;

	final RecvFile recvFiled = new RecvFile();

	private Handler mHandler = new Handler()
	{

		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{

				case 1:
					// 接收信息
					IPMEvent ipme = (IPMEvent) msg.getData().get("ipme");
					IPMComEvent devents = (IPMComEvent) ADDRtoINFO.get(ipme.getIPMAddress().toString());
					IPMPack tmppackd = devents.getPack();
					String tmpstrw = Cp932.toCp932(ipmsg.makeUserStr(tmppackd));
					new AlertDialog.Builder(context).setTitle(tmpstrw).setMessage(ipme.getPack().getExtra())
							.setPositiveButton("确定", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{

									dialog.cancel();

								}
							})

							.show();
					break;
				case 2:
					// 接收文件
					final IPMEvent event = (IPMEvent) msg.getData().get("ipme");
					StringBuffer sb = new StringBuffer();
					if (event.getPack().getExtra() != null)
					{
						sb.append(event.getPack().getExtra());
						sb.append("\n\t");
					}
					for (FileMsgInfo fileMsgInfo : event.getPack().getFileMsgInfo())
					{
						String str = getString(R.string.rece_msg, fileMsgInfo.getFileName(), FileSizeUtil
								.toConvertMB(Integer.valueOf(fileMsgInfo.getFileSize(), 16)));
						sb.append(str);
					}
					SharedPreferences prefs = getPreferences(0);
					IPMComEvent devent = (IPMComEvent) ADDRtoINFO.get(event.getIPMAddress().toString());
					IPMPack tmppack = devent.getPack();
					String tmpstr = Cp932.toCp932(ipmsg.makeUserStr(tmppack));
					String path = prefs.getString("path", "");
					receView = factory.inflate(R.layout.alert_rece_message, null);
					final EditText editText = (EditText) receView.findViewById(R.id.file_store_path_edit);
					editText.setText(path);
					Button button = (Button) receView.findViewById(R.id.browse);
					button.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							Intent intent = new Intent();
							intent.setClass(IPMSG.this, FileListActivity.class);
							Bundle bundle = new Bundle();
							bundle.putBoolean("isOnlyDirBrowse", true);
							intent.putExtras(bundle);
							IPMSG.this.startActivityForResult(intent, 0);
						}
					});

					new AlertDialog.Builder(IPMSG.this).setTitle(tmpstr).setMessage(sb.toString()).setView(receView)
							.setNeutralButton("确定", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{
									IPMSG.this.showDialog(DIALOG_PROGRESS);

									recvFiled.recv(event.getPack(), ipmsg.getUser(), ipmsg.getHost(), null, editText
											.getText().toString());
								}
							}).setNegativeButton("关闭", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{

								}
							}).show();
					break;

				case 4:
					// 重名
					final IPMEvent renameIpme = (IPMEvent) msg.getData().get("ipme");

					final View textEntryView = factory.inflate(R.layout.alert_rename_message, null);

					new AlertDialog.Builder(IPMSG.this).setView(textEntryView).setNeutralButton("确定",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{
									// EditText editText = (EditText)
									// textEntryView.findViewById(R.id.fileName);

									// recvFiled.recv(renameIpme.getPack(),
									// ipmsg.getUser(), ipmsg.getHost(),
									// editText
									// .getText().toString());
								}
							})

					.setNegativeButton("关闭", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int whichButton)
						{
							dialog.cancel();
						}
					})

					.show();
					break;
				case 5:
					double value = msg.getData().getDouble("process");
					mProgressDialog.setProgress((int) value);
					break;
				case 6:
					mProgressDialog.cancel();
					break;
				case 7:
					String message = msg.getData().getString("msg");
					mProgressDialog.setMessage(message);
				default:
					synchronized (this)
					{
						setListAdapter(new EfficientAdapter(IPMSG.this));
						break;
					}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		switch (resultCode)
		{

			case RESULT_OK:
				/* 取得来自Activity2的数据，并显示于画面上 */
				Bundle bunde = data.getExtras();
				String path = bunde.getString("path");
				if (path != null && !path.equals(""))
				{
					EditText editText = (EditText) receView.findViewById(R.id.file_store_path_edit);
					editText.setText(path);
					Editor editor = getPreferences(0).edit();
					editor.putString("path", path);
					editor.commit();
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SharedPreferences prefs = getPreferences(0);
		String user = prefs.getString("userName", "administrator");
		String group = prefs.getString("group", "no group");
		String host = prefs.getString("host", "localhost");
		ipmsg = IPMsg.getInstance(user);
		ipmsg.setGroup(group);
		ipmsg.setHost(host);
		ipmsg.addIPMListener(new IPMListener()
		{
			public void eventOccured(IPMEvent ipme)
			{
				processEvent(ipme);
			}
		});

		String sdcardState = Environment.getExternalStorageState();
		if (sdcardState.equals(Environment.MEDIA_SHARED))
		{
			Toast.makeText(this, R.string.media_mounted, Toast.LENGTH_LONG);
			return;
		}
		context=this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		memberlist = new ArrayList();
		// createWindow();
		ipmsg.entry();
		recvFiled.setProgressListener(new ProgressListener()
		{
			@Override
			public void dismiss()
			{

				mHandler.sendEmptyMessage(6);
			}

			@Override
			public void fail(String message)
			{

				mHandler.sendEmptyMessage(6);
			}

			@Override
			public void onSuccess()
			{

				mHandler.sendEmptyMessage(6);
			}

			@Override
			public void setMessage(String message)
			{

				Message msg = new Message();
				msg.what = 7;
				Bundle bundle = new Bundle();
				bundle.putString("msg", message);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}

			@Override
			public void setPercent(double percent)
			{
				Message msg = new Message();
				msg.what = 5;
				Bundle bundle = new Bundle();

				bundle.putDouble("process", percent);
				msg.setData(bundle);
				mHandler.sendMessage(msg);

			}

			@Override
			public void onStart()
			{

			}

			@Override
			public void openDir(String path)
			{
				Intent intent = new Intent();
				intent.setClass(IPMSG.this, FileListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("rootPath", path);
				intent.putExtras(bundle);
				IPMSG.this.startActivityForResult(intent, 0);
			}
		});
		factory = LayoutInflater.from(IPMSG.this);

	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{

			case DIALOG_PROGRESS:
				mProgressDialog = new ProgressDialog(IPMSG.this);
				// mProgressDialog.setIcon(R.drawable.alert_dialog_icon);
				mProgressDialog.setTitle(R.string.receing);
				mProgressDialog.setMax(100);
				// mProgressDialog.setMessage("测试");
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

				mProgressDialog.setButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{

							}
						});

				return mProgressDialog;

		}
		return null;
	}

	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id)
	{
		Intent intent = new Intent();
		intent.setClass(IPMSG.this, SendActivity.class);
		Bundle bundle = new Bundle();
		IPMComEvent com = NAMEtoINFO.get(memberlist.get(position));
		bundle.putSerializable("ipMAddress", com.getIPMAddress());
		intent.putExtras(bundle);

		IPMSG.this.startActivity(intent);
	}

	private class EfficientAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;

		public EfficientAdapter(Context context)
		{
			mInflater = LayoutInflater.from(context);
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount()
		{
			return memberlist.size();
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position)
		{
			return position;
		}

		/**
		 * Use the array index as a unique id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position)
		{
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null)
			{
				convertView = mInflater.inflate(R.layout.fuunction_list_item_icon_text, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				convertView.setTag(holder);
			} else
			{
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.

			holder.text.setText((CharSequence) memberlist.get(position));

			// holder.icon.setImageBitmap(mIcon[(position) % 3]);

			return convertView;
		}

		class ViewHolder
		{
			TextView text;
			ImageView icon;
		}
	}

	synchronized void processEvent(IPMEvent ipme)
	{
		// Log.e("IPMSG", ipme.getID() + "");

		Message message = new Message();
		Bundle bundle = new Bundle();
		switch (ipme.getID())
		{
			case IPMEvent.UPDATELIST_EVENT:

				synchronized (memberlist)
				{
					// received = true;
					if (!refreshing)
					{
						// refreshing = true;
						NAMEtoINFO = new Hashtable<String, IPMComEvent>();
						ADDRtoINFO = new Hashtable();

						memberlist = new ArrayList();
						Enumeration members = ipmsg.getUserlist().elements();
						SortVector tmpvec = new SortVector();
						while (members.hasMoreElements())
						{
							IPMComEvent tmpevent = (IPMComEvent) members.nextElement();
							tmpvec.addElement(makeSortKey(tmpevent), tmpevent);
						}
						members = tmpvec.elements();
						while (members.hasMoreElements())
						{
							IPMComEvent tmpevent = (IPMComEvent) members.nextElement();
							IPMPack tmppack = tmpevent.getPack();
							// if (tmppack.getGroup() != null &&
							// groupcache.get(tmppack.getGroup()) == null)
							// {
							// groups.addItem(Cp932.toCp932(tmppack.getGroup()));
							// groupcache.put(tmppack.getGroup(),
							// tmppack.getGroup());
							// }
							// if (!tmpgroup.equals(ipmsg.getPref("allName")))
							// if (tmppack.getGroup() == null)
							// continue;
							// else if (!tmppack.getGroup().equals(tmpgroup))
							// continue;
							String tmpstr = Cp932.toCp932(ipmsg.makeListStr(tmppack));
							if (!NAMEtoINFO.containsKey(tmpstr))
							{
								memberlist.add(tmpstr);
								NAMEtoINFO.put(tmpstr, tmpevent);
								ADDRtoINFO.put(tmpevent.getIPMAddress().toString(), tmpevent);
							}

						}
						// new RefreshList().start();
						mHandler.sendEmptyMessage(0);
					}
				}
				break;
			case IPMEvent.RECEIVEMSG_EVENT:
				if (!ipmsg.lessThanReceiveMax())
				{
					System.err.println("too many receive dialog.");
					break;
				}
				ipmsg.incReceiveCount();
				// getToolkit().beep();
				// RecvDlg rd = new RecvDlg(this, ipmsg, (IPMComEvent)
				// ADDRtoINFO.get(ipme.getIPMAddress().toString()), ipme);
				// rd.setVisible(true);

				bundle.putSerializable("ipme", ipme);
				message.setData(bundle);
				message.what = 1;
				mHandler.sendMessage(message);
				break;
			case IPMEvent.RECEIVEFILEMSG_EVENT:
				if (!ipmsg.lessThanReceiveMax())
				{
					System.err.println("too many receive dialog.");
					break;
				}
				ipmsg.incReceiveCount();

				bundle.putSerializable("ipme", ipme);
				message.setData(bundle);
				// File file = new File(IPMsg.downloadDir +
				// ipme.getPack().getFileMsgInfo().get(0).getFileName());
				// if (file.exists())
				// {
				// message.what = 4;
				// } else
				// {
				message.what = 2;
				// }
				mHandler.sendMessage(message);
				break;

			case IPMEvent.READMSG_EVENT:
				// /getToolkit().beep();
				String tmpname = "";
				IPMComEvent tmpipmce = (IPMComEvent) ADDRtoINFO.get(ipme.getIPMAddress().toString());
				IPMPack tmppack;
				if (tmpipmce != null)
				{
					tmppack = tmpipmce.getPack();
					tmpname = ipmsg.makeListStr(tmppack);
				} else
					tmpname = ipme.getPack().getUser();
				StringBuffer strbuf = new StringBuffer();
				strbuf.append(ipmsg.getPref("readMsg") + "\n");
				strbuf.append(ipmsg.makeDateStr(ipme.getDate()));
				// /MsgBox mb = new MsgBox(this, Cp932.toCp932(tmpname),
				// Cp932.toCp932(new String(strbuf)), false);
				// mb.setVisible(true);
				break;
			case IPMEvent.DELETEMSG_EVENT:
				// getToolkit().beep();
				tmpname = "";
				tmpipmce = (IPMComEvent) ADDRtoINFO.get(ipme.getIPMAddress().toString());
				if (tmpipmce != null)
				{
					tmppack = tmpipmce.getPack();
					tmpname = ipmsg.makeListStr(tmppack);
				} else
					tmpname = ipme.getPack().getUser();
				strbuf = new StringBuffer();
				strbuf.append(ipmsg.getPref("deleteMsg") + "\n");
				strbuf.append(ipmsg.makeDateStr(ipme.getDate()));
				// mb = new MsgBox(this, Cp932.toCp932(tmpname),
				// Cp932.toCp932(new
				// String(strbuf)), false);
				// mb.setVisible(true);
				break;
			case IPMEvent.CANTSENDMSG_EVENT:
				// RetryDlg retry = new RetryDlg(this, ipmsg.getPref("appName"),
				// ipmsg.getPref("retryMsg"), ipmsg, ipme);
				// retry.setVisible(true);
				break;
		}
	}

	String makeSortKey(IPMComEvent ipmce)
	{
		String tmpkey = ipmsg.getPref("sortKey");
		StringBuffer strbuf = new StringBuffer();
		for (int i = 0; i < tmpkey.length(); i++)
			switch (tmpkey.charAt(i))
			{
				case 'u':
					String tmpuser;
					if (ipmce.getPack().getExtra() == null)
						tmpuser = ipmce.getPack().getUser();
					else
						tmpuser = ipmce.getPack().getExtra();
					strbuf.append(tmpuser);
					break;
				case 'g':
					if (ipmce.getPack().getGroup() != null)
						strbuf.append(ipmce.getPack().getGroup());
					else
						strbuf.append("  ");
					break;
				case 'h':
					strbuf.append(ipmce.getPack().getHost());
					break;
			}
		return new String(strbuf);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ipmsg_menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.refresh:
				ipmsg.refreshList();
				return true;

			case R.id.exit:
				ipmsg.exit();
				System.exit(0);
				return true;

			case R.id.setting:
				SharedPreferences prefer = getPreferences(0);
				View dreceView = factory.inflate(R.layout.ipmsg_setting, null);
				final EditText userEdit = (EditText) dreceView.findViewById(R.id.ipmsg_username_edit);
				userEdit.setText(prefer.getString("userName", "administrator"));
				final EditText groupEdit = (EditText) dreceView.findViewById(R.id.ipmsg_group_edit);
				groupEdit.setText(prefer.getString("group", "no group"));
				final EditText hostEdit = (EditText) dreceView.findViewById(R.id.ipmsg_host_edit);
				hostEdit.setText(prefer.getString("host", "localhost"));

				new AlertDialog.Builder(IPMSG.this).setTitle(R.string.setting).setView(dreceView).setNeutralButton(
						R.string.ok, new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								Editor editor = getPreferences(0).edit();
								editor.putString("userName", userEdit.getText().toString());
								editor.putString("group", groupEdit.getText().toString());
								editor.putString("host", hostEdit.getText().toString());
								ipmsg.setUser(userEdit.getText().toString());
								ipmsg.setGroup(groupEdit.getText().toString());
								ipmsg.setHost(hostEdit.getText().toString());
								editor.commit();
								ipmsg.refreshList();
							}
						}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{

					}
				}).show();
				break;

		}

		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_ENDCALL)
		{
			ipmsg.exit();

			onDestroy();
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}
}