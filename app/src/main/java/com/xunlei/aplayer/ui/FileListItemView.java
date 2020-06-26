package com.xunlei.aplayer.ui;

import android.content.Context;
import android.widget.RelativeLayout;

import cn.dcx.privatebox.R;
import com.xunlei.aplayer.util.AsyncVideoThumbnailFetcherTask;

import java.lang.ref.WeakReference;

public class FileListItemView extends RelativeLayout{
	private WeakReference<AsyncVideoThumbnailFetcherTask> taskRef;

	public FileListItemView(Context context) {
		super(context);
		super.inflate(context, R.layout.file_list_item, this);
	}
}
