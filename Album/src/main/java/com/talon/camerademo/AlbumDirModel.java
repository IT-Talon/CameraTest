package com.talon.camerademo;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by 003 on 2016-12-13.
 */
public class AlbumDirModel implements Serializable
{
    private static final long serialVersionUID = -648682205559610005L;

    private String name;

    private int photoCount;

    private String firstMediaPath;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getPhotoCount()
    {
        return photoCount;
    }

    public void setPhotoCount(int photoCount)
    {
        this.photoCount = photoCount;
    }

    public String getFirstMediaPath()
    {
        return firstMediaPath;
    }

    public void setFirstMediaPath(String firstMediaPath)
    {
        this.firstMediaPath = firstMediaPath;
    }

    public Uri getFirstUri()
    {
        return ResourceUtil.getFileURI(getFirstMediaPath());
    }
}