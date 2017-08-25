package com.talon.camerademo;

import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by 003 on 2016-12-13.
 */
public class AlbumDirAdapter extends BaseQuickAdapter<AlbumDirModel, BaseViewHolder> {
    private OnItemClickListener itemClickListener;

    public AlbumDirAdapter(List<AlbumDirModel> data, OnItemClickListener itemClickListener) {
        super(R.layout.album_dir_item, data);
        this.itemClickListener = itemClickListener;
    }

    @Override
    protected void convert(BaseViewHolder holder, final AlbumDirModel dirModel) {
        ImageView draweeView = holder.getView(R.id.imageView);
        Uri uri = dirModel.getFirstUri();
//        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).build();
//        DraweeController controller = Fresco.newDraweeControllerBuilder().setOldController(draweeView.getController()).setImageRequest(request).build();
//        draweeView.setController(controller);
        Glide.with(mContext).load(uri).into(draweeView);
        //直接通过holder.addOnClickListener(R.id.itemView)添加事件是没有点击效果的！！！
        holder.getView(R.id.itemView).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(dirModel);
                }
            }
        });
        int photoCount = dirModel.getPhotoCount();
        String name = dirModel.getName();

        if (photoCount > 0) {
            holder.setText(R.id.txtName, CommonUtil.fillString(mContext, R.string.format_name_count, name, photoCount));
        } else {
            holder.setText(R.id.txtName, name);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AlbumDirModel dirModel);
    }
}