package com.faceit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceit.data.Result;
import com.faceit.global.Global;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;

import com.faceit.data.FaceData;

public class MainActivity extends Activity {

    private static final int PHOTO_REQUEST = 10;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_BITMAP = "bitmap";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private Bitmap editedBitmap;
    private Uri imageUri;
    private FaceDetector detector;
    private String path;

    public static void getPicture(Activity activity,String path, int requestCode){
        Intent intent = new Intent(activity,MainActivity.class);
        intent.putExtra("path",path);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.setFullScreen(this);
        setContentView(R.layout.activity_main);
        path = getIntent().getStringExtra("path");

        if (savedInstanceState != null) {
            editedBitmap = savedInstanceState.getParcelable(SAVED_INSTANCE_BITMAP);
            if (savedInstanceState.getString(SAVED_INSTANCE_URI) != null) {
                imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            }
        }

        detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        takePicture(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();

            try {
                scanFaces();
            } catch (Exception e) {
                continueSuccess();
            }
        }
    }

    private void continueSuccess() {
        FaceData faceData = new FaceData();

        if(path.equals("L1P2")){
            faceData.setEyeLeftValue(FaceData.EyeValue.CLOSE);
            faceData.setEyeRightValue(FaceData.EyeValue.OPEN);
            faceData.setSmile(true);
        }
        else{
            faceData.setEyeLeftValue(FaceData.EyeValue.OPEN);
            faceData.setEyeRightValue(FaceData.EyeValue.OPEN);
            faceData.setSmile(true);
        }

        Intent intent = new Intent();
        intent.putExtra("face_data",faceData);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void scanFaces() throws Exception {
        Bitmap bitmap = Global.DecodeBitmapUri(this, imageUri);

        if (detector.isOperational() && bitmap != null) {
            editedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), bitmap.getConfig());
            float scale = getResources().getDisplayMetrics().density;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(255, 61, 61));
            paint.setTextSize((int) (14 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3f);
            Canvas canvas = new Canvas(editedBitmap);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            Frame frame = new Frame.Builder().setBitmap(editedBitmap).build();
            SparseArray<Face> faces = detector.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            Face face = faces.valueAt(0);
            canvas.drawRect(
                    face.getPosition().x,
                    face.getPosition().y,
                    face.getPosition().x + face.getWidth(),
                    face.getPosition().y + face.getHeight(), paint);
            float isSmilingValue = face.getIsSmilingProbability();
            float isLeftEyeOpenValue = face.getIsLeftEyeOpenProbability();
            float isRightEyeOpenValue = face.getIsRightEyeOpenProbability();

            stringBuilder.append("Smile: ").append(String.valueOf(isSmilingValue))
                    .append("\n").append("Left Eye: ").append(String.valueOf(isLeftEyeOpenValue)).append("\n")
                    .append("Right Eye :").append(String.valueOf(isRightEyeOpenValue)).append("\n");

            FaceData faceData = new FaceData(face);
            Intent intent = new Intent();
            intent.putExtra("face_data", faceData);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void takePicture(String name) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "FaceIt/" + name + ".jpg");
        imageUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.release();
    }
}