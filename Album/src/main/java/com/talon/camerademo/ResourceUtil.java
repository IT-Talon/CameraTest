package com.talon.camerademo;

import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ResourceUtil {
    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, int id) {
        Drawable drawable = null;

        if (id > 0) {
            try {
                drawable = context.getResources().getDrawable(id);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return drawable;
    }

    @SuppressWarnings("deprecation")
    public static int getColor(Context context, int id) {
        return context.getResources().getColor(id);
    }

    public static String getMetaValue(Context context, String metaKey) {
        String metaValue = null;

        if (context != null && metaKey != null) {
            try {
                Bundle metaData = null;
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

                if (info != null) {
                    metaData = info.metaData;
                }

                Object value = metaData == null ? null : metaData.get(metaKey);

                if (value != null) {
                    if (value instanceof String) {
                        metaValue = (String) value;
                    } else {
                        metaValue = value.toString();
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return metaValue;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        Rect bounds = drawable.getBounds();
        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        if (width > 0 && height > 0) {
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                try {
                    bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    public static String getAlbumLatestThumbnails(Context context) {
        String path = null;

        try {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " not like ?";
            String[] selectionArgs = {"%drawable%"};
            String order = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " asc, " + MediaStore.Images.Media._ID + " desc LIMIT 1";
            CursorLoader loader = new CursorLoader(context, uri, projection, selection, selectionArgs, order);
            Cursor cursor = loader.loadInBackground();

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }

            cursor.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return path;
    }

    public static List<AlbumModel> getAlbumPictures(Context context) {
        List<AlbumModel> itemList = new ArrayList<>();

        try {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " not like ?";
            String[] selectionArgs = {"%drawable%"};
            String order = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " asc, " + MediaStore.Images.Media._ID + " desc";
            CursorLoader loader = new CursorLoader(context, uri, projection, selection, selectionArgs, order);
            Cursor cursor = loader.loadInBackground();

            if (cursor.getCount() > 0) {
                AlbumModel item;
                int dataColumnIndex;
                String path, bucketDisplayName;

                while (cursor.moveToNext()) {
                    dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    path = cursor.getString(dataColumnIndex);
                    dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    bucketDisplayName = cursor.getString(dataColumnIndex);
                    item = new AlbumModel(false);
                    item.setPath(path);
                    item.setBucketDisplayName(bucketDisplayName);
                    itemList.add(item);
                }
            }

            cursor.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return itemList;
    }

    public static List<AlbumModel> getAlbumVedios(Context context) {
        List<AlbumModel> itemList = new ArrayList<>();

        try {
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION};
            String selection = MediaStore.Video.Media.DURATION + " <= 15000";
            String order = MediaStore.Video.Media._ID + " desc";
            CursorLoader loader = new CursorLoader(context, uri, projection, selection, null, order);
            Cursor cursor = loader.loadInBackground();

            if (cursor.getCount() > 0) {
                AlbumModel item;
                int dataColumnIndex;
                String path, bucketDisplayName;
                long duration;

                while (cursor.moveToNext()) {
                    dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    path = cursor.getString(dataColumnIndex);
                    dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                    bucketDisplayName = cursor.getString(dataColumnIndex);
                    dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                    duration = cursor.getLong(dataColumnIndex);
                    item = new AlbumModel(true);
                    item.setPath(path);
                    item.setBucketDisplayName(bucketDisplayName);
                    item.setVideoDuration(duration);
                    itemList.add(item);
                }
            }

            cursor.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return itemList;
    }

    public static int toIntColor(String color) {
        int intColor = 0;

        if (color != null && !color.isEmpty()) {
            color = color.toLowerCase();
            color = color.replaceFirst("#", "");

            if (color.length() < 8) {
                color = "ff" + color;
            }

            intColor = (int) Long.parseLong(color, 16);
        }

        return intColor;
    }

    public static String toStringColor(int color, boolean includeAlpha) {
        if (color == 0) {
            return includeAlpha ? "#00000000" : "#000000";
        } else {
            StringBuilder colorStr = new StringBuilder(Integer.toHexString(color));
            int neededLength = includeAlpha ? 8 : 6;
            int realLength = colorStr.length();

            if (realLength > neededLength) {
                colorStr.replace(0, realLength - neededLength, "");
            } else {
                while (realLength < neededLength) {
                    colorStr.insert(0, 0);
                }
            }

            return '#' + colorStr.toString();
        }
    }

    public static int getGradientColor(float fraction, int startARGB, int endARGB) {
        int startA = (startARGB >> 24) & 0xFF;
        int startR = (startARGB >> 16) & 0xFF;
        int startG = (startARGB >> 8) & 0xFF;
        int startB = startARGB & 0xFF;
        int endA = (endARGB >> 24) & 0xFF;
        int endR = (endARGB >> 16) & 0xFF;
        int endG = (endARGB >> 8) & 0xFF;
        int endB = endARGB & 0xFF;
        int a = (startA + (int) (fraction * (endA - startA))) << 24;
        int r = (startR + (int) (fraction * (endR - startR))) << 16;
        int g = (startG + (int) (fraction * (endG - startG))) << 8;
        int b = startB + (int) (fraction * (endB - startB));
        return a | r | g | b;
    }

    /**
     * 截屏
     */
    public static Bitmap createScreenCapture(View view) {
        return createScreenCapture(view, 0, false);
    }

    public static Bitmap createScreenCapture(View view, int bgColor, boolean transparent) {
        Bitmap bitmap = null;

        try {
            Config config = transparent ? Config.ARGB_4444 : Config.RGB_565;
            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), config);
            Canvas canvas = new Canvas(bitmap);

            if (bgColor != 0) {
                canvas.drawColor(bgColor);
            }

            view.setDrawingCacheEnabled(true);
            view.draw(canvas);
            view.setDrawingCacheEnabled(false);
        } catch (Throwable t) {
        }

        return bitmap;
    }

    public static Rect getVideoBounds(String path) {
        MediaMetadataRetriever metadata = new MediaMetadataRetriever();
        metadata.setDataSource(path);
        int videoWidth = Integer.parseInt(metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int videoHeight = Integer.parseInt(metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        return new Rect(0, 0, videoWidth, videoHeight);
    }

    public static Rect getBitmapBounds(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        BitmapFactory.decodeFile(path, options);
        return new Rect(0, 0, options.outWidth, options.outHeight);
    }

    public static Bitmap getBitmapFromPath(String path) {
        return getBitmapFromPath(path, true);
    }

    public static Bitmap getBitmapFromPath(String path, boolean limitSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        BitmapFactory.decodeFile(path, options);

        if (limitSize) {
            int reqWidth = 1280;
            int reqHeight = 720;

            if (options.outWidth < options.outHeight) {
                reqWidth = 720;
                reqHeight = 1280;
            }

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        }

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options op, int reqWidth, int reqheight) {
        int originalWidth = op.outWidth;
        int originalHeight = op.outHeight;
        int inSampleSize = 1;

        if (originalWidth > reqWidth || originalHeight > reqheight) {
            int halfWidth = originalWidth / 2;
            int halfHeight = originalHeight / 2;

            while ((halfWidth / inSampleSize > reqWidth) && (halfHeight / inSampleSize > reqheight)) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void loadImageForImageView(ImageView imageView, File file, int width, int height, boolean crop, int placeholderResId) {
        DrawableRequestBuilder<File> requestBuilder = Glide.with(imageView.getContext()).load(file);

        if (FileType.isTypeOf(file.getPath(), "gif")) {
            if (requestBuilder instanceof DrawableTypeRequest) {
                ((DrawableTypeRequest<File>) requestBuilder).asGif();
            }

            requestBuilder = requestBuilder.diskCacheStrategy(DiskCacheStrategy.SOURCE);
        }

        requestBuilder = requestBuilder.override(width, height);

        if (crop) {
            requestBuilder = requestBuilder.centerCrop();
        } else {
            requestBuilder = requestBuilder.fitCenter();
        }

        if (placeholderResId > 0) {
            requestBuilder = requestBuilder.placeholder(placeholderResId);
        } else if (placeholderResId < 0) {
            requestBuilder = requestBuilder.dontAnimate();
        }

        requestBuilder.into(imageView);
    }

    public static void loadImageForImageView(ImageView imageView, File file, int placeholderResId, int errorResId, boolean fit, boolean centerInside) {
        DrawableRequestBuilder<File> requestBuilder = Glide.with(imageView.getContext()).load(file);

        if (FileType.isTypeOf(file.getPath(), "gif")) {
            if (requestBuilder instanceof DrawableTypeRequest) {
                ((DrawableTypeRequest<File>) requestBuilder).asGif();
            }

            requestBuilder = requestBuilder.diskCacheStrategy(DiskCacheStrategy.SOURCE);
        }

        if (errorResId > 0) {
            requestBuilder = requestBuilder.error(errorResId);
        }

        if (placeholderResId > 0) {
            requestBuilder = requestBuilder.placeholder(placeholderResId);
        } else if (placeholderResId < 0) {
            requestBuilder = requestBuilder.dontAnimate();
        }

        if (fit) {
            if (centerInside) {
                requestBuilder = requestBuilder.fitCenter();
            } else {
                requestBuilder = requestBuilder.centerCrop();
            }
        }

        requestBuilder.into(imageView);
    }

    public static void loadImageForImageView(ImageView imageView, String url, int placeholderResId, int errorResId) {
        DrawableRequestBuilder<String> requestBuilder = Glide.with(imageView.getContext()).load(url);

        if (errorResId > 0) {
            requestBuilder = requestBuilder.error(errorResId);
        }

        if (placeholderResId > 0) {
            requestBuilder = requestBuilder.placeholder(placeholderResId);
        } else if (placeholderResId < 0) {
            requestBuilder = requestBuilder.dontAnimate();
        }

        requestBuilder = requestBuilder.centerCrop();
        requestBuilder.into(imageView);
    }

    public static Uri getAssetURI(String assetPath) {
        return Uri.parse("asset:///" + assetPath);
    }

    public static Uri getDrawableURI(Context context, int drawableId) {
        String url = "res://" + context.getPackageName() + "/" + drawableId;
        return Uri.parse(url);
    }

    public static Uri getFileURI(String path) {
        return Uri.parse("file://" + path);
    }
}