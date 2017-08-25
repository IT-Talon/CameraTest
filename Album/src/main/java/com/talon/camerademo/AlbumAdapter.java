package com.talon.camerademo;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Talon on 2017-8-25.
 */

public class AlbumAdapter extends BaseQuickAdapter<AlbumModel, BaseViewHolder>
{
    private RecyclerView recyclerView;

    private OnPhotoClickListener onPhotoClickListener;

    public AlbumAdapter(RecyclerView recyclerView, List<AlbumModel> data, OnPhotoClickListener onPhotoClickListener)
    {
        super(R.layout.album_item, data);
        this.recyclerView = recyclerView;
        this.onPhotoClickListener = onPhotoClickListener;
    }

    @Override
    protected void convert(BaseViewHolder holder, final AlbumModel albumModel)
    {
        ImageView imageView = holder.getView(R.id.imageView);
        Uri uri = albumModel.getUri();
        int itemHeight = (AppContext.getInstance().getScreenWidth() - recyclerView.getPaddingLeft() - recyclerView.getPaddingRight()) / 3;
        int imageSize = (int)(itemHeight * 0.75f);
        holder.getConvertView().getLayoutParams().height = itemHeight;
//        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setResizeOptions(new ResizeOptions(imageSize, imageSize)).build();
//        DraweeController controller = Fresco.newDraweeControllerBuilder().setOldController(draweeView.getController()).setImageRequest(request).build();
//        draweeView.setController(controller);

        Glide.with(mContext).load(uri).into(imageView);

        if(onPhotoClickListener != null)
        {
            imageView.setOnClickListener(new OnClickListener()
            {
                public void onClick(View view)
                {
                    onPhotoClickListener.onPhotoClick(albumModel);
                }
            });
        }
    }

    public interface OnPhotoClickListener
    {
        void onPhotoClick(AlbumModel albumModel);
    }
}