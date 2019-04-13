package com.lime.opengl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lime.opengl.render.Example63Renderer;
import com.lime.opengl.render.Example66Renderer;
import com.lime.opengl.render.HelloTriangleRenderer;
import com.lime.opengl.render.MapBuffersRenderer;
import com.lime.opengl.render.MipMap2DRenderer;
import com.lime.opengl.render.MultiTextureRenderer;
import com.lime.opengl.render.ParticleSystemRenderer;
import com.lime.opengl.render.SimpleTexture2DRenderer;
import com.lime.opengl.render.SimpleTextureCubemapRenderer;
import com.lime.opengl.render.SimpleVertexShaderRenderer;
import com.lime.opengl.render.TextureWrapRenderer;
import com.lime.opengl.render.VAORenderer;
import com.lime.opengl.render.VBORenderer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecycleAdapter adapter;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
        List<String> items = new ArrayList<>();
        items.add(HelloTriangleRenderer.class.getName());
        items.add(Example63Renderer.class.getName());
        items.add(Example66Renderer.class.getName());
        items.add(MapBuffersRenderer.class.getName());
        items.add(VAORenderer.class.getName());
        items.add(VBORenderer.class.getName());
        items.add(SimpleVertexShaderRenderer.class.getName());
        items.add(MipMap2DRenderer.class.getName());
        items.add(SimpleTexture2DRenderer.class.getName());
        items.add(SimpleTextureCubemapRenderer.class.getName());
        items.add(TextureWrapRenderer.class.getName());
        items.add(MultiTextureRenderer.class.getName());
        items.add(ParticleSystemRenderer.class.getName());
        adapter = new RecycleAdapter(MainActivity.this, items);
        recyclerView.setAdapter(adapter);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
