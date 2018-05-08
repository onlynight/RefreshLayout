package com.github.onlynight.refreshlayout.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onScrollViewDemoClick(View view) {
        startActivity(new Intent(this, ScrollViewDemoActivity.class));
    }

    public void onViewDemoClick(View view) {
        startActivity(new Intent(this, ViewDemoActivity.class));
    }

    public void onRecyclerViewDemoClick(View view) {
        startActivity(new Intent(this, RecyclerViewDemoActivity.class));
    }
}
