package com.lime.opengl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lime.opengl.render.es2.AirHockey1Renderer;
import com.lime.opengl.render.es2.AirHockey2Renderer;
import com.lime.opengl.render.es2.AirHockey3DRenderer;
import com.lime.opengl.render.es2.AirHockeyOrthoRenderer;
import com.lime.opengl.render.es2.AirHockeyTexturedRenderer;
import com.lime.opengl.render.es3.Example63Renderer;
import com.lime.opengl.render.es3.Example66Renderer;
import com.lime.opengl.render.es3.HelloTriangleRenderer;
import com.lime.opengl.render.es3.MapBuffersRenderer;
import com.lime.opengl.render.es3.MipMap2DRenderer;
import com.lime.opengl.render.es3.MultiTextureRenderer;
import com.lime.opengl.render.es3.ParticleSystemRenderer;
import com.lime.opengl.render.es3.SimpleTexture2DRenderer;
import com.lime.opengl.render.es3.SimpleTextureCubemapRenderer;
import com.lime.opengl.render.es3.SimpleVertexShaderRenderer;
import com.lime.opengl.render.es3.TextureWrapRenderer;
import com.lime.opengl.render.es3.VAORenderer;
import com.lime.opengl.render.es3.VBORenderer;

import java.util.ArrayList;
import java.util.List;

public class GL2ItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glitems);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(GL2ItemsActivity.this, LinearLayoutManager.VERTICAL));
        List<String> items = new ArrayList<>();
        items.add(AirHockey1Renderer.class.getName());
        items.add(AirHockey2Renderer.class.getName());
        items.add(AirHockey3DRenderer.class.getName());
        items.add(AirHockeyOrthoRenderer.class.getName());
        items.add(AirHockeyTexturedRenderer.class.getName());
        adapter = new RecycleAdapter(GL2ItemsActivity.this, items);
        recyclerView.setAdapter(adapter);
    }
}
