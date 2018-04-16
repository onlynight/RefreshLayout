package com.github.onlynight.refreshlayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.onlynight.refreshlayout.RefreshLayout;

public class MainActivity extends AppCompatActivity {

    RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = findViewById(R.id.refresh_layout);
//        refreshLayout.setRefreshing(true);
        refreshLayout.setRefreshing(true);
    }

    public void refresh(View view) {
        refreshLayout.setRefreshing(true);
    }
}
