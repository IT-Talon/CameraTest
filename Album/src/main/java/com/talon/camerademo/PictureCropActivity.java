package com.talon.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PictureCropActivity extends Activity {
    View imageParent;
    ImageCropView cropView;
    Button btn;

    public static void startActivity(Activity activity, String picturePath, float croppedRatio) {
        Intent intent = new Intent(activity, PictureCropActivity.class);
        intent.putExtra(AppConstant.KEY_PICTURE_PATH, picturePath);
        intent.putExtra(AppConstant.KEY_CROPPED_RATIO, croppedRatio);
        activity.startActivityForResult(intent, AppConstant.REQUEST_CODE_IMAGE_CROPPED);
        activity.overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_crop);
        imageParent = findViewById(R.id.imageParent);
        cropView = findViewById(R.id.cropView);
        btn = findViewById(R.id.btn_ok);
        final Intent intent = this.getIntent();
        cropView.setImageRatio(intent.getFloatExtra(AppConstant.KEY_CROPPED_RATIO, 1));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOkClick(view);
            }
        });
        imageParent.post(new Runnable() {
            public void run() {
                int width = imageParent.getWidth();
                int height = imageParent.getHeight();
                float scale = AppConstant.MIN_IMAGE_SIDE / (float) width;

                if (scale < 1) {
                    width = (int) (width * scale);
                    height = (int) (height * scale);
                }

                cropView.setImageFilePath(intent.getStringExtra(AppConstant.KEY_PICTURE_PATH), width, height);
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_leftright, R.anim.out_leftright);
    }

    private void onOkClick(View view) {
        Bitmap croppedImage = cropView.getCroppedBitmap();

        if (croppedImage != null) {
            String dataKey = this.getClass().getName();
            AppCache.addData(dataKey, croppedImage);
            Intent data = new Intent();
            data.putExtra(AppCache.DATA_KEY, dataKey);
            data.putExtra(AppConstant.KEY_PICTURE_PATH, cropView.getImageFilePath());
            data.putExtra(AppConstant.KEY_CROP_RECT, cropView.getCropRect());
            this.setResult(RESULT_OK, data);
            this.finish();
        }
    }
}