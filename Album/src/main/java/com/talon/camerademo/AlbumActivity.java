package com.talon.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Created by 003 on 2016-12-12.
 */
public class AlbumActivity extends Activity implements AlbumAdapter.OnPhotoClickListener,View.OnClickListener,CompoundButton.OnCheckedChangeListener {
    View floatView;

    RecyclerView photoView;

    RecyclerView dirView;

    CheckBox btnCategory;

    private List<AlbumModel> allMedias, medias;

    private TreeMap<String, List<AlbumModel>> dirMap;

    private List<AlbumDirModel> dirs;

    private AlbumAdapter albumAdapter;

    private AlbumDirAdapter dirAdapter;

    private float croppedRatio;

    public static void start(Activity activity) {
        start(activity, 0);
    }

    public static void start(Activity activity, float croppedRatio) {
        Intent intent = new Intent(activity, AlbumActivity.class);
        intent.putExtra(AppConstant.KEY_CROPPED_RATIO, croppedRatio);
        activity.startActivityForResult(intent, AppConstant.REQUEST_CODE_ALBUM);
        activity.overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);
    }

    public static void start(Fragment fragment) {
        start(fragment, 0);
    }

    public static void start(Fragment fragment, float croppedRatio) {
        Activity activity = fragment.getActivity();

        if (activity != null) {
            Intent intent = new Intent(activity, AlbumActivity.class);
            intent.putExtra(AppConstant.KEY_CROPPED_RATIO, croppedRatio);
            fragment.startActivityForResult(intent, AppConstant.REQUEST_CODE_ALBUM);
            activity.overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        floatView = findViewById(R.id.floatView);
        photoView = findViewById(R.id.photoView);
        dirView = findViewById(R.id.dirView);
        btnCategory = findViewById(R.id.btnCategory);
        floatView.setOnClickListener(this);
        photoView.setOnClickListener(this);
        dirView.setOnClickListener(this);
        btnCategory.setOnCheckedChangeListener(this);
        this.croppedRatio = getIntent().getFloatExtra(AppConstant.KEY_CROPPED_RATIO, 0);
        initView();
        load();
    }

    private void initView() {
        this.albumAdapter = new AlbumAdapter(photoView, medias = new ArrayList<>(), this);
        photoView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        View footerView = new View(this);
        int footerHeight = CommonUtil.dp2px(this, 6);
        footerView.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, footerHeight));
        albumAdapter.addFooterView(footerView);
        photoView.setAdapter(albumAdapter);

        this.dirAdapter = new AlbumDirAdapter(dirs = new ArrayList<>(), new AlbumDirAdapter.OnItemClickListener() {
            public void onItemClick(AlbumDirModel dirModel) {
                String dirName = dirModel.getName();
                medias.clear();
                medias.addAll(dirMap.containsKey(dirName) ? dirMap.get(dirName) : allMedias);
                albumAdapter.notifyDataSetChanged();
                btnCategory.setText(dirModel.getName());
                btnCategory.setChecked(false);
            }
        });
        dirView.setLayoutManager(new LinearLayoutManager(this));
        int headerFooterHeight = CommonUtil.dp2px(this, 5);
        footerView = new View(this);
        footerView.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerFooterHeight));
        View headerView = new View(this);
        headerView.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerFooterHeight));
        dirAdapter.addFooterView(footerView);
        dirAdapter.addHeaderView(headerView);
        dirView.setAdapter(dirAdapter);
    }

    private void load() {
        this.dirMap = new TreeMap<>();
        this.allMedias = ResourceUtil.getAlbumPictures(this);

        if (croppedRatio == 0) {
            allMedias.addAll(ResourceUtil.getAlbumVedios(this));
        }

        String bucketDisplayName;
        List<AlbumModel> list;
        AlbumDirModel dirModel;

        for (AlbumModel item : allMedias) {
            bucketDisplayName = item.getBucketDisplayName();
            list = dirMap.get(bucketDisplayName);

            if (list == null) {
                list = new ArrayList<>();
                dirMap.put(bucketDisplayName, list);
            }

            list.add(item);
        }

        allMedias.clear();

        //这里再次遍历是为了排序，dirMap本身是按Key的自然顺序来存放数据的
        for (Entry<String, List<AlbumModel>> entry : dirMap.entrySet()) {
            dirModel = new AlbumDirModel();
            list = entry.getValue();
            dirModel.setName(entry.getKey());
            dirModel.setPhotoCount(list.size());
            dirModel.setFirstMediaPath(list.get(0).getPath());
            dirs.add(dirModel);
            allMedias.addAll(list);
        }

        dirModel = new AlbumDirModel();
        dirModel.setName(ResourceUtil.getString(this, R.string.all_photos));
        if (!allMedias.isEmpty()) {
            dirModel.setPhotoCount(allMedias.size());
            dirModel.setFirstMediaPath(allMedias.get(0).getPath());
            dirs.add(0, dirModel);
            medias.addAll(allMedias);
            dirAdapter.notifyDataSetChanged();
            albumAdapter.notifyDataSetChanged();
            int dirRows = Math.min(4, dirs.size());
            dirView.getLayoutParams().height = CommonUtil.dp2px(this, 60 * dirRows + 10);
        }
    }

    @Override
    public void onPhotoClick(AlbumModel albumModel) {
        if (croppedRatio == 0) {
            Intent data = new Intent();
            data.putExtra(AppConstant.KEY_ALBUM_MODEL, albumModel);
            setResult(RESULT_OK, data);
            finish();
        } else {
            PictureCropActivity.startActivity(this, albumModel.getPath(), croppedRatio);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppConstant.REQUEST_CODE_IMAGE_CROPPED: {
                if (resultCode == RESULT_OK) {
                    this.setResult(RESULT_OK, data);
                    this.finish();
                }

                break;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_leftright, R.anim.out_leftright);
    }


    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btnBack) {
            this.onBackPressed();
        } else if (i == R.id.floatView) {
            btnCategory.setChecked(false);
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.btnCategory) {
            if (isChecked) {
                floatView.setVisibility(View.VISIBLE);
                floatView.setBackgroundColor(0x80494949);
                floatView.setClickable(true);
                dirView.setVisibility(View.VISIBLE);
                dirView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.in_topbottom));
            } else {
                floatView.setBackgroundColor(0);
                floatView.setClickable(false);
                dirView.setVisibility(View.INVISIBLE);
                dirView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.out_bottomtop));
            }
        }
    }

}