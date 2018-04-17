package com.github.onlynight.refreshlayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.onlynight.refreshlayout.RefreshLayout;
import com.github.onlynight.refreshlayout.demo.header.HeaderView;

public class MainActivity extends AppCompatActivity implements RefreshLayout.OnRefreshListener {

    RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setHeaderView(new HeaderView(this));
//        refreshLayout.setRefreshing(true);
//        refreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_stop_refreshing:
                refreshLayout.setRefreshing(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void temp(View view) {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefreshing(RefreshLayout refreshLayout) {
        Toast.makeText(this, "onRefreshing", Toast.LENGTH_SHORT).show();
    }
}
