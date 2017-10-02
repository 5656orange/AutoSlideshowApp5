package jp.techacademy.saki.hanai.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class AutoSlideshowApp extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    Button mForwardButton;
    Button mBackButton;
    Button mRStartStopButton;
    Cursor mCursor;
    int mStartStopFlag = 0;

    Handler mHandler = new Handler();
    Timer mTimer;
    double mTimerSec = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_slideshow_app);
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
        mForwardButton = (Button) findViewById(R.id.button_forward);
        mBackButton = (Button) findViewById(R.id.button_back);
        mRStartStopButton = (Button) findViewById(R.id.button_start_stop);

        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextImage();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreviousImage();
            }
        });

        mRStartStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartStopFlag == 0) {
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setNextImage();
                                    }
                                });
                            }
                        }, 100, 2000);
                    }
                    mStartStopFlag =1;
                    mRStartStopButton.setText("停止");
                    mForwardButton.setEnabled(false);
                    mBackButton.setEnabled(false);
                }else{
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                    mStartStopFlag =0;
                    mRStartStopButton.setText("自動再生");
                    mForwardButton.setEnabled(true);
                    mBackButton.setEnabled(true);
                }

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }else{
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
            default:
                break;
        }
    }
    private void getContentsInfo() {
        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        mCursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        Log.d("URI",""+mCursor.getCount());
        if (mCursor.moveToFirst()) {
            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = mCursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }else {
            mCursor.close();
        }

    }

    public void setNextImage(){
        if (mCursor.isLast()){
            mCursor.moveToFirst();
            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = mCursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);

        }else if (mCursor.moveToNext()) {
            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = mCursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }else {
            mCursor.close();
        }
    }

    public void setPreviousImage() {
        if (mCursor.isFirst()){
            mCursor.moveToLast();
            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = mCursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);

        }else if (mCursor.moveToPrevious()) {
            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = mCursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }else {
            mCursor.close();
        }

    }
}

