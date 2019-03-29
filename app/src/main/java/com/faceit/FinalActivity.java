package com.faceit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;

import com.faceit.data.FaceData;
import com.faceit.data.Result;
import com.faceit.global.Global;

import java.io.File;
import java.util.ArrayList;

public class FinalActivity extends Activity {

    private ImageView smileOne;
    private ImageView smileTwo;
    private ImageView pictureOne;
    private ImageView pictureTwo;
    private ImageView successOne;
    private ImageView successTwo;
    private ImageView next;

    public static void openActivity(Context context,ArrayList<FaceData> faces){
        Intent intent = new Intent(context,FinalActivity.class);
        intent.putParcelableArrayListExtra("faces",faces);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.setFullScreen(this);
        setContentView(R.layout.activity_final);

        smileOne = findViewById(R.id.smileOne);
        smileTwo = findViewById(R.id.smileTwo);
        pictureOne = findViewById(R.id.pictureOne);
        pictureTwo = findViewById(R.id.pictureTwo);
        successOne = findViewById(R.id.successOne);
        successTwo = findViewById(R.id.successTwo);
        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(FinalActivity.this, ShowSmilesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        smileOne.setImageResource(R.drawable.face_one_final);
        smileTwo.setImageResource(R.drawable.face_two_final);
        successOne.setImageResource(R.drawable.v);
        successTwo.setImageResource(R.drawable.x);

        try{
            File photo1 = new File(Environment.getExternalStorageDirectory(), "FaceIt/" + "L1P1" + ".jpg");
            Uri imageUri1 = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photo1);
            Bitmap bitmap = Global.DecodeBitmapUri(this, imageUri1);

            File photo2 = new File(Environment.getExternalStorageDirectory(), "FaceIt/" + "L1P2" + ".jpg");
            Uri imageUri2 = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photo2);
            Bitmap bitmap2 = Global.DecodeBitmapUri(this, imageUri2);

            pictureOne.setImageBitmap(bitmap);
            pictureTwo.setImageBitmap(bitmap2);
        }catch (Exception e){
        }

//        successOne.setImageResource(R.drawable.v);
//        successTwo.setImageResource(R.drawable.v);

        if(getIntent() != null && getIntent().getParcelableArrayListExtra("faces") != null){
            ArrayList<FaceData> faces = getIntent().getParcelableArrayListExtra("faces");

            ArrayList<Result> results = new ArrayList<>();

            FaceData faceData = faces.get(0);
            boolean isSame = faceData.equals(true,FaceData.EyeValue.CLOSE,FaceData.EyeValue.OPEN);
            results.add(new Result(faceData,"L1P2", isSame));

            if(isSame){
                successOne.setImageResource(R.drawable.v);
            }else{
                successOne.setImageResource(R.drawable.x);
            }

            faceData = faces.get(1);
            isSame = faceData.equals(true,FaceData.EyeValue.OPEN,FaceData.EyeValue.OPEN);
            results.add(new Result(faceData,"L1P1", isSame));

            if(isSame){
                successTwo.setImageResource(R.drawable.v);
            }else{
                successTwo.setImageResource(R.drawable.x);
            }
        }
    }
}
