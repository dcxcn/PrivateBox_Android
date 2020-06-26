package com.xunlei.aplayer.view;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import cn.dcx.privatebox.R;
import com.xunlei.aplayer.adapter.FilePickerListViewAdapter;
import com.xunlei.aplayer.model.Content;
import com.xunlei.aplayer.model.FileItemInfo;
import com.xunlei.aplayer.model.ThumbnailParam;
import com.xunlei.aplayer.util.AsyncVideoThumbnailFetcherTask;
import com.xunlei.aplayer.util.VideoFileHelper;
import com.xunlei.aplayer.util.VideoInfoLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by admin on 2016/7/19.
 */
public class FilePickerView extends LinearLayout implements VideoInfoLoader.VideoInfoSettable {

    private static final String ERROR_TAG = Content.APLAYER_DEMO_LOG_PREF_TAG + FilePickerView.class.getSimpleName();
    private static final String WARE_TAG = ERROR_TAG;

    private Context mContext;
    private View mRootView;
    private View mParentFolderText;
    private FilePickListener mFilePickListener;
    private ListView mFileListView;
    private static final String ROOT = Content.PATH_ROOT;
    private String mCurDirPath = ROOT;

    public FilePickerView(Context context, FilePickListener filePickListener, String currentDir) {
        super(context);

        mContext = context;
        mFilePickListener = filePickListener;
        mRootView =  LayoutInflater.from(context).inflate(R.layout.file_picker, this);
        //mFileListView = (ListView) mRootView.findViewById(R.id.fileListView);

        mParentFolderText = findViewById(R.id.parentFolderText);
        mParentFolderText.setOnClickListener(mParentFolderTextOnClickListener);
        mParentFolderText.setVisibility(View.GONE);
        mFileListView = (ListView)findViewById(R.id.fileListView);
        mFileListView.setOnItemClickListener(mFileListViewOnItemClickListener);
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mCurDirPath = (null == currentDir) ? rootPath : currentDir;

        updateFileList(mCurDirPath);
    }

    private void refreshFileList()
    {
        FilePickerListViewAdapter filePickerListViewAdapter = (FilePickerListViewAdapter) mFileListView.getAdapter();
        filePickerListViewAdapter.notifyDataSetChanged();
    }

    private static boolean isVideoFile(String fileName)
    {
        int pos = fileName.lastIndexOf('.');
        String extensionName = (pos >= 0) ? fileName.substring(pos + 1) : null;
        return (null != extensionName && VideoFileHelper.isSupportedVideoFileExtension(extensionName));
    }
    public void updateFileList(String path) {
        mCurDirPath = path;
        notifySlectedChange(mCurDirPath, false);
        File dir = new File(path);
        if ((!dir.exists() || !dir.isDirectory()) && !path.equals(ROOT)) {
            updateFileList(ROOT);
            return;
        }
        if (dir.getParentFile() != null) {
            mParentFolderText.setVisibility(View.VISIBLE);
        }
        else {
            mParentFolderText.setVisibility(View.GONE);
        }
        ArrayList<FileItemInfo> fileList = new ArrayList<FileItemInfo>();
        ArrayList<FileItemInfo> dirList = new ArrayList<FileItemInfo>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : dir.listFiles()) {
                if (file.isHidden()) {
                    continue;
                }
                if (file.isDirectory()) {
                    FileItemInfo fileItem = new FileItemInfo();
                    fileItem.isDirectory = true;
                    fileItem.name = file.getName();
                    fileItem.path = file.getPath();
                    dirList.add(fileItem);
                }
                else {
                    FileItemInfo fileItem = new FileItemInfo();
                    fileItem.isDirectory = false;
                    fileItem.name = file.getName();
                    fileItem.path = file.getPath();
                    fileList.add(fileItem);

                    //异步加载缩略图
                    if(isVideoFile(fileItem.name)){
                        final int SNAPS_TIME_MS = 20*1000;
                        final int THUMBNAIL_WIDTH = 48;
                        final int THUMBNAIL_HEIGHT = 48;

                        ThumbnailParam thumbnailParam = new ThumbnailParam(fileItem.name, fileItem.path,
                                SNAPS_TIME_MS, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                        AsyncVideoThumbnailFetcherTask asyncVideoThumbnailFetcherTask = new AsyncVideoThumbnailFetcherTask(this, thumbnailParam);
                        asyncVideoThumbnailFetcherTask.execute(fileItem);
                    }
                }
            }
        }

        Comparator comp = new FileItemInfo.SortComparator();
        Collections.sort(dirList,comp);
        Collections.sort(fileList, comp);
        //fileList.addAll(0, dirList);
        fileList.addAll(dirList);

        FilePickerListViewAdapter adapter = new FilePickerListViewAdapter(mContext, fileList);
        mFileListView.setAdapter(adapter);
    }

    private final OnClickListener mParentFolderTextOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            File curPathDir = new File(mCurDirPath);
            if (!curPathDir.exists() || !curPathDir.isDirectory()) {
                updateFileList(ROOT);
                return;
            }
            File parentFile = curPathDir.getParentFile();
            if (parentFile != null) {
                updateFileList(parentFile.toString());
            }
        }
    };

    private final ListView.OnItemClickListener mFileListViewOnItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FilePickerListViewAdapter adapter = (FilePickerListViewAdapter)parent.getAdapter();
            ArrayList<FileItemInfo> fileList = adapter.getVideoFileList();
            FileItemInfo fileItemInfo = fileList.get(position);
            if (fileItemInfo.isDirectory) {
                updateFileList(fileItemInfo.path);
            }
            else {
                String videoFilePath = fileList.get(position).path;
                String videoFileName = fileList.get(position).name;

                notifySlectedChange(videoFilePath, true);
            }
        }
    };

    @Override
    public void refreshMediaInfo() {
        refreshFileList();
    }

    public interface FilePickListener
    {
        void onSelected(String filePath, boolean isFile);
    }

    public void notifySlectedChange(String filePath, boolean isFile)
    {
        if(null == mFilePickListener)
            return;

        mFilePickListener.onSelected(filePath, isFile);
    }
}
