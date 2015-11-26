package com.xyw.android.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.xyw.android.adapter.FileAdapter;
import com.xyw.android.utils.FileUtil;
import com.xyw.android.utils.ViewUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private TextView tv_title, tv_copy, tv_cut, tv_paste, tv_delete, tv_exit;
	private ListView lv_fileName;
	private LinearLayout ll_file_menu;
	private String filePath = null;
	private File currentFile;
	private SharedPreferences sp;
	private Editor editor;
	private File[] files;
	private File selectedFile;
	private File selectedPosition;
	private List<File> filesInCurrentDir = new ArrayList<File>();
	private FileAdapter fileNameAdapter;
	private boolean isCopy = false;
	private boolean isCut = false;
	private boolean pasteEnable = false;
	private ProgressDialog deleteDialog = null, pasteDialog = null;

	private static final int UPDATE_FILE_LIST = 1;
	private static final int UPDATE_CURRENT_PATH = 2;
	private static final int DELETE_COMPLETED = 3;
	private static final int PASTE_COMPLETED = 4;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case UPDATE_FILE_LIST:
				tv_title.setText(currentFile.getAbsolutePath());
				break;

			case UPDATE_CURRENT_PATH:
				tv_title.setText(currentFile.getAbsolutePath());
				break;
			case DELETE_COMPLETED:
				filesInCurrentDir.remove(selectedPosition);
				fileNameAdapter.notifyDataSetChanged();
				ll_file_menu.setVisibility(View.GONE);
				if (deleteDialog != null) {
					deleteDialog.hide();
					deleteDialog = null;
				}
				Toast.makeText(MainActivity.this, selectedFile.getName() + "已删除", Toast.LENGTH_LONG).show();
				break;
			case PASTE_COMPLETED:
				Log.i("xyw", "粘贴完毕");
				filesInCurrentDir.add(new File(currentFile.getAbsoluteFile() + "/" + selectedFile.getName()));
				fileNameAdapter.notifyDataSetChanged();
				ll_file_menu.setVisibility(View.GONE);
				isCopy = false;
				isCut = false;
				pasteEnable = false;
				tv_paste.setEnabled(pasteEnable);
				if (pasteDialog != null) {
					pasteDialog.hide();
					pasteDialog = null;
				}
				Toast.makeText(MainActivity.this, "已粘贴", Toast.LENGTH_SHORT).show();
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		ViewUtil.initView(MainActivity.this);
		sp = getSharedPreferences("File", Activity.MODE_PRIVATE);
		editor = sp.edit();
		filePath = sp.getString("CurrentPath", null);
		if ((filePath == null) && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			filePath = getPackageResourcePath();
		}
		currentFile = new File(filePath);
		filesInCurrentDir = getFilesFromPath(currentFile);
		fileNameAdapter = new FileAdapter(MainActivity.this, filesInCurrentDir);
		tv_title.setText(filePath);
		tv_copy.setOnClickListener(this);
		tv_cut.setOnClickListener(this);
		tv_paste.setOnClickListener(this);
		tv_delete.setOnClickListener(this);
		tv_paste.setEnabled(pasteEnable);
		tv_exit.setOnClickListener(this);
		lv_fileName.setAdapter(fileNameAdapter);
		lv_fileName.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				ll_file_menu.setVisibility(View.VISIBLE);
				selectedFile = filesInCurrentDir.get(position);
				Log.i("xyw", selectedFile.getName());
				return true;
			}
		});
		lv_fileName.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				// if (filesInCurrentDir.get(position).isDirectory()
				// && !filesInCurrentDir.get(position).getName().equals("..")) {
				// currentFile =
				// filesInCurrentDir.get(position).getAbsoluteFile();
				// filesInCurrentDir.clear();
				// files = currentFile.listFiles();
				// filesInCurrentDir.add(new File(".."));
				// for (File file : files) {
				// filesInCurrentDir.add(file);
				// }
				// } else {
				// if (position == 0 && !currentFile.getName().equals("/")) {
				// currentFile = new
				// File(filesInCurrentDir.get(position).getParent());
				// filesInCurrentDir.clear();
				// files = currentFile.listFiles();
				// filesInCurrentDir.add(new File(".."));
				// for (File file : files) {
				// filesInCurrentDir.add(file);
				// }
				// }
				// }
				if (filesInCurrentDir.get(position).isDirectory()
						&& !filesInCurrentDir.get(position).getName().equals("..")) {
					currentFile = filesInCurrentDir.get(position);
					filesInCurrentDir = getFilesFromPath(currentFile);
					fileNameAdapter = (FileAdapter) lv_fileName.getAdapter();
					fileNameAdapter.setData(filesInCurrentDir);
				} else if (filesInCurrentDir.get(position).getName().equals("..")) {
					filesInCurrentDir = getFilesFromPath(new File(currentFile.getParent()));
					fileNameAdapter = (FileAdapter) lv_fileName.getAdapter();
					fileNameAdapter.setData(filesInCurrentDir);
					currentFile = new File(currentFile.getParent());
				}

				fileNameAdapter.notifyDataSetChanged();
				Message msg = new Message();
				msg.what = UPDATE_FILE_LIST;
				handler.sendMessage(msg);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (currentFile.getParent() != null) {
				Log.i("xyw", currentFile.getParent());
				currentFile = new File(currentFile.getParent());
				filesInCurrentDir.clear();
				files = currentFile.listFiles();
				filesInCurrentDir.add(new File(".."));
				for (File file : files) {
					filesInCurrentDir.add(file);
				}
				fileNameAdapter = (FileAdapter) lv_fileName.getAdapter();
				fileNameAdapter.notifyDataSetChanged();
				Message msg = new Message();
				msg.what = UPDATE_CURRENT_PATH;
				handler.sendMessage(msg);
				return false;
			}
		case KeyEvent.KEYCODE_MENU:
			ll_file_menu.setVisibility(View.VISIBLE);
			return false;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_copy:
			if (isCut) {
				isCut = false;
			}
			Toast.makeText(MainActivity.this, "已复制", Toast.LENGTH_SHORT).show();
			isCopy = true;
			pasteEnable = true;
			tv_paste.setEnabled(pasteEnable);
			break;
		case R.id.tv_cut:
			if (isCopy) {
				isCopy = false;
			}
			Toast.makeText(MainActivity.this, "已剪切", Toast.LENGTH_SHORT).show();
			isCopy = false;
			pasteEnable = true;
			tv_paste.setEnabled(pasteEnable);
			break;
		case R.id.tv_paste:
			if (isCopy) {
				new CopyThread(selectedFile, currentFile).start();
				pasteDialog = ProgressDialog.show(this, "", "复制中...");
			} else if (isCut) {
				new CutThread(selectedFile, currentFile).start();
				pasteDialog = ProgressDialog.show(this, "", "粘贴中...");
			}
			break;
		case R.id.tv_delete:
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("警告！！！");
			builder.setMessage("是否删除？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					new DeleteThread(selectedFile).start();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}

			});
			builder.create().show();
			ll_file_menu.setVisibility(View.GONE);
			break;
		case R.id.tv_exit:
			finish();
			break;
		}
	}

	private List<File> getFilesFromPath(File file) {
		List<File> files = new ArrayList<File>();
		File[] fileInPath = file.listFiles();
		if (file.getParent() != null) {

			files.add(new File(".."));
			if (fileInPath != null) {
				for (File tmp : fileInPath) {
					files.add(tmp);
				}
			}
		} else {
			if (fileInPath != null) {
				for (File tmp : fileInPath) {
					files.add(tmp);
				}
			}
		}
		return files;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		editor.putString("CurrentPath", currentFile.getAbsolutePath());
		editor.commit();
		Log.i("xyw", currentFile.getAbsolutePath());
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public class CopyThread extends Thread {

		File src;
		File des;

		public CopyThread(File src, File des) {
			super();
			this.src = src;
			this.des = des;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("xyw", "paste" + des.getAbsolutePath());
			if (src.isDirectory()) {
				FileUtil.copyFolder(src, des);
			} else {
				FileUtil.copyFile(src, des);
			}
			Message msg = new Message();
			msg.what = PASTE_COMPLETED;
			handler.sendMessage(msg);
		}

	}

	public class CutThread extends Thread {

		File src = null;
		File des = null;

		public CutThread(File src, File des) {
			super();
			this.src = src;
			this.des = des;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (src.isDirectory()) {
				FileUtil.copyFolder(src, des);
				FileUtil.deleteFolder(src);
			} else {
				FileUtil.copyFile(src, des);
				FileUtil.deleteFile(src);
			}
			Message msg = new Message();
			msg.what = PASTE_COMPLETED;
			handler.sendMessage(msg);
		}

	}

	public class DeleteThread extends Thread {

		File src = null;

		public DeleteThread(File src) {
			super();
			this.src = src;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (src.isDirectory()) {
				FileUtil.deleteFolder(src);
			} else {
				FileUtil.deleteFile(src);
			}
			Message msg = new Message();
			msg.what = DELETE_COMPLETED;
			handler.sendMessage(msg);
		}

	}

}
