package com.jx_linkcreate.productshow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.jx_linkcreate.productshow.transmitter.NetworkManager;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class ImagePreviewActivity extends AppCompatActivity {

    public final static String INTENT_KEY_PREVIEW_PATHS = "paths";

    private ArrayList<String> mUrl = new ArrayList<>();
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        handleIntent();

    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringArrayListExtra(INTENT_KEY_PREVIEW_PATHS);
        }

        mViewPager = findViewById(R.id.activity_image_preview_viewpager);
        mViewPager.setAdapter(new ImagePagerAdapter());
        CircleIndicator indicator = findViewById(R.id.activity_image_preview_indicator);
        indicator.setViewPager(mViewPager);
    }

    private String wrapUrlIfNotBeginWithHttp(String url) {
        String baseUrl = NetworkManager.getInstance(this).getBaseUrl();
        String wrappedUrl = baseUrl;
        if (!url.startsWith("http")) {
            wrappedUrl = baseUrl + url;
        }
        return wrappedUrl;
    }

    private class ImagePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mUrl.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            Context context = container.getContext();
            PhotoView photoView = new PhotoView(context);
            Glide.with(context)
                    .load(wrapUrlIfNotBeginWithHttp(mUrl.get(position)))
                    .into(photoView);

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
