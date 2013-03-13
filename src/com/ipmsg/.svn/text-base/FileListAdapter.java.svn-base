package com.ipmsg;

/* import相关class */
import java.io.File;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/* 自定义的Adapter，继承android.widget.BaseAdapter */
public class FileListAdapter extends BaseAdapter
{
	/*
	 * 变量声明 mIcon1：回到根目录的图文件 mIcon2：回到上一层的图档 mIcon3：文件夹的图文件 mIcon4：文件的图档
	 */
	private LayoutInflater mInflater;
	private Bitmap mIcon1;
	private Bitmap mIcon2;
	private Bitmap mIcon3;
	private Bitmap mIcon4;
	private Bitmap mp3;
	private Bitmap pdf;
	private Bitmap doc;
	private List<String> items;
	private List<String> paths;

	/* MyAdapter的构造器，传入三个参数 */
	public FileListAdapter(Context context, List<String> it, List<String> pa)
	{
		/* 参数初始化 */
		mInflater = LayoutInflater.from(context);
		items = it;
		paths = pa;
		mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.back01);
		mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.back02);
		mIcon3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
		mIcon4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.news);
		pdf = BitmapFactory.decodeResource(context.getResources(), R.drawable.pdf);
		doc = BitmapFactory.decodeResource(context.getResources(), R.drawable.doc);
		mp3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mp3);
	}

	/* 因继承BaseAdapter，需覆盖以下方法 */
	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public Object getItem(int position)
	{
		return items.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if (convertView == null)
		{
			/* 使用自定义的file_row作为Layout */
			convertView = mInflater.inflate(R.layout.file_row, null);
			/* 初始化holder的text与icon */
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		File f = new File(paths.get(position).toString());
		/* 设置[回到根目录]的文字与icon */
		if (items.get(position).toString().equals("b1"))
		{
			holder.text.setText("Back to /");
			holder.icon.setImageBitmap(mIcon1);
		}
		/* 设置[回到上一层]的文字与icon */
		else if (items.get(position).toString().equals("b2"))
		{
			holder.text.setText("Back to ..");
			holder.icon.setImageBitmap(mIcon2);
		}
		/* 设置[文件或文件夹]的文字与icon */
		else
		{
			holder.text.setText(f.getName());
			if (f.isDirectory())
			{
				holder.icon.setImageBitmap(mIcon3);
			} else
			{
				String fName = f.getName();
				/* 取得扩展名 */
				String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

				if (end.equalsIgnoreCase("mp3"))
				{
					holder.icon.setImageBitmap(mp3);
				} else if (end.equalsIgnoreCase("doc"))
				{
					holder.icon.setImageBitmap(doc);
				} else if (end.equalsIgnoreCase("pdf"))
				{
					holder.icon.setImageBitmap(pdf);
				} else
				{
					holder.icon.setImageBitmap(mIcon4);

				}
			}
		}
		return convertView;
	}

	/* class ViewHolder */
	private class ViewHolder
	{
		TextView text;
		ImageView icon;
	}
}
