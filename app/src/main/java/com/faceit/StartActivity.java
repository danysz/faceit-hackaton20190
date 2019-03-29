package com.faceit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.faceit.global.Global;

public class StartActivity extends Activity {
    public static final int DELAY_MILLIS = 600;
    ImageView imageView;
    ImageView play;
    ImageView rememberPicture;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.setFullScreen(this);
        setContentView(R.layout.activity_start);
        imageView = findViewById(R.id.imageView);
        play = findViewById(R.id.play);
        rememberPicture = findViewById(R.id.remember_picture);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGifFinished();
            }
        });

//        startActivity(new Intent(this, FinalActivity.class));
        startAnimationAnCounter();
    }

    private void startAnimationAnCounter() {
        Glide.with(this).asGif().load(R.drawable.boy)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onGifFinished();
                    }
                }, DELAY_MILLIS);
                return false;
            }

            @Override
            public boolean onResourceReady(final GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                resource.setLoopCount(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            try {
                                Thread.sleep(DELAY_MILLIS);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!resource.isRunning()) {
                                StartActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        play.setEnabled(true);
                                        play.setVisibility(View.VISIBLE);
                                        rememberPicture.setVisibility(View.VISIBLE);
                                    }
                                });
                                break;
                            }
                        }
                    }
                }).start();
                return false;
            }
        }).into(imageView);
    }

    private void onGifFinished() {
        Intent intent = new Intent(StartActivity.this, ShowSmilesActivity.class);
        startActivity(intent);
    }
}
