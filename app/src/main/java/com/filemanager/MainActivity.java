package com.filemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.filemanager.picture.bean.FileRelay;
import com.filemanager.picture.interfaces.OnFilePathListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileRelay.startActivtiy(MainActivity.this, "wps", new OnFilePathListener() {
                    @Override
                    public void onFilePath(String path) {
                        Toast.makeText(MainActivity.this,path,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
