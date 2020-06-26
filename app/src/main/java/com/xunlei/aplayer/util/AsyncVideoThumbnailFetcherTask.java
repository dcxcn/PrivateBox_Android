package com.xunlei.aplayer.util;

import java.lang.ref.WeakReference;

import com.aplayer.aplayerandroid.APlayerAndroid;
import com.xunlei.aplayer.model.FileItemInfo;
import com.xunlei.aplayer.model.ThumbnailParam;

import android.os.AsyncTask;

public class AsyncVideoThumbnailFetcherTask extends AsyncTask<FileItemInfo, Void, APlayerAndroid.MediaInfo> {
	private final WeakReference<VideoInfoLoader.VideoInfoSettable> listViewRef;
	private ThumbnailParam thumbnailParam;
	private static APlayerAndroid aPlayer = new APlayerAndroid();

	public AsyncVideoThumbnailFetcherTask(VideoInfoLoader.VideoInfoSettable listItemView, ThumbnailParam thumbnailParam) {

		this.thumbnailParam = thumbnailParam;
		listViewRef = new WeakReference<VideoInfoLoader.VideoInfoSettable>(listItemView);
	}

	@Override
	protected APlayerAndroid.MediaInfo doInBackground(FileItemInfo... params) {
		if(null == thumbnailParam || null == thumbnailParam.path || thumbnailParam.path.isEmpty())
			return null;
		APlayerAndroid.MediaInfo mediaInfo = aPlayer.parseThumbnail(thumbnailParam.path,
				thumbnailParam.snapsTimeMs, thumbnailParam.width, thumbnailParam.height);

		FileItemInfo fileItemInfo = params[0];
		fileItemInfo.mediaInfo = mediaInfo;
		return mediaInfo;
	}

	@Override
	protected void onPostExecute(APlayerAndroid.MediaInfo mediaInfo) {
		if (isCancelled()) {
			mediaInfo = null;
		}
		if (mediaInfo != null) {
			final VideoInfoLoader.VideoInfoSettable listView = listViewRef.get();
			if(null != listView){
				listView.refreshMediaInfo();
			}
		}
	}
}
