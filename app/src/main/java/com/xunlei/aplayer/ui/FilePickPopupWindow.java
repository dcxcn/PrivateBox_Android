package com.xunlei.aplayer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.dcx.privatebox.R;
import com.xunlei.aplayer.view.FilePickerView;

/**
 * Created by admin on 2016/7/19.
 */
public class FilePickPopupWindow extends PopupWindow {

    private Context mContent;
    private View mRootView = null;
    private TextView mTextViewCurrentDirParh = null;
    private LinearLayout mFileChoseContainer = null;   //显示文件选择器正文
    private String mCurDirPath;
    private FilePickerView mFilePickerView;
    private FilePickerView.FilePickListener listener;
    private PopWndFilePickListener mFilePickListener;

    public FilePickPopupWindow ( FilePickerView.FilePickListener listener, Context content,int width, int height, String deafultPath)
    {
        super(width, height);
        LayoutInflater inflater = LayoutInflater.from(content);
        mContent = content;
        mRootView = inflater.inflate(R.layout.popwnd_file_pick_layout, null);
        setContentView(mRootView);

        mCurDirPath = deafultPath;
        this.listener = listener;

        init();
        registerListener();
    }

    private void init()
    {
        mTextViewCurrentDirParh  = (TextView) mRootView.findViewById(R.id.popwnd_file_pick_title);
        mFileChoseContainer = (LinearLayout) mRootView.findViewById(R.id.popwnd_file_pick_details);
        mFilePickListener = new PopWndFilePickListener();
        mFilePickerView = new FilePickerView(mContent,mFilePickListener,mCurDirPath);
        mFilePickerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mFileChoseContainer.addView(mFilePickerView);

        mTextViewCurrentDirParh.setText(mCurDirPath);
    }

    private void registerListener()
    {
        //mFilePickListener = new PopWndFilePickListener();
        mTextViewCurrentDirParh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilePickPopupWindow.this.dismiss();
            }
        });
    }

    private class PopWndFilePickListener implements FilePickerView.FilePickListener
    {

        @Override
        public void onSelected(String filePath, boolean isFile) {
            setCurrentPath(filePath);
            if(isFile)
            {
                FilePickPopupWindow.this.dismiss();
            }

            if(null != listener)
            {
                listener.onSelected(filePath, isFile);
            }
        }
    }

    private void setCurrentPath(String path)
    {
        if(null != mTextViewCurrentDirParh)
        {
            mTextViewCurrentDirParh.setText(path);
        }
    }

}
