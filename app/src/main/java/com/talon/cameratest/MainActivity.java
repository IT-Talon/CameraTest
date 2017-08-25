package com.talon.cameratest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.talon.camerademo.AlbumActivity;
import com.talon.camerademo.AlbumModel;
import com.talon.camerademo.AppCache;
import com.talon.camerademo.AppConstant;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_img)
    ImageView mainImg;
    @BindView(R.id.main_btn)
    Button mainBtn;

    private boolean isScrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstant.REQUEST_CODE_ALBUM:
                    if (isScrop) {
                        String bitmapKey = data.getStringExtra(AppCache.DATA_KEY);
                        Bitmap bitmap = (Bitmap) AppCache.removeData(bitmapKey);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] bytes = baos.toByteArray();

                        Glide.with(MainActivity.this)
                                .load(bytes)
                                .into(mainImg);
                    } else {
                        AlbumModel album = (AlbumModel) data.getSerializableExtra(AppConstant.KEY_ALBUM_MODEL);
                        Glide.with(MainActivity.this).load(album.getUri()).into(mainImg);
                    }
                    break;
            }
        }
    }

    @OnClick({R.id.main_img, R.id.main_btn})
    public void onViewClicked(View view) {
        Log.d("Talon", "dsadadsa");
        switch (view.getId()) {
            case R.id.main_img:
                isScrop = false;
                // 不裁剪
                AlbumActivity.start(this);
                break;
            case R.id.main_btn:
                isScrop = true;
                // 裁剪
                AlbumActivity.start(this, 1);
                break;
        }
    }
}
