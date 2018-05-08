package com.github.onlynight.refreshlayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.onlynight.refreshlayout.RefreshLayout;
import com.github.onlynight.refreshlayout.demo.adapter.TestAdapter;

public class RecyclerViewDemoActivity extends AppCompatActivity implements RefreshLayout.OnRefreshListener {

    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_demo);

        refreshLayout = findViewById(R.id.refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);

        adapter = new TestAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setRefreshing(true);

    }

    @Override
    public void onRefreshing(RefreshLayout refreshLayout) {
        refreshLayout.setRefreshing(false);
    }

}
