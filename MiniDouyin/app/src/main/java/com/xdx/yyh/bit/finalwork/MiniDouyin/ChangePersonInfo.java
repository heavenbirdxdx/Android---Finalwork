package com.xdx.yyh.bit.finalwork.MiniDouyin;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.LoginPerson;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.Person;
import com.xdx.yyh.bit.finalwork.MiniDouyin.database.OperatorHelper;
import com.xdx.yyh.bit.finalwork.MiniDouyin.database.PersonDbHelper;
import com.xdx.yyh.bit.finalwork.MiniDouyin.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChangePersonInfo extends AppCompatActivity {

    private EditText editName;
    private EditText editNum;
    private EditText editBirth;
    private LottieAnimationView btn_portrait;
    private LottieAnimationView btn_personInfo;
    private ImageView portrait;

    private File imgFile;

    private static final int REQUEST_PORTRAIT_CAMERA = 1;
    private static final int REQUEST_PORTRAIT_STORAGE = 2;
    private static final int REQUEST_PORTRAIT_CROP_RRTURN = 3;
    private static final int REQUEST_PERSONINFO_CHANGE = 4;
    public LoginPerson loginPerson;

    private PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
    private OperatorHelper mOperatorHelper = new OperatorHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_person_info);

        editNum = findViewById(R.id.edit_num);
        editName = findViewById(R.id.edit_name);
        editBirth = findViewById(R.id.edit_birth);
        btn_portrait = findViewById(R.id.btn_portrait);
        btn_personInfo = findViewById(R.id.btn_save);
        loginPerson = (LoginPerson)getApplication();
        portrait = findViewById(R.id.portrait);


        editNum.setText(loginPerson.getLoginPerson().getNum());
        editName.setText(loginPerson.getLoginPerson().getName());
        editBirth.setText(loginPerson.getLoginPerson().getBirth());
        Bitmap bmp;
        String portraitPath = loginPerson.getLoginPerson().getPortrait();
        if(portraitPath.equals(new String ("R.mipmap.yellowchic"))){
            bmp= BitmapFactory.decodeResource(getResources(),R.mipmap.yellowchic);
        }
        else{
            bmp = BitmapFactory.decodeFile(portraitPath);
        }
        portrait.setImageBitmap(bmp);


        btn_portrait.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                跳出弹窗选择, 选择是拍照还是选照片
                final String[] items = new String[]{"拍照", "从相册中选"};
                AlertDialog alertDialog = new AlertDialog.Builder(ChangePersonInfo.this)
                        .setTitle("更换头像")
                        .setIcon(R.mipmap.reminder)
                        .setItems(items, new DialogInterface.OnClickListener() {//添加列表
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (items[i]){
                                    case "拍照":
//                                         调用系统相机
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        imgFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
                                        if(imgFile != null){
                                            Uri fileUri =
                                                    FileProvider.getUriForFile(ChangePersonInfo.this,"com.xdx.yyh.bit.finalwork.MiniDouyin",imgFile);
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
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        btn_personInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = editNum.getText().toString();
                String name = editName.getText().toString();
                String birth = editBirth.getText().toString();
                if(mOperatorHelper.isExist(number)){
                    mOperatorHelper.updatePersonFromDatabase(number,name,birth);
                    List<Person> people = mOperatorHelper.getPersonInfoFromDatabase(number);
                    loginPerson.setLoginPerson(people.get(0));
                    btn_personInfo.playAnimation();

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goHomePage();
                        }
                    },2700);

                }
                else{
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    Person person = mOperatorHelper.insertPersonFromDatabase(
                            loginPerson.getLoginPerson().getPortrait(),number,name,birth,timeStamp,
                            loginPerson.getLoginPerson().getNum());
                    if(person == null){
                        Toast.makeText(getApplicationContext(), "更改错误",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        btn_personInfo.playAnimation();
                        loginPerson.setLoginPerson(person);

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goHomePage();
                            }
                        },2700);
                    }
                }
            }
        });
    }

    public void goHomePage(){
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }


    //    接受点击更换头像后的返回信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("更换头像返回", "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

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
                loginPerson.getLoginPerson().setPortrait(imgPath);
                if(mOperatorHelper.updatePersonPortraitFromDatabase(imgPath,loginPerson.getLoginPerson().getNum())){
                    cropPhoto(imgUri);
                }
                else{
                    Toast.makeText(getApplicationContext(), "更换错误!!!",
                            Toast.LENGTH_LONG).show();
                }
            }
            else if(requestCode == REQUEST_PORTRAIT_CROP_RRTURN){
                if (data != null) {
                    Bitmap bitmap_portrait = data.getExtras().getParcelable("data");
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

        this.startActivityForResult(intent, REQUEST_PORTRAIT_CROP_RRTURN);
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
