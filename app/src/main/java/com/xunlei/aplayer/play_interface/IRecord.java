package com.xunlei.aplayer.play_interface;

/**
 * Created by LZ on 2016/9/24.
 */
public interface IRecord {
    boolean startRecords(String mediaOutPath);
    boolean isRecording();
    void endRecords();
}
