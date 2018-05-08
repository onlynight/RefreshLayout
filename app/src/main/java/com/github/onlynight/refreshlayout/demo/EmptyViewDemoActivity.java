package com.github.onlynight.refreshlayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.onlynight.refreshlayout.RefreshLayout;

public class EmptyViewDemoActivity extends AppCompatActivity {

    private RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_view_demo);

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setEmptyView(R.layout.part_empty);
        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefreshing(RefreshLayout refreshLayout) {
                refreshLayout.setRefreshing(false);
                refreshLayout.setEmptyViewVisible(true);
            }
        });

        refreshLayout.setRefreshing(true);
    }
}
