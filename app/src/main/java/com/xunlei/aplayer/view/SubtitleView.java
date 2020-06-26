package com.xunlei.aplayer.view;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import cn.dcx.privatebox.R;
import com.xunlei.aplayer.adapter.PopWndChoseListAdapter;
import com.xunlei.aplayer.model.Content;
import com.xunlei.aplayer.model.PopWndOptionsCategory;
import com.xunlei.aplayer.model.PopWndOptionsIteamParam;
import com.xunlei.aplayer.play_interface.IConfigSubtitle;
import com.xunlei.aplayer.ui.FilePickPopupWindow;
import com.xunlei.aplayer.util.ListViewUtility;
import com.xunlei.aplayer.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class SubtitleView extends LinearLayout {

    private Context mContext;
    private View mRootView;
    private View mParentView;
    private IConfigSubtitle mConfigSubtitle;
    private FilePickerView.FilePickListener mSubTitleFilePickListener;
    private String mDeafultSubtitleDirPath;
    private PopWndOptionsCategory mPopWndOptionsCategory = new PopWndOptionsCategory();

    private TextView mSubtitleUsableTitle;
    private TextView mSubtitleUsableVal;
    private Switch   mSubtitleShowSwitch;
    private TextView mSubtitleExternalVal;
    private TextView mSubtitlePathVal;
    private ListView mSubtitleListView;
    private FilePickPopupWindow mFilePickPopupWindow;

    private static final String ERROR_TAG = Content.APLAYER_DEMO_LOG_PREF_TAG + SubtitleView.class.getSimpleName();
    private static final String SUBTITLT_USABLE = "true";
    private static final String SUBTITLT_NOT_USABLE = "false";
    private static final String ROOT = Content.PATH_ROOT;

    private class SubtitleSet
    {
        public static final String CATEGORY_NAME = "Subtitle";
    }

    public SubtitleView(Context context, IConfigSubtitle configSubtitle, View parentView) {
        super(context);
        mParentView = parentView;
        mContext = context;
        mConfigSubtitle = configSubtitle;
        mDeafultSubtitleDirPath = getSubtitleDefaultDir();
        mRootView = LayoutInflater.from(context).inflate(R.layout.subtitle_set_view, this);
        setOrientation(VERTICAL);

        init();
        updateParam();
        registerListener();
        updateParam();
    }

    private void init()
    {

        mSubtitleUsableTitle = (TextView) findViewById(R.id.popwnd_subtitle_usable_title);
        mSubtitleUsableVal = (TextView) findViewById(R.id.popwnd_subtitle_usable_val);
        mSubtitleShowSwitch = (Switch) findViewById(R.id.popwnd_subtitle_show);
        mSubtitleExternalVal = (TextView) findViewById(R.id.popwnd_subtitl_ext_name);
        mSubtitlePathVal = (TextView) findViewById(R.id.popwnd_subtitl_path);
        mSubtitleListView = (ListView) findViewById(R.id.popwnd_subtitle_list);

        mSubTitleFilePickListener = new SubTitleFilePickListener();
        mFilePickPopupWindow = creatFilePickerPopWnd();
    }

    private void updateParam()
    {
        String strSubtitleUsable = mConfigSubtitle.isUsable() ? SUBTITLT_USABLE : SUBTITLT_NOT_USABLE;
        mSubtitleUsableVal.setText(strSubtitleUsable);
        mSubtitleShowSwitch.setChecked(mConfigSubtitle.isShow());
        mSubtitleExternalVal.setText(mConfigSubtitle.getExternalSupportType());
        mSubtitlePathVal.setText(mConfigSubtitle.getExternalSubtitlePath());

        List<String> subtitleList = mConfigSubtitle.getSubtitleList();
        int currentSelectPos = mConfigSubtitle.getCurrentSubtitlePos();
        PopWndOptionsCategory retCategory = subtitleListToCategory(subtitleList, currentSelectPos);
        mPopWndOptionsCategory.setCategory(retCategory);
        mSubtitleListView.setAdapter(new PopWndChoseListAdapter(mContext, mPopWndOptionsCategory));
        ListViewUtility.setListViewHeightBasedOnChildren(mSubtitleListView);
    }

    private void registerListener()
    {
        mSubtitleListView.setOnItemClickListener(new PopWndChoseListAdapter.IteamListener(mPopWndOptionsCategory,
                new PopWndChoseListAdapter.IteamListener.IteamClickListener()
                {
                    @Override
                    public void onItemClick(int pos, String strIteamTitle) {
                        if(!mConfigSubtitle.setCurrentSubtitle(pos)){
                            Log.e(ERROR_TAG, "Set Subtitle Failed!");
                            updateParam();
                        }
                    }
                }));

        mSubtitlePathVal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilePickWnd();
            }
        });

        mSubtitleShowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mConfigSubtitle.show(b);
                updateParam();
            }
        });
    }

    private FilePickPopupWindow creatFilePickerPopWnd()
    {
        UIUtil.Size scrrenSize = UIUtil.getScrrenResolutionSize(getContext());
        int height = scrrenSize.height;
        int width = scrrenSize.width;

        String msg = String.format("height = %d, wideght = %d", height, width);

        //屏幕可能旋转，取得较小的一边
        int length = (height > width) ? width : height;
        final int MARGIN = 200;
        length -= MARGIN;
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

        FilePickPopupWindow pickPopupWindow =  new FilePickPopupWindow(mSubTitleFilePickListener, mContext, length, length, mDeafultSubtitleDirPath);
        return pickPopupWindow;
    }

    private void showFilePickWnd()
    {
        if (null == mFilePickPopupWindow){
            mFilePickPopupWindow = creatFilePickerPopWnd();
        }

        mFilePickPopupWindow.showAtLocation(mParentView, Gravity.CENTER, 0, 0);
    }
    private static PopWndOptionsCategory subtitleListToCategory(List<String> subtitleList, int currentSelectPos)
    {
        List<PopWndOptionsIteamParam> ieamList = new ArrayList<PopWndOptionsIteamParam>();
        for(int i = 0; null != subtitleList && i < subtitleList.size(); ++i){
            boolean isSelected = (currentSelectPos == i);
            String subtitleName = subtitleList.get(i);
            PopWndOptionsIteamParam popWndOptionsIteamParam = new PopWndOptionsIteamParam(isSelected, true, subtitleName);
            ieamList.add(popWndOptionsIteamParam);
        }

        PopWndOptionsCategory popWndOptionsCategory = new PopWndOptionsCategory(SubtitleSet.CATEGORY_NAME, ieamList);
        return popWndOptionsCategory;
    }

    private String getSubtitleDefaultDir()
    {
        String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (null == defaultPath) {
            defaultPath = ROOT;
        }

        return defaultPath;
    }

    private class SubTitleFilePickListener implements FilePickerView.FilePickListener
    {

        @Override
        public void onSelected(String filePath, boolean isFile) {
            if(isFile) {
                mFilePickPopupWindow.dismiss();
                mFilePickPopupWindow = null;
                if(!mConfigSubtitle.setExternalSubtitlePath(filePath)) {
                    Log.e(ERROR_TAG, "Set External Subtitle Path Failed");
                }

                updateParam();
            }
            else
            {
                mDeafultSubtitleDirPath = filePath;
            }

        }
    }

}
