package com.talon.camerademo;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by 003 on 2016-12-12.
 */
public class AlbumModel implements Serializable
{
    private static final long serialVersionUID = 5444648851180276521L;

    private String path;

    private String bucketDisplayName;

    private long videoDuration;

    private final boolean vedio;

    public AlbumModel(boolean vedio)
    {
        this.vedio = vedio;
    }

    public String getPath()
    {
        return this.path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getBucketDisplayName()
    {
        return this.bucketDisplayName;
    }

    public void setBucketDisplayName(String bucketDisplayName)
    {
        this.bucketDisplayName = bucketDisplayName;
    }

    public long getVideoDuration()
    {
        return this.videoDuration;
    }

    public void setVideoDuration(long videoDuration)
    {
        this.videoDuration = videoDuration;
    }

    public boolean isVedio()
    {
        return this.vedio;
    }

    public Uri getUri()
    {
        return ResourceUtil.getFileURI(getPath());
    }
}
