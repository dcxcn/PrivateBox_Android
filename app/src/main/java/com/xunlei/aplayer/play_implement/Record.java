package com.xunlei.aplayer.play_implement;

import com.aplayer.aplayerandroid.APlayerAndroid;
import com.xunlei.aplayer.play_interface.IRecord;

/**
 * Created by LZ on 2016/9/24.
 */
public class Record implements IRecord
{
    private APlayerAndroid mAPlayerAndroid = null;

    public Record(APlayerAndroid aPlayerAndroid) {
        mAPlayerAndroid = aPlayerAndroid;
    }

    @Override
    public boolean startRecords(String mediaOutPath) {
    	
        return mAPlayerAndroid.startRecord(mediaOutPath);
    }

    @Override
    public boolean isRecording() {
        return mAPlayerAndroid.isRecording();
    }

    @Override
    public void endRecords() {
    	mAPlayerAndroid.endRecord();
    }
}
