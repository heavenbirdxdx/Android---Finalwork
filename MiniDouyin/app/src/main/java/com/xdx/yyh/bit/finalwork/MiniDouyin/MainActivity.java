package com.xdx.yyh.bit.finalwork.MiniDouyin;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.xdx.yyh.bit.finalwork.MiniDouyin.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressLint("SdCardPath")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int REQUEST_CODE = 11;
    private static final int REQUST_CODE_CAMERA_AUDIO_STORAGE = 1;
    private ImageView portrait;
    private Button btn_portrait;
    private Bitmap bitmap_portrait;
    private static String path = "/sdcard/finger";
    private Uri portraitUri;
    private File imgFile;

    private static final String TAG = "ChangePortrait";
    private static final int REQUEST_PORTRAIT_CAMERA = 1;
    private static final int REQUEST_PORTRAIT_STORAGE = 2;
    private static final int GRANT_PERMISSION = 3;
    private static final int REQUEST_EXTERNAL_STORAGE = 101;
    private String[] mPermissionsArrays = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        -------By yxd ----------------
//        播放动画
        LottieAnimationView fab = findViewById(R.id.fab);

        fab.playAnimation();
        ObjectAnimator alphaAnimationView = ObjectAnimator.ofFloat( fab,
                "alpha",1.0f,0.0f
        );
//        ------------By yxd------------

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ||ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]
                                    {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                            REQUEST_CODE);
//                        startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));

                    }
                else {
                    startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        -------By yxd----------------------------
//        更换头像
        portrait = findViewById(R.id.portrait);
        btn_portrait = findViewById(R.id.btn_portrait);

        btn_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                跳出弹窗选择, 选择是拍照还是选照片
                final String[] items = new String[]{"拍照", "从相册中选"};
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("更换头像")
                        .setIcon(R.mipmap.reminder)
                        .setItems(items, new DialogInterface.OnClickListener() {//添加列表
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
                               switch (items[i]){
                                   case "拍照":
//                                       自定义相机
//                                       Intent intentCamera = new Intent();
//                                       intentCamera.setClass(MainActivity.this,PortraitCameraActivity.class);
//                                       startActivityForResult(intentCamera,1);

//                                         调用系统相机
                                       Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                       imgFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
                                       if(imgFile != null){
                                           Uri fileUri =
                                                   FileProvider.getUriForFile(MainActivity.this,"com.xdx.yyh.bit.finalwork.MiniDouyin",imgFile);
                                           takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                                       }
                                       startActivityForResult(takePictureIntent, REQUEST_PORTRAIT_CAMERA);

                                       break;
                                   case "从相册中选":
                                       Intent intentAlbum = new Intent(Intent.ACTION_PICK, null);
                                       intentAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                       startActivityForResult(Intent.createChooser(intentAlbum, "Select Picture"), REQUEST_PORTRAIT_STORAGE);
                                       break;
                                   default:
                                       break;
                               }
//                                Toast.makeText(MainActivity.this, "点的是：" + items[i], Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

//        -------By yxd-----------------------------
    }

// -------------By yxd --------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_PORTRAIT_CAMERA) {
//                cropPhoto(Uri.fromFile(imgFile));
//                //todo 根据imageView裁剪
                int targetW = portrait.getWidth();
                int targetH = portrait.getHeight();
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                //todo 根据缩放比例读取文件，生成Bitmap
                Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
                //todo 如果存在预览方向改变，进行图片旋转
                try {
                    bmp = rotateImage(bmp, imgFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                portrait.setImageBitmap(bmp);


            }
            else if (requestCode == REQUEST_PORTRAIT_STORAGE) {
                cropPhoto(data.getData());
            }
            else if(requestCode == 3){
                if (data != null) {
                    bitmap_portrait = data.getExtras().getParcelable("data");
                    if(bitmap_portrait!=null){
                        /**
                         * 上传服务器代码
                         */
//                        setPicToView(bitmap_portrait);//保存在SD卡中
                        portrait.setImageBitmap(bitmap_portrait);//用ImageView显示出来
                    }
                }
            }

        }
    }


    /**
     * 调用系统的裁剪
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);

        this.startActivityForResult(intent, 3);
    }

//    有些错误，未调用，备用
    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName =path + "portrait.jpg";//图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

//    -------------By yxd ----------------


    public void watchVideo() {
        startActivity(new Intent(this, VideoWatch.class));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            watchVideo();
        } else if (id == R.id.nav_gallery) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this,
                        new String[]
                                {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        REQUEST_CODE);
            else {
                startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));
            }
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }

        switch (requestCode) {
            case REQUST_CODE_CAMERA_AUDIO_STORAGE: {
                for (int i = 0; i < grantResults.length - 1; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_LONG).show();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
    }

    public static Bitmap rotateImage(Bitmap bitmap, String path) throws Exception{
        ExifInterface srcExif = new ExifInterface(path);
        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch(orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 80;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            default:
                break;
        }
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
