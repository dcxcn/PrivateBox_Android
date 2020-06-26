package com.xunlei.aplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.aplayer.aplayerandroid.APlayerAndroid;
import com.xunlei.aplayer.model.FileItemInfo;
import com.xunlei.aplayer.ui.FileListItemView;
import com.xunlei.aplayer.util.TimeUtil;
import com.xunlei.aplayer.util.VideoFileHelper;
import cn.dcx.privatebox.R;
import java.util.ArrayList;
import java.util.List;

public class FilePickerListViewAdapter extends BaseAdapter{
	private Context context;
	private List<FileItemInfo> fileList;

	private static final int thumbnailWidth = 48;
	private static final int thumbnailHeight = 48;
	private static final int  snapsTimeMs = 20*1000;

	public FilePickerListViewAdapter(Context context, List<FileItemInfo> fileList) {
		this.context = context;
		this.fileList = fileList;
	}

	@Override
	public int getCount() {
		return this.fileList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new FileListItemView(context);
		}

		FileItemInfo fileInfo = fileList.get(position);
		FileListItemView fileListItemView = (FileListItemView)convertView;
		ImageView folderImageView = (ImageView)fileListItemView.findViewById(R.id.folderImageView);
		ImageView videoThumbnailImageView = (ImageView)fileListItemView.findViewById(R.id.videoThumbnailImageView);
		TextView folderNameTextView = (TextView)fileListItemView.findViewById(R.id.folderNameTextView);
		TextView fileNameTextView = (TextView)fileListItemView.findViewById(R.id.fileNameTextView);
		TextView videoDurationTextView = (TextView)fileListItemView.findViewById(R.id.durationTextView);
		TextView videoResolutionTextView = (TextView)fileListItemView.findViewById(R.id.resolutionTextView);

		if (fileInfo.isDirectory) {
			folderImageView.setVisibility(View.VISIBLE);
			folderNameTextView.setVisibility(View.VISIBLE);
			videoThumbnailImageView.setVisibility(View.GONE);
			fileNameTextView.setVisibility(View.GONE);
			videoDurationTextView.setVisibility(View.GONE);
			videoResolutionTextView.setVisibility(View.GONE);

			folderNameTextView.setText(fileInfo.name);
		}
		else {
			videoThumbnailImageView.setVisibility(View.VISIBLE);
			fileNameTextView.setVisibility(View.VISIBLE);
			folderImageView.setVisibility(View.GONE);
			folderNameTextView.setVisibility(View.GONE);
			videoDurationTextView.setVisibility(View.VISIBLE);
			videoResolutionTextView.setVisibility(View.VISIBLE);

			//set default val
			fileNameTextView.setText(fileInfo.name);
			videoThumbnailImageView.setImageResource(R.drawable.ic_document_file_48px);
			videoDurationTextView.setText("");
			videoResolutionTextView.setText("");

			int pos = fileInfo.name.lastIndexOf('.');
			String extensionName = (pos >= 0) ? fileInfo.name.substring(pos + 1) : null;
			APlayerAndroid.MediaInfo mediaInfo = fileInfo.mediaInfo;

			if(null != extensionName && VideoFileHelper.isSupportedVideoFileExtension(extensionName) && null != mediaInfo){
				if (mediaInfo.bitMap != null) {
					Bitmap bitmap = APlayerAndroid.MediaInfo.byteArray2BitMap(mediaInfo.bitMap);
					if(null != bitmap)
						videoThumbnailImageView.setImageBitmap(bitmap);
				}

				if (mediaInfo.duration_ms != 0) {
					final int MILLSECONDS_TIME_BASE = 1000;
					int timeSeconds = (int)(mediaInfo.duration_ms / MILLSECONDS_TIME_BASE);
					videoDurationTextView.setText(TimeUtil.formatFromSecond(timeSeconds));
				}

				String rational = String.format("%5d X %5d", mediaInfo.width, mediaInfo.height);
				videoResolutionTextView.setText(rational);
			}
			else
			{
				videoDurationTextView.setVisibility(View.GONE);
				videoResolutionTextView.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	public ArrayList<FileItemInfo> getVideoFileList() {
		return (ArrayList<FileItemInfo>)fileList;
	}
}
