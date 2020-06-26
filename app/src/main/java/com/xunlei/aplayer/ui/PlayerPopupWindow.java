package com.xunlei.aplayer.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import cn.dcx.privatebox.R;
import com.xunlei.aplayer.model.Content;
import com.xunlei.aplayer.model.PlayConfig;
import com.xunlei.aplayer.view.AudioSetView;
import com.xunlei.aplayer.view.RecordSetView;
import com.xunlei.aplayer.view.SubtitleView;
import com.xunlei.aplayer.view.VideoSetView;

/**
 * Created by admin on 2016/7/13.
 */
public class PlayerPopupWindow extends PopupWindow {

    private static final String ERROR_TAG = Content.APLAYER_DEMO_LOG_PREF_TAG + PlayerPopupWindow.class.getSimpleName();
    String[] mCatagoryArray = null;
    private View mVideoSetting = null;
    private View mAudioSetting = null;
    private View mSubtitleSetting = null;
    private View mRecordSetting = null;
    private View mCurrentShow = null;
    private FrameLayout mDetailsViewRoot = null;
    View mRootView = null;
    View mParentView = null;
    ListView mCategoryListView = null;
    PlayConfig mPlayConfig = null;
    String      mOriginalMediaPath = null;

    private class ResouseIndex
    {
        public static final int INDEX_VIDEO         = 0;
        public static final int INDEX_AUDIO         = 1;
        public static final int INDEX_SUBTITLE      = 2;
        //public static final int INDEX_VR            = 3;
        public static final int INDEX_RECORD        = 3;
    }

    public PlayerPopupWindow(PlayConfig playConfig, View parentView, String originalMediaPath, int width, int height) {
        super(width, height);
        mRootView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.popup, null);
        setContentView(mRootView);
        mPlayConfig = playConfig;
        mParentView = parentView;
        mOriginalMediaPath = originalMediaPath;
        mCategoryListView = (ListView)mRootView.findViewById(R.id.categoryListView);
        mDetailsViewRoot = (FrameLayout)mRootView.findViewById(R.id.set_detail_container);
        //mCatagoryArray = contentView.getResources().getStringArray(R.array.play_setting_category);



//        //mCategoryListView.setFocusable(true);
//        //setFocusable(true);
        mCategoryListView.setFocusable(true);
        //mCategoryListView.setItemsCanFocus(false);
        mCategoryListView.setItemsCanFocus(true);
//        //mCategoryListView.setFocusableInTouchMode(true);
//        mCategoryListView.setSelected(false);

        initCategoryArray();
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mRootView.getContext(),
//                android.R.spacer_medium.simple_list_item_1, mCatagoryArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mRootView.getContext(),
                R.layout.popwnd_category_item, mCatagoryArray);
        mCategoryListView.setAdapter(adapter);


        //mCategoryListView.setChoiceMode();

        initSetView();
        registerListener();

        //默认选中第一个
        if(mCategoryListView.getAdapter().getCount() > 0)
        {
            //mCategoryListView.
            //mCategoryListView.setSelection(0);
            mCategoryListView.performItemClick(this.getContentView(), 0, 0);
            mCategoryListView.setSelection(0);
            showVideoSetDetails();
        }

    }


    private void initCategoryArray()
    {
        mCatagoryArray = mRootView.getResources().getStringArray(R.array.play_setting_category);
    }
    private void initSetView()
    {
        mVideoSetting = new VideoSetView(mRootView.getContext(), mPlayConfig.getConfigVideo());
        mAudioSetting = new AudioSetView(mRootView.getContext(), mPlayConfig.getConfigAudio());
        mSubtitleSetting = new SubtitleView(mRootView.getContext(), mPlayConfig.getSubtitle(), mParentView);
        mRecordSetting  = new RecordSetView(mRootView.getContext(), mPlayConfig.geRecord(), mOriginalMediaPath);
    }

    private void registerListener()
    {
        mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int postion = i;
                switch (postion)
                {
                    case ResouseIndex.INDEX_VIDEO:
                        showVideoSetDetails();
                        break;
                    case ResouseIndex.INDEX_AUDIO:
                        showAudeoSetDetails();
                        break;
                    case ResouseIndex.INDEX_SUBTITLE:
                        showSubtitleSetDetails();
                        break;
//                    case ResouseIndex.INDEX_VR:
//                        break;
                    case ResouseIndex.INDEX_RECORD:
                        showRecordSetDetails();
                        break;
                    default:
                        Log.e(ERROR_TAG, "invalidate category index!");
                        break;
                }
            }
        });
    }

    private void showVideoSetDetails()
    {
        showSetView(mVideoSetting);
    }

    private void showAudeoSetDetails()
    {
        showSetView(mAudioSetting);
    }

    private void showSubtitleSetDetails()
    {
        showSetView(mSubtitleSetting);
    }

    private void showRecordSetDetails()
    {
        showSetView(mRecordSetting);
    }

    private void showSetView(View setingView)
    {
        if(mCurrentShow == setingView)
        {
            return;
        }

        mDetailsViewRoot.removeAllViews();
        mDetailsViewRoot.addView(setingView);
    }
}
