package com.example.administrator.boxingdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.config.BoxingCropOption;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.utils.BoxingFileHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1024;
    private ImageView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BoxingMediaLoader.getInstance().init(new BoxingGlideLoader());
        BoxingCrop.getInstance().init(new BoxingUcrop());
        head = (ImageView) findViewById(R.id.icon_head);
    }

    public void pickIcon(View view) {
        String cachePath = BoxingFileHelper.getCacheDir(this);
        if (TextUtils.isEmpty(cachePath)) {
            Toast.makeText(getApplicationContext(), R.string.boxing_storage_deny, Toast.LENGTH_SHORT).show();
            return;
        }
        Uri destUri = new Uri.Builder()
                .scheme("file")
                .appendPath(cachePath)
                .appendPath(String.format(Locale.US, "%s.png", System.currentTimeMillis()))
                .build();
        BoxingConfig singleCropImgConfig = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).needCamera(R.mipmap.camera_white).withCropOption(new BoxingCropOption(destUri))
                .withMediaPlaceHolderRes(R.mipmap.ic_default_image);
        Boxing.of(singleCropImgConfig).withIntent(this, MyBoxingActivity.class).start(this, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final ArrayList<BaseMedia> medias = Boxing.getResult(data);
            if (requestCode == REQUEST_CODE && medias != null && medias.size() > 0) {
                BaseMedia baseMedia = medias.get(0);
                String path = baseMedia.getPath();
                Log.e("onActivityResult", "onActivityResult: " + path);
                Glide.with(this)
                        .load(path)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.mipmap.user_icon)
                        .placeholder(R.mipmap.user_icon)
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(head);

            }
        }
    }
}
