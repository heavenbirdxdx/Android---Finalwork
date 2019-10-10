package com.xdx.yyh.bit.finalwork.MiniDouyin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.facebook.stetho.Stetho;
import com.google.android.material.navigation.NavigationView;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.LoginPerson;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.Person;
import com.xdx.yyh.bit.finalwork.MiniDouyin.database.OperatorHelper;
import com.xdx.yyh.bit.finalwork.MiniDouyin.database.PersonDb;
import com.xdx.yyh.bit.finalwork.MiniDouyin.database.PersonDbHelper;
import com.xdx.yyh.bit.finalwork.MiniDouyin.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressLint("SdCardPath")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int REQUEST_CODE = 11;
    private static final int REQUST_CODE_CAMERA_AUDIO_STORAGE = 1;
    private ImageView portrait;
    private LottieAnimationView  btn_portrait;
    private Bitmap bitmap_portrait;
    private static String path = "/sdcard/finger";
    private File imgFile;
    private TextView tv_num;
    private TextView tv_name;
    private  TextView tv_birth;
    private  TextView tv_date;
    public LoginPerson loginPerson;
    private ImageView portrait2;
    private TextView tv_name2;
    private TextView tv_birth2;

    private  NavigationView navigationView ;


    private PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
    private OperatorHelper mOperatorHelper = new OperatorHelper(this);

    private static final String TAG = "ChangePortrait";

    private static final int REQUEST_PORTRAIT_CAMERA = 1;
    private static final int REQUEST_PORTRAIT_STORAGE = 2;
    private static final int REQUEST_PORTRAIT_CROP_RRTURN = 3;
    private static final int REQUEST_PERSONINFO_CHANGE = 4;
    private static final int REQUEST_DATE_CHANGE = 5;

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
        Stetho.initializeWithDefaults(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

//        -------By yxd ----------------

        tv_num = findViewById(R.id.num);
        tv_name = findViewById(R.id.name);
        tv_birth = findViewById(R.id.birth);
        tv_date = findViewById(R.id.date);
        portrait2 = headerLayout.findViewById(R.id.portrait2);
        tv_name2 = headerLayout.findViewById(R.id.tv_name2);
        tv_birth2 = headerLayout.findViewById(R.id.tv_birth2);
        LottieAnimationView fab = findViewById(R.id.fab);
//        全局变量
        loginPerson = (LoginPerson) getApplication();

//        判断权限
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
                            {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                    REQUEST_CODE);
//                        startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, CustomCameraActivity.class),REQUEST_DATE_CHANGE);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        网络连接
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "网络未连接，请查看网络",
                    Toast.LENGTH_LONG).show();
        }


//        更换头像
        portrait = findViewById(R.id.portrait);
        btn_portrait = findViewById(R.id.btn_portrait);

        btn_portrait.setOnClickListener(new View.OnClickListener() {
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

//        预先加入
        if(!mOperatorHelper.isExist(loginPerson.getLoginPerson().getNum())){
            SQLiteDatabase db = mPersonDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            values.put(PersonDb.PersonInfo.COLUMN_NUM_TITLE,"1120172169");
            values.put(PersonDb.PersonInfo.COLUMN_NAME_TITLE, "杨训迪");
            values.put(PersonDb.PersonInfo.COLUMN_BIRTHDAY_TITLE,"1999-11-30");
            values.put(PersonDb.PersonInfo.COLUMN_DATE_TITLE, timeStamp);
            values.put(PersonDb.PersonInfo.COLUMN_PORTRAIT_TITLE,"R.mipmap.yellowchic");
            long newRowId = db.insert(PersonDb.PersonInfo.TABLE_NAME,null,values);
        }

//            启动时，将数据库中的loginPerson.getLoginPerson().getNum()的信息加载进来
        List<Person> people = mOperatorHelper.getPersonInfoFromDatabase(loginPerson.getLoginPerson().getNum());

        if(people.size() > 0){
            Person person = people.get(0);
            loginPerson.setLoginPerson(person);
            tv_num.setText(loginPerson.getLoginPerson().getNum());
            tv_name.setText(loginPerson.getLoginPerson().getName());
            tv_birth.setText(loginPerson.getLoginPerson().getBirth());
            tv_date.setText(loginPerson.getLoginPerson().getDate());
            tv_name2.setText(loginPerson.getLoginPerson().getName());
            tv_birth2.setText(loginPerson.getLoginPerson().getBirth());

            Bitmap bmp;
            String portraitPath =loginPerson.getLoginPerson().getPortrait();
            if(portraitPath.equals(new String ("R.mipmap.yellowchic"))){
                bmp=BitmapFactory.decodeResource(getResources(),R.mipmap.yellowchic);
            }
            else{
                bmp = BitmapFactory.decodeFile(portraitPath);
            }
            portrait.setImageBitmap(bmp);
            portrait2.setImageBitmap(bmp);
        }
        else{
            Toast.makeText(getApplicationContext(), "数据库加载错误",
                    Toast.LENGTH_LONG).show();
        }

    }

// -------------By yxd --------------------

//    接受点击更换头像后的返回信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_PORTRAIT_CAMERA) {
                if(mOperatorHelper.updatePersonPortraitFromDatabase(imgFile.getAbsolutePath(),loginPerson.getLoginPerson().getNum())){
                    loginPerson.getLoginPerson().setPortrait(imgFile.getAbsolutePath());
                    cropPhoto(getImageContentUri(this,imgFile));
                }
                else{
                    Toast.makeText(getApplicationContext(), "更换错误!!!",
                            Toast.LENGTH_LONG).show();
                }


            }
            else if (requestCode == REQUEST_PORTRAIT_STORAGE) {

//                更新本地数据库
                Uri imgUri = data.getData();
                String imgPath = getImagePath(imgUri,null);
                if(mOperatorHelper.updatePersonPortraitFromDatabase(imgPath,loginPerson.getLoginPerson().getNum())){
                    loginPerson.getLoginPerson().setPortrait(imgPath);
                    cropPhoto(imgUri);
                }
                else{
                    Toast.makeText(getApplicationContext(), "更换错误!!!",
                            Toast.LENGTH_LONG).show();
                }
            }
            else if(requestCode == REQUEST_PORTRAIT_CROP_RRTURN){
                if (data != null) {
                    bitmap_portrait = data.getExtras().getParcelable("data");
                    if(bitmap_portrait!=null){
                        /**
                         * 上传服务器代码
                         */
//                        setPicToView(bitmap_portrait);//保存在SD卡中
                        portrait.setImageBitmap(bitmap_portrait);//用ImageView显示出来
                        portrait2.setImageBitmap(bitmap_portrait);
                    }
                }
            }
            else if(requestCode == REQUEST_PERSONINFO_CHANGE){
                tv_num.setText(loginPerson.getLoginPerson().getNum());
                tv_name.setText(loginPerson.getLoginPerson().getName());
                tv_birth.setText(loginPerson.getLoginPerson().getBirth());
                tv_date.setText(loginPerson.getLoginPerson().getDate());
                tv_name2.setText(loginPerson.getLoginPerson().getName());
                tv_birth2.setText(loginPerson.getLoginPerson().getBirth());

                Bitmap bmp;
                String portraitPath =loginPerson.getLoginPerson().getPortrait();
                if(portraitPath.equals(new String ("R.mipmap.yellowchic"))){
                    bmp=BitmapFactory.decodeResource(getResources(),R.mipmap.yellowchic);
//                portrait.setImageBitmap(bmp);
//                portrait2.setImageBitmap(bmp);

                }
                else{
                    bmp = BitmapFactory.decodeFile(portraitPath);
//                cropPhoto(Uri.fromFile(new File(portraitPath)));
                }
                portrait.setImageBitmap(bmp);
                portrait2.setImageBitmap(bmp);
            }
            else if(requestCode == REQUEST_DATE_CHANGE){
                tv_date.setText(loginPerson.getLoginPerson().getDate());
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

        this.startActivityForResult(intent, REQUEST_PORTRAIT_CROP_RRTURN);
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


//    查看网络连接
    private boolean isNetworkAvailable() {

        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;


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
//                拍摄视频
                startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));
            }
        } else if (id == R.id.nav_setting) {
//            设置功能
            startActivityForResult(new Intent(MainActivity.this, ChangePersonInfo.class), REQUEST_PERSONINFO_CHANGE );
        } else if (id == R.id.nav_send) {
//            消息页面
            startActivity(new Intent(MainActivity.this, SendMsg.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    protected void onDestroy() {
        mPersonDbHelper.close();
        super.onDestroy();
    }


//    content://格式的Uri转path
    private String getImagePath(Uri uri,String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;

    }

//    -----------by yxd--------------
    /**
     * Gets the content:// URI  from the given corresponding path to a file
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
