package com.talon.camerademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.File;

/**
 * 图片裁剪
 */
public class ImageCropView extends TouchImageView
{
    private Paint paint;

    private Path path;

    private String imageFilePath;

    private float minPadding, leftRightPadding, topBottomPadding;

    private float displayWidth, displayHeight;

    private float currentTransX, currentTransY;

    private float imageRatio;

    private boolean needCalculatePadding;

    private Rect cropRect;

    public ImageCropView(Context context)
    {
        super(context);
    }

    public ImageCropView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ImageCropView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void sharedConstructing(Context context)
    {
        super.sharedConstructing(context);
        this.minPadding = CommonUtil.dp2px(getContext(), 15);
        this.paint = new Paint();
        this.path = new Path();
        this.setImageRatio(1);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int width = this.getWidth();
        int height = this.getHeight();

        if(width > 0 && height > 0 && needCalculatePadding)
        {
            calculateImagePadding();
            calculateZoom();
        }

        super.onDraw(canvas);

        if(leftRightPadding > 0 && topBottomPadding > 0)
        {
            float left = leftRightPadding;
            float right = leftRightPadding;
            float top = topBottomPadding;
            float bottom = topBottomPadding;

            if(width > left + right && height > top + bottom)
            {
                path.reset();
                path.addRect(0, 0, left, height, Direction.CW);
                path.addRect(width - right, 0, width, height, Direction.CW);
                path.addRect(left, 0, width - right, top, Direction.CW);
                path.addRect(left, height - bottom, width - right, height, Direction.CW);
                paint.setColor(0x99000000);
                paint.setStyle(Style.FILL);
                canvas.drawPath(path, paint);
                paint.setColor(0xFFFFFFFF);
                paint.setStyle(Style.STROKE);
                canvas.drawRect(left, top, width - right, height - bottom, paint);
            }
        }
    }

    @Override
    protected float getFixDragTrans(float delta, float viewSize, float contentSize)
    {
        return delta;
    }

    @Override
    protected float getFixTrans(float trans, float viewSize, float contentSize, boolean horizontal)
    {
        float newTrans = 0;
        float currentTrans = trans;
        float maxPaddingDis;
        float padding = horizontal? leftRightPadding: topBottomPadding;

        if(trans > padding)
        {
            newTrans = -trans + padding;
            currentTrans = padding;
        }
        else if(trans < (maxPaddingDis = -(contentSize - viewSize + padding)))
        {
            newTrans = -trans + maxPaddingDis;
            currentTrans = maxPaddingDis;
        }

        if(horizontal)
        {
            currentTransX = currentTrans;
        }
        else
        {
            currentTransY = currentTrans;
        }

        return newTrans;
    }

    @Override
    protected FlingRunnable createFlingRunnable(float velocityX, float velocityY)
    {
        return null;
    }

    private void calculateImagePadding()
    {
        int width = this.getWidth();
        int height = this.getHeight();
        float maxDisplayWidth = width - minPadding * 2;
        float maxDisplayHeight = height - minPadding * 2;
        displayWidth = maxDisplayWidth;
        displayHeight = maxDisplayWidth / imageRatio;

        if(displayHeight > maxDisplayHeight)
        {
            displayHeight = maxDisplayHeight;
            displayWidth = displayHeight * imageRatio;
        }

        this.leftRightPadding = (width - displayWidth) / 2;
        this.topBottomPadding = (height - displayHeight) / 2;
        needCalculatePadding = false;
    }

    private void calculateZoom()
    {
        float imageWidth = this.getImageWidth();
        float imageHeight = this.getImageHeight();
        float minZoom = 1;

        if(imageWidth > displayWidth && imageHeight > displayHeight)
        {
            minZoom = Math.max(displayWidth / imageWidth, displayHeight / imageHeight);
        }
        else if(imageWidth < displayWidth)
        {
            minZoom = displayWidth / imageWidth;
        }
        else if(imageHeight < displayHeight)
        {
            minZoom = displayHeight / imageHeight;
        }

        this.setMinZoom(minZoom);
        this.setMaxZoom(minZoom * 3);
        this.resetZoom();
    }

    @Override
    public void resetZoom()
    {
        normalizedScale = this.getMinZoom();
        fitImageToView();
    }

    public Bitmap getCroppedBitmap()
    {
        Bitmap bitmap = ResourceUtil.getBitmapFromPath(imageFilePath);

        if(bitmap == null)
        {
            return null;
        }

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        float scaleX = bitmapWidth / this.getImageWidth();
        float scaleY = bitmapHeight / this.getImageHeight();
        int x = Math.max((int)((leftRightPadding - currentTransX) * scaleX), 0);
        int y = Math.max((int)((topBottomPadding - currentTransY) * scaleY), 0);
        int width = Math.max((int)(displayWidth * scaleX), 1);
        int height = Math.max((int)(displayHeight * scaleY), 1);

        if(x + width > bitmapWidth)
        {
            width = bitmapWidth - x;
        }

        if(y + height > bitmapHeight)
        {
            height = bitmap.getHeight() - y;
        }

        this.cropRect = new Rect(x, y, x + width, y + height);
        float ratio = Math.min(1, 720 / (float)width);
        Matrix matrix = null;

        if(ratio < 1)
        {
            matrix = new Matrix();
            matrix.setScale(ratio, ratio);
        }

        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, x, y, width, height, matrix, false);

        if(bitmap != croppedBitmap)
        {
            bitmap.recycle();
        }

        return croppedBitmap;
    }

    public Rect getCropRect()
    {
        return this.cropRect;
    }

    public float getImageRatio()
    {
        return this.imageRatio;
    }

    public void setImageRatio(float imageRatio)
    {
        this.imageRatio = imageRatio;
        this.needCalculatePadding = true;
    }

    public String getImageFilePath()
    {
        return this.imageFilePath;
    }

    public void setImageFilePath(String imageFilePath)
    {
        this.imageFilePath = imageFilePath;
        ResourceUtil.loadImageForImageView(this, new File(imageFilePath), 0, 0, false, false);
    }

    public void setImageFilePath(String imageFilePath, int maxWidth, int maxHeight)
    {
        this.imageFilePath = imageFilePath;
        ResourceUtil.loadImageForImageView(this, new File(imageFilePath), maxWidth, maxHeight, false, 0);
    }
}