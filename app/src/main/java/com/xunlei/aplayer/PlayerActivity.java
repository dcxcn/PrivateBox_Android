package com.xunlei.aplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aplayer.aplayerandroid.APlayerAndroid;
import com.xunlei.aplayer.model.APlayerParam;
import com.xunlei.aplayer.model.PlayConfig;
import com.xunlei.aplayer.model.Content;
import com.xunlei.aplayer.play_implement.ConfigAudio;
import com.xunlei.aplayer.play_implement.ConfigSubtitle;
import com.xunlei.aplayer.play_implement.ConfigVideo;
import com.xunlei.aplayer.play_implement.Record;
import com.xunlei.aplayer.play_interface.IConfigVideo;
import com.xunlei.aplayer.ui.PlayerPopupWindow;
import com.xunlei.aplayer.util.APlayerSationUtil;
import com.xunlei.aplayer.util.BoolUtil;
import com.xunlei.aplayer.util.PropertyNameToConfigID;
import com.xunlei.aplayer.util.TimeUtil;
import cn.dcx.privatebox.R;
import java.util.HashMap;
import java.util.Map;

import static com.xunlei.aplayer.util.PreferencesAttribute.getPreferencesAll;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayerActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
//            ActionBar actionBar = getSupportActionBar();
//            if (actionBar != null) {
//                actionBar.show();
//            }

            mControlsView.setVisibility(View.VISIBLE);

        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-spacer_medium UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private static final String ERROR_TAG = Content.APLAYER_DEMO_LOG_PREF_TAG + PlayerActivity.class.getSimpleName();
    private static final String DEBUG_TAG = ERROR_TAG;
    public static final String VIDEO_FILE_PATH = "video_file_path";
    public static final String VIDEO_FILE_NAME = "video_file_name";
    private APlayerAndroid aPlayer = null;
    private APlayerAndroid.OnPlayCompleteListener mPlayCompleteListener = null;
    private FrameLayout mRootView;
    private SurfaceView mSurView;
    private TextView mTextViewCurrentTime;
    private TextView mTextViewDurationTime;
    private SeekBar mSeekBarPlayProgress;
    private Button mBtnPlaySeting;

    private APlayerParam aPlayerParam;
    private boolean mIsNeedUpdateUIProgress = false;   //标志位，是否需要更新播放进度
    private Thread mUpdateThread = null;
    private EventHandle handle = null;
    private boolean mIsSystemCallPause = false;          //是否因系统Activity切换导致暂停，以便于恢复播放
    //private boolean                               mIsOpen = false;                //标志文件是否打开
    private PopupWindow mPopupWindow = null;
    private PlayConfig mPlayConfig = null;            //播放配置接口,部分和具体媒体文件强相关的参数，必须打开文件成功后设置
    private TextView mTextViewSeekTime = null;         //拖动进度条时，在屏幕中央以大字体显示当前的进度
    private TextView mTextViewBufferProgress = null;         //拖动进度条时，在屏幕中央以大字体显示当前的进度
    private volatile boolean mIsTouchingSeekbar = false;

    private static final int UI_UPDATE_PLAY_STATION = 0X1000;//更新视频持续时间
    private static final int TIMER_UPDATE_INTERVAL_TIME = 100;   //定时器时间间隔，更新播放进度


    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mVisible = true;

        Intent intent = getIntent();

        String mediaFilePath = null;// = (String) intent.getSerializableExtra(Content.MEDIA_FILE_PATH);
//        Uri uri = (Uri) intent.getData();
//        if (uri != null) {
//            mediaFilePath = uri.toString();
////            String path = uri.getPath();
////            String s = uri.getEncodedPath();
//        }
//
//        String sch = getIntent().getScheme();
//        if(null != sch )
//        {
//            Log.e(ERROR_TAG, sch);
//        }
//        final String LOCAL_FILE_PROTOCOL_PREFIX = "file://";
//        if(null != mediaFilePath && mediaFilePath.startsWith(LOCAL_FILE_PROTOCOL_PREFIX)) {
//            mediaFilePath = mediaFilePath.substring(LOCAL_FILE_PROTOCOL_PREFIX.length());
//            mediaFilePath = Uri.decode(mediaFilePath);
//        }

        Uri uri = (Uri) intent.getData();
        mediaFilePath = parseMediaPath(uri);

        Map<String, String> pramMap  = getPreferencesAll(PlayerActivity.this);
        aPlayerParam = new APlayerParam(mediaFilePath, pramMap);
        if (null == mediaFilePath || null == pramMap) {
            Log.e(ERROR_TAG, "APlayerParam is't effective");
        }

        getComponent();
        initPlayer();
        setPlayerParameter();
        boolean isUseVR = isUseVR(aPlayer);
        if(isUseVR) hide();
        mContentView = getDisplayView(isUseVR);
        if(isUseVR) setVRMode();
        
        setScreenOrientation(false);
        initPlayConfig(mContentView);
        setPlayerView(isUseVR);

        hideActionBar();
        registerListener();
        play();

        handle = new EventHandle();
    }

    private Map<String, String> getDeafultConfigParam()
    {
        Map<String, String> configParam = new HashMap<String, String>();
        return null;
    }
    private APlayerParam getDefaultPlayerParam(String mediaFilePath)
    {
        Map<String, String> configParam = getDeafultConfigParam();
        APlayerParam aPlayerParam = new APlayerParam(mediaFilePath, configParam);
        return aPlayerParam;
    }
    private void setVRMode()
    {
        Map<String, String> configParam = aPlayerParam.getConfigParam();
        if(null == configParam){
            return;
        }

        PropertyNameToConfigID propertyNameToConfigID = PropertyNameToConfigID.getInstance(this);
        String vrModeKey = getString(R.string.PREF_VR_MODE);
        String vrModeVal = configParam.get(vrModeKey);
        if(null == vrModeKey || null == vrModeVal){
            Log.e(DEBUG_TAG, "SetConfig() faile " + "vrModeKey = " + vrModeKey + " vrModeVal = " + vrModeVal);
        }

        int configID = propertyNameToConfigID.getIDByName(vrModeKey);
        if (0 != aPlayer.setConfig(configID, vrModeVal)) {
             Log.e(DEBUG_TAG, "SetConfig() faile " + "configID = " + configID + " val = " + vrModeVal);
        }
    }

    private void setScreenOrientation(boolean isUseVR)
    {
        int orientation = isUseVR ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        setRequestedOrientation(orientation);
    }

    private void initPlayConfig(View displayView)
    {
        final String filePath = aPlayerParam.getFilePath();
        PlayConfig.PlayerListener playerListener = new PlayConfig.PlayerListener();
        playerListener.playCompleteListener = mPlayCompleteListener;
        mPlayConfig = new PlayConfig(new ConfigVideo(aPlayer, filePath, playerListener, displayView),
                new ConfigAudio(aPlayer), new ConfigSubtitle(aPlayer), new Record(aPlayer));
    }

    private static boolean isUseVR(APlayerAndroid aPlayerAndroid) {
        boolean IsUseVR = false;
        if(null != aPlayerAndroid){
            IsUseVR = BoolUtil.APlayerConfigValToBool(aPlayerAndroid.getConfig(APlayerAndroid.CONFIGID.VR_ENABLE));
        }

        return IsUseVR;
    }

    private View getDisplayView(boolean isVRView) {
        View displayView = null;
        if(isVRView) {
//            displayView = aPlayer.createVRView(this);
//            mRootView.addView(displayView);
//            DisplayAreaSetUtil.adjustMatchParentDisplaySize(displayView);
//            //DisplayAreaSetUtil.adjustCustomRatioDisplaySize(displayView,1);
//            mRootView.bringChildToFront(findViewById(R.id.player_set_root));
//            mRootView.bringChildToFront(findViewById(R.id.play_setting));
        }
        else
        {
            displayView = mSurView;
        }

        return displayView;
    }

    private void getComponent() {
        mRootView = (FrameLayout)findViewById(R.id.play_view_root);
        mControlsView = findViewById(R.id.player_control);

        mSurView = (SurfaceView) findViewById(R.id.fullscreen_content);
        mTextViewCurrentTime = (TextView) findViewById(R.id.play_time);
        mTextViewDurationTime = (TextView) findViewById(R.id.duration_time);
        mSeekBarPlayProgress = (SeekBar) findViewById(R.id.play_seek_bar);
        mBtnPlaySeting = (Button) findViewById(R.id.play_setting);
        mTextViewSeekTime = (TextView) findViewById(R.id.play_seek_time_text_view);
        mTextViewBufferProgress = (TextView) findViewById(R.id.play_buffering_text_view);

        mTextViewCurrentTime.setText(TimeUtil.formatFromSecond(0));
        mTextViewDurationTime.setText(TimeUtil.formatFromSecond(0));
        mTextViewSeekTime.setVisibility(View.INVISIBLE);
        mTextViewBufferProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

        //delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mBtnPlaySeting.setVisibility(View.INVISIBLE);
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mBtnPlaySeting.setVisibility(View.VISIBLE);
        // show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onPause() {

        int statue = aPlayer.getState();
        if (isPlay(statue)) {
            aPlayer.pause();
            mIsSystemCallPause = true;
        }

        //让播放进度UI更新线程退出
        stopUIUpdateThread();
        Toast.makeText(getApplicationContext(), "statue = " + statue, Toast.LENGTH_SHORT).show();
        Log.e(DEBUG_TAG, "onPause zxcvbnm,.");
        super.onPause();
    }

    @Override
    protected void onResume() {
        //普通播放，后台切换回来，恢复之前的播放状态
        boolean isUseVR = isUseVR(aPlayer);
        setPlayerView(isUseVR);
        if (mIsSystemCallPause) {
            aPlayer.play();
            mIsSystemCallPause = false;
        }

        startUIUpdateThread();
        Log.e(DEBUG_TAG, "onResume 123456789");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.e(DEBUG_TAG, "OnStop...");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        aPlayer.close();
        aPlayer.destroy();
        destroyPopWind();
        super.onDestroy();

        Log.e(DEBUG_TAG, "onDestroy()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetDisplayAreaSize();
    }

    //Screen orientation change, or file open success adjust diaplay area size(aftern open file, can get the native ration)
    void resetDisplayAreaSize()
    {
        if(null == mPlayConfig)
        {
            return;
        }
        IConfigVideo configVideo = mPlayConfig.getConfigVideo();
        if(null == configVideo)
        {
            return;
        }

        IConfigVideo.AspectRatioType aspectRatioType = configVideo.getAspectRatioType();
        String aspectCustomParam = null;
        if(IConfigVideo.AspectRatioType.RatioCustom == aspectRatioType)
        {
            aspectCustomParam = configVideo.getAspectRatioCustom();
        }
        configVideo.setAspectRatioType(aspectRatioType, aspectCustomParam);
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void destroyPopWind() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }
    private class EventHandle extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UI_UPDATE_PLAY_STATION:
                    updateUIPlayStation(msg.arg1, msg.arg2);
                    break;
                default:
                    break;
            }
        }
    }

    private class UpdatePlayUIProcess implements Runnable {
        @Override
        public void run() {
            //while(!Thread.currentThread().isInterrupted())
            while (mIsNeedUpdateUIProgress) {
                if (!mIsTouchingSeekbar) {
                    int currentPlayTime = 0;
                    int durationTime = 0;
                    if (null != aPlayer) {
                        currentPlayTime = aPlayer.getPosition();
                        durationTime = aPlayer.getDuration();
                    }

                    Message msg = handle.obtainMessage(UI_UPDATE_PLAY_STATION, currentPlayTime, durationTime);
                    handle.sendMessage(msg);
                }

                try {
                    Thread.sleep(TIMER_UPDATE_INTERVAL_TIME);
                } catch (InterruptedException e) {
                    Log.e(DEBUG_TAG, e.toString());
                }
            }

        }
    }


    private void updateUIPlayStation(int currentPlayTimeMs, int durationTimeMs) {
        setTimeTextView(mTextViewCurrentTime, currentPlayTimeMs);
        setTimeTextView(mTextViewDurationTime, durationTimeMs);

        if (durationTimeMs > 0 && currentPlayTimeMs >= 0) {
            mSeekBarPlayProgress.setMax(durationTimeMs);
            mSeekBarPlayProgress.setProgress(currentPlayTimeMs);
        } else {
            mSeekBarPlayProgress.setProgress(0);
        }

    }

    private void setTimeTextView(TextView timeView, int millisecond) {
        int second = millisecond / 1000;
        second = second > 0 ? second : 0;  //媒体文件未打开时，播放位置字段无效
        timeView.setText(TimeUtil.formatFromSecond(second));
    }

    private void setBufferProgress(int progress) {
        int visibility = (progress == 100) ? View.INVISIBLE : View.VISIBLE;
        mTextViewBufferProgress.setVisibility(visibility);
        String strProgress = progress + "%";
        mTextViewBufferProgress.setText(strProgress);
    }

    private void initPlayer() {
        if (null != aPlayer) {
            return;
        }

        aPlayer = new APlayerAndroid();
        aPlayer.setOnOpenSuccessListener(new APlayerAndroid.OnOpenSuccessListener() {
            @Override
            public void onOpenSuccess() {
                aPlayer.play();
                startUIUpdateThread();
                Button playPauseSwitchbutton = (Button) findViewById(R.id.button_play_pause);
                playPauseSwitchbutton.setText(R.string.PLAYRT_PAUSE);

                //test code,avoid listener covered
                aPlayer.setOnPlayCompleteListener(mPlayCompleteListener);
                resetDisplayAreaSize();
                //switch vr mode,need reset orientation
                setScreenOrientation(isUseVR(aPlayer));
            }
        });

        aPlayer.setOnOpenCompleteListener(new APlayerAndroid.OnOpenCompleteListener() {
            public void onOpenComplete(boolean isOpenSuccess) {
                Log.e(DEBUG_TAG, "event setOnOpenCompleteListener " + (isOpenSuccess ? "success" : "fail"));
                //aPlayer.play();
            }
        });


        aPlayer.setOnPlayStateChangeListener(new APlayerAndroid.OnPlayStateChangeListener() {

            @Override
            public void onPlayStateChange(int nCurrentState, int nPreState) {
                Log.e(DEBUG_TAG, "event onPlayStateChange nCurrentState = " + nCurrentState + " nPreState = " + nPreState);
                if (APlayerAndroid.PlayerState.APLAYER_PLAYING == nCurrentState) {
                    startUIUpdateThread();
                    Log.e(DEBUG_TAG, "Start UpdateUIThread()");
                } else {
                    stopUIUpdateThread();
                    Log.e(DEBUG_TAG, "Stop UpdateUIThread()");
                }
            }
        });

        mPlayCompleteListener = new APlayerAndroid.OnPlayCompleteListener() {
            @Override
            public void onPlayComplete(String playRet) {
                Log.e(DEBUG_TAG, "event onPlayComplete  playRet = " + playRet);
                boolean isUseCallStop = APlayerSationUtil.isStopByUserCall(playRet);
                if (!isUseCallStop) {
                    finish();           //只在自然播放结束后退出
                }
            }
        };
        aPlayer.setOnPlayCompleteListener(mPlayCompleteListener);

        aPlayer.setOnSeekCompleteListener(new APlayerAndroid.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                // TODO Auto-generated method stub
                Log.e(DEBUG_TAG, "event onSeekComplete = ");
            }
        });

        aPlayer.setOnBufferListener(new APlayerAndroid.OnBufferListener() {

            @Override
            public void onBuffer(int progress) {
                Log.e(DEBUG_TAG, "event onBuffer progress = " + progress);
                //((TextView) findViewById(R.id.textView1)).setText(progress+ " ");
                setBufferProgress(progress);
            }
        });

        aPlayer.setOnSurfaceDestroyListener(new APlayerAndroid.OnSurfaceDestroyListener() {

            @Override
            public void onSurfaceDestroy() {
                Log.e(DEBUG_TAG, "event OnSurfaceDestroy");
            }
        });

        aPlayer.setOnShowSubtitleListener(new APlayerAndroid.OnShowSubtitleListener() {

            @Override
            public void onShowSubtitle(String subtitle) {
                Log.e(DEBUG_TAG, "event OnShowSubtitleListener " + subtitle);
            }
        });

        aPlayer.setOnSystemPlayerFailListener(new APlayerAndroid.OnSystemPlayerFailListener() {

            @Override
            public void onSystemPlayerFail() {
                Log.e(DEBUG_TAG, "event OnSystemPlayerFail ");
                aPlayer.setConfig(APlayerAndroid.CONFIGID.HW_DECODER_USE, "1");
            }
        });
    }

    private void setPlayerView(boolean isUseVR) {
        if(!isUseVR) {
            aPlayer.setView(mSurView);
        }
    }

    private void setPlayerParameter() {
        //aPlayer.UseSystemPlayer(true);
        //aPlayer.UseSystemPlayer(false);

        Map<String, String> configParam = aPlayerParam.getConfigParam();
        if (null != configParam) {
            PropertyNameToConfigID propertyNameToConfigID = PropertyNameToConfigID.getInstance(this);
            for (Map.Entry<String, String> entry : configParam.entrySet()) {
                Integer integer = propertyNameToConfigID.getIDByName(entry.getKey());
                if(null == integer){
                    continue;
                }

                int configID = integer.intValue();
                if (0 != aPlayer.setConfig(configID, entry.getValue())) {
                    Log.e(DEBUG_TAG, "SetConfig() faile " + "configID = " + configID + " val = " + entry.getValue());
                }
            }
        }

    }

    private void play() {
        if (null == aPlayer) {
            Log.e(DEBUG_TAG, "null == aPlayer");
            return;
        }

        String filePath = aPlayerParam.getFilePath();
        aPlayer.open(filePath);
    }

    private void registerListener() {
        // Set up the user interaction to manually show or hide the system UI.
        View.OnTouchListener playViewClick = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN != motionEvent.getAction())
                {
                    return true;
                }

                toggle();
                PlayerActivity.this.destroyPopWind();
                return true;
            }

        };

        mRootView.setOnTouchListener(playViewClick);
        mContentView.setOnTouchListener(playViewClick);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.player_hide_control_bar_button).setOnTouchListener(mDelayHideTouchListener);

        final Button playPauseSwitchButton = (Button) findViewById(R.id.button_play_pause);
        Button restartButton = (Button) findViewById(R.id.button_restart);

        playPauseSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == aPlayer) {
                    return;
                }

                String buttonText = playPauseSwitchButton.getText().toString();
                if (buttonText.compareToIgnoreCase(getString(R.string.PLAYRT_PLAY)) == 0) {
                    playPauseSwitchButton.setText(R.string.PLAYRT_PAUSE);
                    aPlayer.play();
                } else {
                    playPauseSwitchButton.setText(R.string.PLAYRT_PLAY);
                    aPlayer.pause();
                }
            }
        });


        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == aPlayer) {
                    return;
                }

                aPlayer.setPosition(0);
            }
        });

        mSeekBarPlayProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private static final int SEEK_MIN_GATE_MS = 1000;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (null == aPlayer || !fromUser) {
                    return;
                }

                mIsTouchingSeekbar = true;
                userSeekPlayProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(DEBUG_TAG, "onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTextViewSeekTime.setVisibility(View.INVISIBLE);
                mIsTouchingSeekbar = false;
                startUIUpdateThread();
            }
        });

        mBtnPlaySeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mPopupWindow) {
//                    int height = PlayerActivity.this.mSurView.getHeight();
//                    int widht = PlayerActivity.this.mSurView.getWidth();

                    int height = mRootView.getHeight();
                    int widht = mRootView.getWidth();

                    String msg = String.format("height = %d, wideght = %d", height, widht);

                    //屏幕可能旋转，取得较小的一边
                    int length = (height > widht) ? widht : height;
                    final int MARGIN = 200;
                    length -= MARGIN;
                    Toast.makeText(PlayerActivity.this, msg, Toast.LENGTH_LONG).show();
                    //View parentView  = PlayerActivity.this.mSurView;
                    View parentView = mRootView;
                    mPopupWindow = new PlayerPopupWindow(mPlayConfig, parentView, aPlayerParam.getFilePath(), length, length);
                    mPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
                }
            }
        });

    }

    private boolean isOverSeekGate(int seekBarPositionMs, int currentPlayPosMs) {
        final int SEEK_MIN_GATE_MS = 1000;
        boolean isChangeOverSeekGate = Math.abs(currentPlayPosMs - seekBarPositionMs) > SEEK_MIN_GATE_MS;
        return isChangeOverSeekGate;
    }

    private void userSeekPlayProgress(int seekPostionMs) {
        int currentPlayPos = aPlayer.getPosition();
        boolean isChangeOverSeekGate = isOverSeekGate(seekPostionMs, currentPlayPos);
        if (!isChangeOverSeekGate) {
            //避免拖动粒度过细，拖动时频繁定位影响体验
            return;
        }

        mIsTouchingSeekbar = true;
        stopUIUpdateThread();
        //mSeekBarPlayProgress.setProgress(seekPostionMs);
        setTimeTextView(mTextViewCurrentTime, seekPostionMs);
        setTimeTextView(mTextViewSeekTime, seekPostionMs);
        mTextViewSeekTime.setVisibility(View.VISIBLE);

        aPlayer.setPosition(seekPostionMs);

        Log.e(DEBUG_TAG, "current time = " + currentPlayPos);
        Log.e(DEBUG_TAG, "onProgressChanged to " + seekPostionMs);
    }

    //
    private boolean isPlay(int status) {
        if (APlayerAndroid.PlayerState.APLAYER_PLAY == status ||
                APlayerAndroid.PlayerState.APLAYER_PLAYING == status) {
            return true;
        }

        return false;
    }

    private void startUIUpdateThread() {
        if (null == mUpdateThread) {
            mIsNeedUpdateUIProgress = true;
            mUpdateThread = new Thread(new UpdatePlayUIProcess());
            mUpdateThread.start();
        } else {
            Log.e(DEBUG_TAG, "null != mUpdateThread");
        }
    }

    private void stopUIUpdateThread() {
        mIsNeedUpdateUIProgress = false;
        mUpdateThread = null;
    }

    private String parseMediaPath(Uri uri)
    {
        if(null == uri){
            return null;
        }

        String path = uri.toString();
        final String scheme = uri.getScheme();
        final String LOCAL_FILE_SCHEME = "file";
        //仅仅对本地文件立即进行Decode
        if(null != scheme && LOCAL_FILE_SCHEME.equalsIgnoreCase(scheme)) {
            path = uri.getEncodedPath();
            path = Uri.decode(path);
        }

        return path;
    }

}
