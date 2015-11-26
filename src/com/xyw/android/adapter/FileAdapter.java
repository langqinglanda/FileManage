package com.xyw.android.adapter;

import java.io.File;
import java.util.List;

import com.xyw.android.filemanager.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {

	List<File> data;
	Context context;

	public FileAdapter(Context context, List<File> data) {
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		Drawable drawable = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_file, null, false);
			viewHolder.fileName = (TextView) convertView.findViewById(R.id.tv_fileName);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.fileName.setText(data.get(position).getName());
		if (data.get(position).isDirectory()) {
			drawable = context.getResources().getDrawable(R.drawable.folder);
		} else {
			drawable = context.getResources().getDrawable(R.drawable.document);
		}
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		viewHolder.fileName.setCompoundDrawables(drawable, null, null, null);
		viewHolder.fileName.setCompoundDrawablePadding(5);
		return convertView;
	}

	public List<File> getData() {
		return data;
	}

	public void setData(List<File> data) {
		this.data = data;
	}

	private final class ViewHolder {
		TextView fileName;
	}

}
