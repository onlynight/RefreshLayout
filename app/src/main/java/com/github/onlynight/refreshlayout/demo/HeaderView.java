package com.github.onlynight.refreshlayout.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.onlynight.refreshlayout.RefreshLayout;

public class HeaderView extends RefreshLayout.RefreshHeaderView {

    private TextView mTextTitle;

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
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_header, this, false);
        mTextTitle = contentView.findViewById(R.id.text_content);
        addView(contentView);
    }

    @Override
    public void onStartRefresh() {
        mTextTitle.setText("onStartRefresh");
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
    }

}
