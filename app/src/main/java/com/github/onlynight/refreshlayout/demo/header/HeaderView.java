package com.github.onlynight.refreshlayout.demo.header;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.github.onlynight.refreshlayout.RefreshLayout;
import com.github.onlynight.refreshlayout.demo.R;

public class HeaderView extends RefreshLayout.RefreshHeaderView {

    private TextView mTextTitle;
    private SimpleDraweeView mImgLoading;

    public HeaderView(@NonNull Context context) {
        super(context);
        initView();
    }

    public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(21)
    public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_header, this, false);
        mTextTitle = contentView.findViewById(R.id.text_content);
        mImgLoading = contentView.findViewById(R.id.img_loading);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse("res:// /" + R.drawable.gif_refreshing)).build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request).setAutoPlayAnimations(true)
                .setOldController(mImgLoading.getController())
                .build();
        mImgLoading.setController(controller);

        addView(contentView);
    }

    @Override
    public void onStartRefresh() {
        mTextTitle.setText("onStartRefresh");
        try {
            mImgLoading.getController().getAnimatable().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReleaseToRefresh() {
        mTextTitle.setText("onReleaseToRefresh");
    }

    @Override
    public void onRefreshing() {
        mTextTitle.setText("onRefreshing");
    }

    @Override
    public void onFinishRefresh() {
        mTextTitle.setText("onFinishRefresh");
    }

    @Override
    public void onBackToOriginalState() {
        mTextTitle.setText("onBackToOriginalState");
        mImgLoading.clearAnimation();
        try {
            mImgLoading.getController().getAnimatable().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel() {
    }

}
