package com.xunlei.aplayer.model;

/**
 * Created by admin on 2016/8/24.
 */
public class ThumbnailParam
{
    public String name;
    public String path;
    public long  snapsTimeMs;
    public int width;
    public int height;

    public ThumbnailParam() {
    }

    public ThumbnailParam(String name, String path, long snapsTimeMs, int width, int height) {
        this.name = name;
        this.path = path;
        this.snapsTimeMs = snapsTimeMs;
        this.width = width;
        this.height = height;
    }
}
