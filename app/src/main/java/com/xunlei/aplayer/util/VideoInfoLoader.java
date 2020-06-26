package com.xunlei.aplayer.util;

import android.graphics.Bitmap;

public class VideoInfoLoader {
	private static Bitmap getPlaceHolderBitmap() {
		int picw = 96;
		int pich = 72;
		Bitmap placeHolderBitmap = Bitmap.createBitmap(picw, pich, Bitmap.Config.ARGB_8888);
		int[] pix = new int[picw * pich];
		for (int y = 0; y < pich; y++) {
			for (int x = 0; x < picw; x++) {
				int index = y * picw + x;
			    pix[index] = 0xff000000;
			}
		}
		placeHolderBitmap.setPixels(pix, 0, picw, 0, 0, picw, pich);
		return placeHolderBitmap;
	}

//	public static void LoadVideoInfo(Context context, ThumbnailParam videoFileInfo, VideoInfoSettable listItemView) {
//		if (cancelPotentialWork(videoFileInfo, listItemView)) {
//			listItemView.setVideoName(videoFileInfo.name);
//			listItemView.setVideoDuration("--");
//			listItemView.setVideoResolution("--");
//			listItemView.setVideoThumbnail(getPlaceHolderBitmap());
//			AsyncVideoThumbnailFetcherTask task = new AsyncVideoThumbnailFetcherTask(context, listItemView, videoFileInfo);
//			listItemView.setLastTask(task);
//			task.execute();
//		}
//	}
//
//	private static boolean cancelPotentialWork(ThumbnailParam videoFileInfo, VideoInfoSettable listItemView) {
//		final AsyncVideoThumbnailFetcherTask fetcherTask = listItemView.getLastTask();
//		if (fetcherTask != null) {
//			String path = fetcherTask.getMediaPath();
//	        if (null == path || !path.equals(videoFileInfo.path)) {
//	        	fetcherTask.cancel(true);
//	        } else {
//	            return false;
//	        }
//		}
//		return true;
//	}
	
	public interface VideoInfoSettable {
		public void refreshMediaInfo();
	}
}
