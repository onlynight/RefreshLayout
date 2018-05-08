package com.github.onlynight.refreshlayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
    }

    @Override
    public void onRefreshing(RefreshLayout refreshLayout) {
        Toast.makeText(this, "onRefreshing", Toast.LENGTH_SHORT).show();
        refreshLayout.setRefreshing(false);
    }

}
