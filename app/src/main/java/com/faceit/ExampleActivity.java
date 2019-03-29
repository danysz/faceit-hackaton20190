package com.faceit;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.faceit.data.FaceData;
import com.faceit.global.Global;

public class ExampleActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.setFullScreen(this);
        setContentView(R.layout.activity_example);
        MainActivity.getPicture(this,"L1P1", REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            FaceData face = data.getParcelableExtra("face_data");
        }
    }
}
