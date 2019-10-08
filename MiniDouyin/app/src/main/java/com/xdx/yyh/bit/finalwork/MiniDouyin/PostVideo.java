package com.xdx.yyh.bit.finalwork.MiniDouyin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.LoginPerson;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.PostVideoResponse;
import com.xdx.yyh.bit.finalwork.MiniDouyin.newtork.IMiniDouyinService;
import com.xdx.yyh.bit.finalwork.MiniDouyin.utils.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostVideo extends AppCompatActivity {

    private VideoView videoView;
    private static final int GRANT_PERMISSION = 3;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private final int REQUEST_CODE = 11;

    private static final int REQUEST_EXTERNAL_CAMERA = 101;
    private FloatingActionButton button_post;
    private List<Call> mPostVideoResponse = new ArrayList<>();
    public Uri mSelectedImage;
    public Uri mSelectedVideo;
    private String videopath;
    public MultipartBody.Part cover_image;
    public MultipartBody.Part video;
    private Uri videoUri;

    public LoginPerson loginPerson;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        setContentView(R.layout.activity_record_video);

        loginPerson = (LoginPerson)getApplication();

        button_post = findViewById(R.id.btn_post);
        videoView = findViewById(R.id.videoview);
        Intent intent = getIntent();
        videopath = intent.getStringExtra("videopath");
        videoUri = Uri.fromFile(new File(videopath));
        videoView.setVideoURI(videoUri);
        videoView.start();
        button_post.setOnClickListener(v -> {
            //todo Post 刚刚录好的视频  暂时会报错

            postVideo();
        });
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(PostVideo.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }
    private MultipartBody.Part getMultipartFromPath(String name, String path) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }


    private MultipartBody.Part getMultipartFromByte(String name, byte[] bytes) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
        return MultipartBody.Part.createFormData(name,null, requestFile);
    }

    private void postVideo() {
        Toast.makeText(getApplicationContext(), "Posting", Toast.LENGTH_LONG).show();
        button_post.setEnabled(false);

        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show

        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        String path= videopath;
        mmr.setDataSource(path);
        Toast.makeText(getApplicationContext(), "Posting", Toast.LENGTH_LONG).show();
        Bitmap bitmap=mmr.getFrameAtTime();
        Toast.makeText(getApplicationContext(), "Posting", Toast.LENGTH_LONG).show();
        File file = saveImg(bitmap);
        cover_image  = getMultipartFromUri("image_url", Uri.fromFile(file));

        video = getMultipartFromUri("video_url", videoUri);
        Call<PostVideoResponse> postVideoResponseCall = getResponseWithRetrofitAsync(this);
        mPostVideoResponse.add(postVideoResponseCall);
//        Log.i(TAG, "postVideo: ");
        Toast.makeText(getApplicationContext(), "Posting", Toast.LENGTH_LONG).show();
        postVideoResponseCall.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                Toast.makeText(getApplicationContext(), "Post succeeded", Toast.LENGTH_LONG).show();
                button_post.setEnabled(true);
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Post failed", Toast.LENGTH_LONG).show();
                button_post.setEnabled(true);
            }
        });

    }

    public  Call<PostVideoResponse> getResponseWithRetrofitAsync(PostVideo obj) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://douyin.fkynjyq.com/api/video/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(IMiniDouyinService.class).postVideo(
                "https://douyin.fkynjyq.com/api/video/",
                loginPerson.getLoginPerson().getNum(),
                loginPerson.getLoginPerson().getName(),
                obj.cover_image,
                obj.video
        );
    }

    public static File saveImg(Bitmap bitmap) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "testsaveimg" + File.separator;
        try {
            File folder = new File(dir);
            if (!folder.exists()) {
                folder.mkdir();
            }
            File file = new File(dir, "test" + ".jpg");
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , out);
            out.flush();
            out.close();
            return file;
        }catch (Throwable t) {
            Log.i("postvideo", t.getMessage());
        }

       return null;
    }



    public boolean requestReadExternalStoragePermission(String explanation) {
        if (ActivityCompat.checkSelfPermission(PostVideo.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PostVideo.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "You should grant external storage permission to continue " + explanation, Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(PostVideo.this, new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, GRANT_PERMISSION);
            }
            return false;
        } else {
            return true;
        }
    }

}
