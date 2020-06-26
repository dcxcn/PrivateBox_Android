package com.xunlei.aplayer.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cn.dcx.privatebox.R;
import com.xunlei.aplayer.adapter.PopWndChoseListAdapter;
import com.xunlei.aplayer.model.Content;
import com.xunlei.aplayer.model.PopWndOptionsCategory;
import com.xunlei.aplayer.model.PopWndOptionsIteamParam;
import com.xunlei.aplayer.play_interface.IConfigVideo;
import com.xunlei.aplayer.play_interface.IConfigVideo.AspectRatioType;
import com.xunlei.aplayer.util.ListViewUtility;
import com.xunlei.aplayer.util.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: document your custom view class.
 */
public class VideoSetView extends LinearLayout {

    public static class DecodeSet
    {
        public static String CATEGORY_NAME;
        public static String HARDWARE_DECODE;
        public static String SOFTWARE_DECODE;
    }

    private static class PictureRatioSet
    {
        public static String CATEGORY_NAME;
        public static String RATION_FULL_SCREEN;
        public static String RATIO_NATIVE;
    }

    private class PictureRatioSetCustom
    {
        public static final String RATIO_1_TO_1 = "1;1";
        public static final String RATIO_5_TO_4 = "5;4";
        public static final String RATIO_4_TO_3 = "4;3";
        public static final String RATIO_16_TO_10 = "16;10";
        public static final String RATIO_16_TO_9 = "16;9";
        public static final String RATIO_2_TO_1 = "2;1";
        public static final String RATIO_21_TO_9 = "21;9";
        public static final String RATIO_64_TO_27 = "64;27";
    }

    private class PictureRatioSetParam
    {
        public IConfigVideo.AspectRatioType    aspectRatioType;
        public String                          customRatioParam;
    }

    private static final String ERROR_TAG = Content.APLAYER_DEMO_LOG_PREF_TAG + VideoSetView.class.getSimpleName();
    private static final String WARE_TAG = ERROR_TAG;

    private Context                                         mContext;
    private View                                            mRootView;
    private IConfigVideo                                    mConfigVideo;
    private TextView                                        mDecoderCategory = null;
    private ListView                                        mDecoderListView = null;
    private TextView                                        mPictureRatioCategory = null;
    private ListView                                        mPictureRationListView = null;
    private PopWndOptionsCategory                           mDecoderParam = new PopWndOptionsCategory();
    private PopWndOptionsCategory                           mPictureRatioParam = new PopWndOptionsCategory();
    private Map<IConfigVideo.AspectRatioType, String>       mAspectRatioMap =  new LinkedHashMap<AspectRatioType, String>();
    private Map<String, String>                             mCustomRatioMap =  new LinkedHashMap<String, String>();
    private static  DecodeSet                               mDecodeSet = null;


    public VideoSetView(Context context, IConfigVideo configVideo) {
        super(context);
        mConfigVideo = configVideo;
        mContext = context;

        mRootView = LayoutInflater.from(context).inflate(R.layout.video_set_view, this);
        setOrientation(VERTICAL);

        mDecodeSet = getVideoDecodeSet(mRootView);
        getVideoRatioSet();
        initPictureRatioMap();
        init();
        updateParam();
        registerListener();
    }

    private void init()
    {
        mDecoderCategory = (TextView) mRootView.findViewById(R.id.popwnd_video_decoder);
        mPictureRatioCategory = (TextView) mRootView.findViewById(R.id.popwnd_video_picture_ratio);
        mDecoderListView = (ListView)mRootView.findViewById(R.id.popwnd_video_decoder_list);
        mPictureRationListView = (ListView)mRootView.findViewById(R.id.popwnd_video_picture_list);
    }

    private void updateParam()
    {
        PopWndOptionsCategory decoderParam = getDeocderCategory();
        PopWndOptionsCategory pictureRatioParam = getPictureRatio();
        if(null != decoderParam){
            mDecoderParam.setCategory(decoderParam);
        }
        if(null != pictureRatioParam){
            mPictureRatioParam.setCategory(pictureRatioParam);
        }

        mDecoderCategory.setText(mDecoderParam.getCategoryTitle());
        mPictureRatioCategory.setText(mPictureRatioParam.getCategoryTitle());

        mDecoderListView.setAdapter(new PopWndChoseListAdapter(mContext, mDecoderParam));
        mPictureRationListView.setAdapter(new PopWndChoseListAdapter(mContext, mPictureRatioParam));

        ListViewUtility.setListViewHeightBasedOnChildren(mDecoderListView);
        ListViewUtility.setListViewHeightBasedOnChildren(mPictureRationListView);
    }

    static public DecodeSet getVideoDecodeSet(View anyAppliactionView)
    {
        DecodeSet decodeSet = new DecodeSet();

        decodeSet.CATEGORY_NAME =   anyAppliactionView.getResources().getString(R.string.POPWND_VIDEO_DECODE);
        decodeSet.HARDWARE_DECODE = anyAppliactionView.getResources().getString(R.string.POPWND_VIDEO_DECODE_HARDWARE);
        decodeSet.SOFTWARE_DECODE = anyAppliactionView.getResources().getString(R.string.POPWND_VIDEO_DECODE_SOFTWARE);

        return decodeSet;
    }

    private void getVideoRatioSet()
    {
        PictureRatioSet.CATEGORY_NAME = mRootView.getResources().getString(R.string.POPWND_VIDEO_RATIO);
        PictureRatioSet.RATION_FULL_SCREEN = mRootView.getResources().getString(R.string.POPWND_VIDEO_RATIO_FULL_SCREEN);
        PictureRatioSet.RATIO_NATIVE = mRootView.getResources().getString(R.string.POPWND_VIDEO_RATIO_NATIVE);
    }

    void initPictureRatioMap()
    {
        //first init Genaral Ratio Map
        mAspectRatioMap.put(IConfigVideo.AspectRatioType.RationFullScreen, PictureRatioSet.RATION_FULL_SCREEN);
        mAspectRatioMap.put(IConfigVideo.AspectRatioType.RatioNative, PictureRatioSet.RATIO_NATIVE);

        //Sencod init Custom  Ratio Map
        mCustomRatioMap.put(PictureRatioSetCustom.RATIO_1_TO_1, customRatioParamToShowTitle(PictureRatioSetCustom.RATIO_1_TO_1));
        mCustomRatioMap.put(PictureRatioSetCustom.RATIO_5_TO_4, customRatioParamToShowTitle(PictureRatioSetCustom.RATIO_5_TO_4));
        mCustomRatioMap.put(PictureRatioSetCustom.RATIO_4_TO_3, customRatioParamToShowTitle(PictureRatioSetCustom.RATIO_4_TO_3));
        mCustomRatioMap.put(PictureRatioSetCustom.RATIO_16_TO_10, customRatioParamToShowTitle(PictureRatioSetCustom.RATIO_16_TO_10));
        mCustomRatioMap.put(PictureRatioSetCustom.RATIO_16_TO_9, customRatioParamToShowTitle(PictureRatioSetCustom.RATIO_16_TO_9));
        mCustomRatioMap.put(PictureRatioSetCustom.RATIO_2_TO_1, customRatioParamToShowTitle(PictureRatioSetCustom.RATIO_2_TO_1));
        mCustomRatioMap.put(PictureRatioSetCustom.RATIO_21_TO_9, customRatioParamToShowTitle(PictureRatioSetCustom.RATIO_21_TO_9));
        mCustomRatioMap.put(PictureRatioSetCustom.RATIO_64_TO_27, customRatioParamToShowTitle(PictureRatioSetCustom.RATIO_64_TO_27));
    }

    private static String customRatioParamToShowTitle(String strCustomRatio)
    {
        String title = strCustomRatio.replace(";", ":");
        return title;
    }
    private PopWndOptionsCategory getDeocderCategory()
    {
        boolean isEnableHardWareDecode = mConfigVideo.isEnableHardWareDecode();
        boolean isUseHardWareDecode = mConfigVideo.isUseHardWareDecode();

        List<PopWndOptionsIteamParam> IteamList = new ArrayList<PopWndOptionsIteamParam>();
        PopWndOptionsIteamParam hardWareDecode = new PopWndOptionsIteamParam(isUseHardWareDecode,
                        isEnableHardWareDecode, DecodeSet.HARDWARE_DECODE);
        PopWndOptionsIteamParam softWareDecode = new PopWndOptionsIteamParam(!isUseHardWareDecode,
                true, DecodeSet.SOFTWARE_DECODE);
        IteamList.add(hardWareDecode);
        IteamList.add(softWareDecode);

        PopWndOptionsCategory popWndOptionsCategory = new PopWndOptionsCategory(DecodeSet.CATEGORY_NAME, IteamList);
        return popWndOptionsCategory;
    }


    private PopWndOptionsCategory getPictureRatio()
    {
        List<PopWndOptionsIteamParam> IteamList = new ArrayList<PopWndOptionsIteamParam>();
        IConfigVideo.AspectRatioType aspectRatioType = mConfigVideo.getAspectRatioType();
        String customRatioParam = mConfigVideo.getAspectRatioCustom();  //notice, this param is effictive, only AspectRatioType == RatioCustom

        for(String title : mAspectRatioMap.values())
        {
            boolean isSelected = isIteamSelected(title, aspectRatioType, customRatioParam);
            PopWndOptionsIteamParam aspectRatioIteamParam = new PopWndOptionsIteamParam(isSelected, true, title);
            IteamList.add(aspectRatioIteamParam);
        }

        for(String title : mCustomRatioMap.values())
        {
            boolean isSelected = isIteamSelected(title, aspectRatioType, customRatioParam);
            PopWndOptionsIteamParam aspectRatioIteamParam = new PopWndOptionsIteamParam(isSelected, true, title);
            IteamList.add(aspectRatioIteamParam);
        }

        PopWndOptionsCategory popWndOptionsCategory = new PopWndOptionsCategory(PictureRatioSet.CATEGORY_NAME, IteamList);
        return popWndOptionsCategory;
    }

    private String aspectRatioTypeToIteamTitle(IConfigVideo.AspectRatioType aspectRatioType, String customRatioParam)
    {
        String iteamTitle = "";
        switch (aspectRatioType)
        {
            case RationFullScreen:
            case RatioNative:
                iteamTitle = mAspectRatioMap.get(aspectRatioType);
                break;
            case RatioCustom:
                iteamTitle = mCustomRatioMap.get(customRatioParam);
                break;
            default:
                break;
        }

        return iteamTitle;
    }

    private boolean isIteamSelected(String iteamTitle, IConfigVideo.AspectRatioType aspectRatioType, String customRatioParam)
    {
        if(null == iteamTitle)
        {
            return false;
        }

        String iteamSelectdTitle = aspectRatioTypeToIteamTitle(aspectRatioType, customRatioParam);
        return iteamSelectdTitle.equalsIgnoreCase(iteamTitle);
    }

    private void registerListener() {
        mDecoderListView.setOnItemClickListener(new PopWndChoseListAdapter.IteamListener(mDecoderParam,
                new PopWndChoseListAdapter.IteamListener.IteamClickListener() {
                    @Override
                    public void onItemClick(int pos, String strIteamTitle) {
                        setDecoder(strIteamTitle);
                    }
                }));

        mPictureRationListView.setOnItemClickListener(new PopWndChoseListAdapter.IteamListener(mPictureRatioParam,
                new PopWndChoseListAdapter.IteamListener.IteamClickListener() {

                    @Override
                    public void onItemClick(int pos, String strIteamTitle) {
                        setPictureRation(strIteamTitle);
                    }
                }
        ));
    }

    //private
    private class VideoDecoderSwitch implements IConfigVideo.IStopCompletCallback
    {
        private boolean mIsUseHardWareDecode = false;
        private IConfigVideo mConfig = null;
        private int mStartPostionMs = 0;    //开始播放的时间位置
        public VideoDecoderSwitch(IConfigVideo configVideo, boolean isUseHardWareDecode, int startPostionMs) {
            mIsUseHardWareDecode = isUseHardWareDecode;
            mConfig = configVideo;
            mStartPostionMs = startPostionMs;
        }

        @Override
        public void onStopComplet() {
            if(!mConfig.useHardWareDecode(mIsUseHardWareDecode))
            {
                Log.e(ERROR_TAG, "Change Decoder Failed!");
                return;
            }

            View displayView = mConfig.getDisplayView();
            do
            {
                //软硬解切换，让view 重绘
                if(null != displayView) {
                    displayView.setVisibility(View.GONE);
                }

                if(!mConfig.open())
                {
                    Log.e(ERROR_TAG, "Player open Failed!");
                    break;
                }

//            //when player reopen, reload param an update ui
//            VideoSetView.this.post(new Runnable() {
//                @Override
//                public void run() {
//                    updateParam();
//                }
//            });
                if(!mConfig.play())
                {
                    Log.e(ERROR_TAG, "Player play Failed!");
                    break;
                }

                if(!mConfig.setPlayPostion(mStartPostionMs))
                {
                    Log.e(ERROR_TAG, "Set play Postion Failed!");
                   break;
                }
            }while (false);

            if(null != displayView) {
                displayView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setDecoder(String chose)
    {
        boolean isUseHardWareDecode = (DecodeSet.HARDWARE_DECODE.compareToIgnoreCase(chose) == 0);
        boolean isUseSoftWareDecode = (DecodeSet.SOFTWARE_DECODE.compareToIgnoreCase(chose) == 0);

        if(!isUseHardWareDecode && !isUseSoftWareDecode)
        {
            Log.e(ERROR_TAG, "Decoder chose param is invalidate!");
            assert (false);
        }

        if(isUseHardWareDecode == mConfigVideo.isUseHardWareDecode())
        {
            //状态未改变
            return;
        }

        int playPostionMs = mConfigVideo.getPlayPostion();
        playPostionMs = playPostionMs > 0 ? playPostionMs : 0;
        IConfigVideo.IStopCompletCallback stopCompletCallback =
                new VideoDecoderSwitch(mConfigVideo, isUseHardWareDecode, playPostionMs);
        mConfigVideo.stopPlayer(stopCompletCallback, true);
    }

    private PictureRatioSetParam pictureRatioTitleToSetParam(String title)
    {
        PictureRatioSetParam pictureRatioSetParam = null;

        for(Map.Entry<IConfigVideo.AspectRatioType, String> entry : mAspectRatioMap.entrySet())
        {
            String val = entry.getValue();
            if(StringUtil.equalsIgnoreCase(val, title))
            {
                pictureRatioSetParam = new PictureRatioSetParam();
                pictureRatioSetParam.aspectRatioType= entry.getKey();
                break;
            }
        }
        if(null != pictureRatioSetParam)
        {
            return pictureRatioSetParam;
        }

        for(Map.Entry<String, String> entry : mCustomRatioMap.entrySet())
        {
            String val = entry.getValue();
            if(StringUtil.equalsIgnoreCase(val, title))
            {
                pictureRatioSetParam = new PictureRatioSetParam();
                pictureRatioSetParam.aspectRatioType = IConfigVideo.AspectRatioType.RatioCustom;
                pictureRatioSetParam.customRatioParam = entry.getKey();
                break;
            }
        }

        return pictureRatioSetParam;
    }

    private void setPictureRation(String choseIteamTitle)
    {
        PictureRatioSetParam pictureRatioSetParam = pictureRatioTitleToSetParam(choseIteamTitle);
        if(null == pictureRatioSetParam)
        {
            Log.e(ERROR_TAG, "invalidate picture ration iteam title");
            return;
        }

        IConfigVideo.AspectRatioType aspectRatioType = pictureRatioSetParam.aspectRatioType;
        String setParam = pictureRatioSetParam.customRatioParam;
        mConfigVideo.setAspectRatioType(aspectRatioType, setParam);

        updateParam();
    }
}
