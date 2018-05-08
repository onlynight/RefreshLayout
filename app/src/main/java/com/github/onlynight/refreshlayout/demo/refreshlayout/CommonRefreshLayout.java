package com.github.onlynight.refreshlayout.demo.refreshlayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.github.onlynight.refreshlayout.RefreshLayout;
import com.github.onlynight.refreshlayout.demo.R;
import com.github.onlynight.refreshlayout.demo.refreshlayout.header.HeaderView;

public class CommonRefreshLayout extends RefreshLayout {

    public CommonRefreshLayout(Context context) {
        super(context);
        initView();
    }

    public CommonRefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CommonRefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public CommonRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setHeaderView(new HeaderView(getContext()));
        setEmptyView(R.layout.part_empty);
    }

}
