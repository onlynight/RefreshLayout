package com.github.onlynight.refreshlayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.onlynight.refreshlayout.RefreshLayout;
import com.github.onlynight.refreshlayout.demo.refreshlayout.CommonRefreshLayout;

public class ViewDemoActivity extends AppCompatActivity implements RefreshLayout.OnRefreshListener {

    CommonRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_demo);

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setRefreshing(true);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefreshing(RefreshLayout refreshLayout) {
        refreshLayout.setRefreshing(false);
    }

}
