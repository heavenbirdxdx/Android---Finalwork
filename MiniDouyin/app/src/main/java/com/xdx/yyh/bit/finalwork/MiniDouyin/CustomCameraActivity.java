package com.xdx.yyh.bit.finalwork.MiniDouyin;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.LoginPerson;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.PostVideoResponse;
import com.xdx.yyh.bit.finalwork.MiniDouyin.database.OperatorHelper;
import com.xdx.yyh.bit.finalwork.MiniDouyin.newtork.IMiniDouyinService;
import com.xdx.yyh.bit.finalwork.MiniDouyin.utils.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.xdx.yyh.bit.finalwork.MiniDouyin.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.xdx.yyh.bit.finalwork.MiniDouyin.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.xdx.yyh.bit.finalwork.MiniDouyin.utils.Utils.getOutputMediaFile;

public class CustomCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private SurfaceView mSurfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;
    public FloatingActionButton mBtn;
    public FloatingActionButton btn_record;
    private SeekBar seekBar;

    private int rotationDegree = 0;
    private String videoPath = null;
    private static int zoomValue = 0;

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int GRANT_PERMISSION = 3;
    private static final String TAG = "PostVideo";
    private List<Call> mPostVideoResponse = new ArrayList<>();
    public Uri mSelectedImage;
    public Uri mSelectedVideo;
    public MultipartBody.Part cover_image;
    public MultipartBody.Part video;
    private ImageView light;
    private boolean islighting = false;
    private Camera.Parameters params;

    //摄像计时
    private Chronometer chronometer;

//    全局变量用户
    public LoginPerson loginPerson;
    private OperatorHelper mOperatorHelper = new OperatorHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_camera);

        loginPerson = (LoginPerson) getApplication();

        mCamera = getCamera(CAMERA_TYPE);
        mSurfaceView = findViewById(R.id.img);
        //todo 给SurfaceHolder添加Callback
        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
        params = mCamera.getParameters();


        findViewById(R.id.btn_facing).setOnClickListener(v -> {
            //todo 切换前后摄像头
            if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_FRONT)
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            else
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
            startPreview(surfaceHolder);
        });

//        ---------By yxd--------------------
//        将横置摄像头换为竖直，引用库：widget
        seekBar = findViewById(R.id.zoom);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try
                {
                    Camera.Parameters params = mCamera.getParameters();
                    final int MAX = params.getMaxZoom();
                    seekBar.setMax(MAX);
                    int zoomValue = params.getZoom();
                    zoomValue = progress;
                    params.setZoom(zoomValue);
                    mCamera.setParameters(params);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //todo 闪光灯
        light = findViewById(R.id.light);
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(islighting){
                    light.setImageResource(R.mipmap.light);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                    islighting = false;
                }
                else{
                    light.setImageResource(R.mipmap.no_light);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(params);
                    islighting = true;
                }
            }
        });

//        未点击摄像，所以计时不可见,且不占空间
        chronometer = findViewById(R.id.chronometer);
        chronometer.setVisibility(View.GONE);

//        todo 点击屏幕 自动对焦
        findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null){
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if(success){
//                                Toast.makeText(VideoActivity.this,"对焦成功",Toast.LENGTH_SHORT).show();
                            }else{

                            }
                        }
                    });
                }
            }
        });

//        ------------By yxd ----------------



        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            //todo 拍一张照片
            mCamera.takePicture(null, null, mPicture);
        });

        btn_record = findViewById(R.id.btn_record);
        btn_record.setOnClickListener(v -> {
            //todo 录制，第一次点击是start，第二次点击是stop
            if (isRecording) {
                //todo 停止录制

//              ----By yxd ----
//                计时结束
                chronometer.stop();
//              ----By yxd----

                releaseMediaRecorder();
                Toast.makeText(getApplicationContext(), "Stop recording!", Toast.LENGTH_LONG).show();
                isRecording = false;
                btn_record.setImageResource(R.drawable.ic_video);
                chronometer.setVisibility(View.GONE);
                //todo 跳转到PostVideo
                Intent postvideointent = new Intent(this, PostVideo.class);
                postvideointent.putExtra("videopath", videoPath);
                startActivity(postvideointent);

            } else {
                //todo 录制

//              ----BY yxd ----
//                计时开始，计时可见
                chronometer.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
//                -----BY yxd-----

                prepareVideoRecorder();
                isRecording = true;
                Toast.makeText(getApplicationContext(), "Recording!", Toast.LENGTH_LONG).show();
                btn_record.setImageResource(R.drawable.ic_stop_video);
            }
        });


        findViewById(R.id.btn).setOnClickListener(v ->{
            initBtns();
        });

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        startPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseMediaRecorder();
        releaseCameraAndPreview();
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等


        rotationDegree = getCameraDisplayOrientation(CAMERA_TYPE);
        cam.setDisplayOrientation(rotationDegree);
        if(cam.getParameters().isZoomSupported()){
            zoomValue = cam.getParameters().getZoom();
        }
        return cam;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源

            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {
        //todo 开始预览
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {
        //todo 准备MediaRecorder
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

//        videoPath = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        videoPath = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();

        Log.i("遥遥",videoPath);
        mMediaRecorder.setOutputFile(videoPath);
//        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO));

        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);

        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        }catch (Exception e){
            releaseMediaRecorder();
            Log.i("  ", "prepareVideoRecorder: ");
            return false;
        }
        return true;
    }


    private void releaseMediaRecorder() {
        //todo 释放MediaRecorder
        if(mMediaRecorder == null){
            Log.d("mMediaRecorder ","null !");
            return;
        }
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
        try{
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(videoPath))));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }

        mCamera.startPreview();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    private int cnt = 0;
    private void initBtns() {
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(v -> {

            if(cnt == 0)
            {
                Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                chooseImage();
                cnt++;
                mBtn.setImageResource(R.drawable.ic_menu_slideshow);
            }
            else if(cnt == 1)
            {
                Toast.makeText(getApplicationContext(), "Please select a video", Toast.LENGTH_SHORT).show();
                chooseVideo();
                cnt++;
                mBtn.setImageResource(R.drawable.ic_posting);

            }
            else{
                mBtn.setImageResource(R.drawable.ic_menu_gallery);
                postVideo();
                Toast.makeText(getApplicationContext(), "Posting……", Toast.LENGTH_LONG).show();
                cnt = 0;
            }

        });
    }


    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.i(TAG, "selectedImage = " + mSelectedImage);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.i(TAG, "mSelectedVideo = " + mSelectedVideo);
                Toast.makeText(getApplicationContext(), "Post it!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(CustomCameraActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        mBtn.setEnabled(false);

        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show

        cover_image = getMultipartFromUri("image_url", mSelectedImage);
        video = getMultipartFromUri("video_url", mSelectedVideo);
        Call<PostVideoResponse> postVideoResponseCall = getResponseWithRetrofitAsync(this);
        mPostVideoResponse.add(postVideoResponseCall);
        Log.i(TAG, "postVideo: ");

        postVideoResponseCall.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                Toast.makeText(getApplicationContext(), "Post succeeded", Toast.LENGTH_LONG).show();
                mBtn.setEnabled(true);

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                if(mOperatorHelper.updatePersonDateFromDatabase(timeStamp,
                        loginPerson.getLoginPerson().getNum())){
                    loginPerson.getLoginPerson().setDate(timeStamp);
                }
                else{
                    Toast.makeText(getApplicationContext(), "数据库更新错误",
                            Toast.LENGTH_LONG).show();
                }
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goHomePage();
                    }
                },1000);
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Post failed", Toast.LENGTH_LONG).show();
                mBtn.setEnabled(true);
            }
        });

    }
    public  Call<PostVideoResponse> getResponseWithRetrofitAsync(CustomCameraActivity obj) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://douyin.fkynjyq.com/api/video/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IMiniDouyinService iMiniDouyinService = retrofit.create(IMiniDouyinService.class);

        return iMiniDouyinService.postVideo(
                "https://douyin.fkynjyq.com/api/video/",
                loginPerson.getLoginPerson().getNum(),
                loginPerson.getLoginPerson().getName(),
                obj.cover_image,
                obj.video
        );
    }


    public void goHomePage(){
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }
}
