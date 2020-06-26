package com.xunlei.aplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.xunlei.aplayer.model.Content;
import com.xunlei.aplayer.view.FilePickerView;

import cn.dcx.privatebox.Manifest;
import cn.dcx.privatebox.R;
public class FilePickerActivity extends AppCompatActivity {
	private static final String DEBUG_TAG = "FilePickerActivity";
	
	private static final String CUR_DIR_PATH = "CurDirPath";
	private String mCurDirPath;

	private MediaFilePickListener mMediaFilePickListener;
	private FilePickerView mFilePickerView;
	private ActionBar mActionBar;

	public FilePickerActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(DEBUG_TAG, "onCreate");
		super.onCreate(savedInstanceState);

		mActionBar = getSupportActionBar();
		mCurDirPath = getDefaultDir(savedInstanceState);
		mMediaFilePickListener = new MediaFilePickListener();
		mFilePickerView = new FilePickerView(this, mMediaFilePickListener, mCurDirPath);
		setContentView(mFilePickerView);

		requestPermission();
		Log.e("mCurDirPath = ",mCurDirPath);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putString(CUR_DIR_PATH, mCurDirPath);
	    super.onSaveInstanceState(savedInstanceState);
		Log.e(DEBUG_TAG, "Save State");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activy_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.file_refresh:
				mFilePickerView.updateFileList(mCurDirPath);
				Toast.makeText(this, "Update:" + mCurDirPath, Toast.LENGTH_SHORT).show();
				break;
			case R.id.player_setting:
				//Intent intent = new Intent(this, SettingsActivity.class);
				Intent intent = new Intent(this, Setting.class);
				startActivity(intent);
				Toast.makeText(this, "Setting:", Toast.LENGTH_SHORT).show();
				break;
			case R.id.open_url:
				inputUrl();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}

		return true;
	}

	private void showCrrentDirPath(String path)
	{
		mActionBar.setTitle(path);
	}
	private void startPlayer(String mediaFilePath)
	{
		//APlayerParam aPlayerParam = new APlayerParam(mediaFilePath, null);
		Intent intent = new Intent(this, PlayerActivity.class);
		//Log.e(DEBUG_TAG, Uri.encode(mediaFilePath));
		intent.setData(Uri.parse(mediaFilePath));
		//intent.putExtra(Content.MEDIA_FILE_PATH, mediaFilePath);
		startActivity(intent);
	}

	private String getDefaultDir(Bundle savedInstanceState)
	{
		String defaultPath;
		if (savedInstanceState != null) {
			defaultPath = savedInstanceState.getString(CUR_DIR_PATH, Content.PATH_ROOT);
		}
		else
		{
			defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		}

		return defaultPath;
	}

	public void inputUrl()
	{
		//装载/res/layout/login.xml界面布局
		final LinearLayout urlInputForm = (LinearLayout)getLayoutInflater()
				.inflate( R.layout.url_input_layout, null);
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.url_address_24px)
				.setTitle("URL")
				.setView(urlInputForm)
				.setPositiveButton("OK" , new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText urlEditText = (EditText) urlInputForm.findViewById(R.id.alter_dlg_url);
						String urlPath = urlEditText.getText().toString();
						if(null != urlPath && !urlPath.isEmpty())
							startPlayer(urlPath);
					}
				})
				.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				})
				.create()
				.show();
	}

	private class MediaFilePickListener implements FilePickerView.FilePickListener
	{

		@Override
		public void onSelected(String filePath, boolean isFile) {
			if(isFile)
			{
				startPlayer(filePath);
			}
			else
			{
				mCurDirPath = filePath;
				showCrrentDirPath(filePath);
			}
		}
	}

	public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;

	public void requestPermission(){
		//判断当前Activity是否已经获得了该权限
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			//如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				Toast.makeText(this,"please give me the permission",Toast.LENGTH_SHORT).show();
			} else {
				//进行权限请求
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						EXTERNAL_STORAGE_REQ_CODE);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case EXTERNAL_STORAGE_REQ_CODE:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
					mFilePickerView.updateFileList(mCurDirPath);

				} else {
					// Permission Denied
					Toast.makeText(FilePickerActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

}
