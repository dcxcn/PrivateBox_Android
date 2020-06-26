package com.xunlei.aplayer.view;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import cn.dcx.privatebox.R;
import com.xunlei.aplayer.model.Content;
import com.xunlei.aplayer.play_interface.IRecord;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by LZ on 2016/9/24.
 */
public class RecordSetView extends LinearLayout {

    private static final String ERROR_TAG = Content.APLAYER_DEMO_LOG_PREF_TAG + RecordSetView.class.getSimpleName();

    private Context mContext;
    private View mRootView;
    private IRecord mRecord;

    TextView    mRecordCategory;
    Switch      mRecordSwitch;
    String      mOriginalMediaPath;

    public RecordSetView(Context context, IRecord record, String originalMediaPath) {
        super(context);

        mContext = context;
        mRecord = record;
        mOriginalMediaPath = originalMediaPath;

//        //show dialog make use chose able
//        mOriginalMediaPath = Environment.getExternalStorageDirectory() + "/test.flv";

        mRootView = LayoutInflater.from(context).inflate(R.layout.record_set_view, this);
        setOrientation(VERTICAL);

        init();
        updateParam();
        registerListener();
    }

    private void init()
    {
        mRecordCategory = (TextView) mRootView.findViewById(R.id.popwnd_record);
        mRecordSwitch = (Switch)mRootView.findViewById(R.id.popwnd_record_show);
    }


    private void registerListener() {
        mRecordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    String mediaPath = Environment.getExternalStorageDirectory() + "/" + getOutputFile(mOriginalMediaPath);
                   if(! mRecord.startRecords(mediaPath))
                   {
                       Log.e(ERROR_TAG, "Start Record Failed!");
                   }
                }
                else
                {
                    mRecord.endRecords();
                }
            }
        });

    }

    private void updateParam()
    {
        boolean isRecording = mRecord.isRecording();
        mRecordSwitch.setChecked(isRecording);
    }

    private static String getOutputFile(String originalMediaPath)
    {
        String formatPath = originalMediaPath.replace('\\', '/');

        String externName = "mp4";
        int externIndex = originalMediaPath.lastIndexOf('.');
        if(externIndex > 0)
        {
            externName = originalMediaPath.substring(externIndex + 1);
        }

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");//日期格式
        String time = dateFormat.format( now );

        String fileName = "";
        int fileIndex = formatPath.lastIndexOf('/');
        if(fileIndex >= 0)
        {
            if(externIndex > 0)
            {
                fileName = formatPath.substring(fileIndex + 1, externIndex);
            }
            else
            {
                fileName = formatPath.substring(fileIndex + 1);
            }

        }

        String tMediaPath = "/" + fileName + "_" + time + "." + externName;
        return tMediaPath;
    }
}