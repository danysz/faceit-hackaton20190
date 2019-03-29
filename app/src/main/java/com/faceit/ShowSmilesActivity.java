package com.faceit;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.faceit.data.FaceData;
import com.faceit.global.Global;
import com.faceit.view.Preview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ShowSmilesActivity extends Activity {

    public static final int REQUEST_CODE = 32;
    private ArrayList<FaceData> faces;
    private int count = 4;
    private int currentItem = 2;
    private ImageView smile;
    private ImageView backgroundText;
    private ImageView time;
    private int MAX_COUNT;
    private boolean needToCounterEmoji;
    boolean needBackground = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.setFullScreen(this);
        setContentView(R.layout.activity_show_smiles);
        initViews();
        initObjects();
    }

    private void initViews() {
        smile = findViewById(R.id.smile);
        backgroundText = findViewById(R.id.backgroundText);
        time = findViewById(R.id.time);
    }

    private void initObjects() {
        needToCounterEmoji = true;
        MAX_COUNT = getIntent().getIntExtra("count", 3);
        count = MAX_COUNT;
        faces = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needToCounterEmoji) {
            needToCounterEmoji = false;
            setImageToEmojiNumber(currentItem);
            startCounterTime();
        }
    }

    private void startCounterTime() {
        MAX_COUNT = 3;
        new CountDownTimer(MAX_COUNT * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                counterFromThree();
            }

            public void onFinish() {
                currentItem -= 1;
                if (currentItem == 0) {
                    currentItem = 2;

                    smile.setImageResource(R.drawable.background);
                    startCounterFaces(0);
                    time.setImageBitmap(null);
                } else {
                    count = MAX_COUNT;
                    setImageToEmojiNumber(currentItem);
                    startCounterTime();
                }
            }
        }.start();
    }

    private void startCounterFaces(int countText) {
        MAX_COUNT = 5;
        count = MAX_COUNT;
        backgroundText.setVisibility(View.VISIBLE);

        if(countText == 0){
            backgroundText.setImageResource(R.drawable.one_text);
        }else if(countText == 1){
            backgroundText.setImageResource(R.drawable.two_text);
        }

        new CountDownTimer(MAX_COUNT * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                counterFromFive();
            }

            public void onFinish() {
                MainActivity.getPicture(ShowSmilesActivity.this, "L1P" + currentItem, REQUEST_CODE);

            }
        }.start();
    }

    private void counterFromThree() {
        int drawable = 0;
        switch (count) {
            case 3:
                drawable = R.drawable.three;
                break;
            case 2:
                drawable = R.drawable.two;
                break;
            case 1:
                drawable = R.drawable.one;
                break;
        }
        time.setImageResource(drawable);
        count -= 1;

    }

    private void counterFromFive() {
        int drawable = 0;
        switch (count) {
            case 5:
                drawable = R.drawable.five;
                break;
            case 4:
                drawable = R.drawable.four;
                break;
        }
        if (drawable != 0) {
            time.setImageResource(drawable);
            count -= 1;
        } else {
            counterFromThree();
        }
    }

    private void setImageToEmojiNumber(int number) {
        smile.setImageDrawable(getDrawableByName("emoji_v" + number));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (needBackground){
            needBackground = false;
            smile.setImageResource(R.drawable.background);
        }
    }

    private Drawable getDrawableByName(String name) {
        Resources resources = this.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", this.getPackageName());
        return resources.getDrawable(resourceId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            FaceData face = data.getParcelableExtra("face_data");
            faces.add(face);
            currentItem -= 1;
            if (currentItem <= 0) {
                FinalActivity.openActivity(this, faces);
            } else {
                count = MAX_COUNT;
                startCounterFaces(1);
            }
        }
    }
}
