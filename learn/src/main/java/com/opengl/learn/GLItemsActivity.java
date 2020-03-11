package com.opengl.learn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GLItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glitems);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(GLItemsActivity.this, LinearLayoutManager.VERTICAL));
        List<String> items = new ArrayList<>();
        items.add(GlViewportRender.class.getName());
        items.add(GlDrawArraysRender.class.getName());
        items.add(GlDrawElementsRender.class.getName());
        adapter = new RecycleAdapter(GLItemsActivity.this, items);
        recyclerView.setAdapter(adapter);
    }
}
