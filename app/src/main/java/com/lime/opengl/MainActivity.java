package com.lime.opengl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.opengl_2).setOnClickListener(MainActivity.this);
        findViewById(R.id.opengl_3).setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.opengl_2: {
                intent = new Intent(MainActivity.this, GL2ItemsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.opengl_3: {
                intent = new Intent(MainActivity.this, GL3ItemsActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
