package com.xdx.yyh.bit.finalwork.MiniDouyin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.Feed;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.FeedResponse;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.PostVideoResponse;
import com.xdx.yyh.bit.finalwork.MiniDouyin.newtork.IMiniDouyinService;
import com.xdx.yyh.bit.finalwork.MiniDouyin.utils.NetworkUtils;
import com.xdx.yyh.bit.finalwork.MiniDouyin.utils.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoWatch extends AppCompatActivity {

    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();
    private List<Call> mFeedsListCall = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private View view;
    private Button mBtnRefresh;
    private boolean isplaying = true;
    private MediaController mc;


    public VideoWatch() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_solution2_c2);
        fetchFeed();
        initRecyclerView();
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiper_refresh_layout);
//
//        assert refreshLayout != null;
//        refreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchFeed();
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }


    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View inflaterview = LayoutInflater.from(VideoWatch.this).inflate(R.layout.item_feeds_feed, null);

                return new MyViewHolder(inflaterview);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = ((MyViewHolder) viewHolder).imageView;
                TextView tv = ((MyViewHolder) viewHolder).textView;
//                VideoView vv = ((MyViewHolder) viewHolder).videoView;
                StandardGSYVideoPlayer sGSYplayer = ((MyViewHolder) viewHolder).standardGSYVideoPlayer;
                // TODO-C2 (10) Uncomment these 2 lines, assign image url of Feed to this url variable
                String url_image = mFeeds.get(i).getImage_url();
                Glide.with(iv.getContext()).load(url_image).into(iv);
                String url_video = mFeeds.get(i).getVideo_url();
                init(sGSYplayer, url_video, url_image);
                String user_name = mFeeds.get(i).getUser_name();
                tv.setText(  user_name);
            }

            @Override public int getItemCount() {
                return mFeeds.size();
            }
        });

    }

    private void init(StandardGSYVideoPlayer videoPlayer, String source1, String source2) {


        videoPlayer.setUp(source1, true, "back to home");



        //增加封面

        ImageView imageView = new ImageView(this);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageView.setImageURI(Uri.parse(source2));

        videoPlayer.setThumbImageView(imageView);

        //增加title

        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);

        //设置返回键

        videoPlayer.getBackButton().setVisibility(View.VISIBLE);

        //设置旋转

//        orientationUtils = new OrientationUtils(this, videoPlayer);

        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏

//        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
//
//            @Override
//
//            public void onClick(View v) {
//
//                orientationUtils.resolveByClick();
//
//            }
//
//        });

        //是否可以滑动调整

        videoPlayer.setIsTouchWiget(true);

        //设置返回按键功能

        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                onBackPressed();

            }

        });

        videoPlayer.startPlayLogic();

    }

    public void fetchFeed() {

        // TODO-C2 (9) Send Request to fetch feed
        // if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        // don't forget to call resetRefreshBtn() after response received

        Call<FeedResponse> feedcall = NetworkUtils.getResponseWithRetrofitAsync2();
        mFeedsListCall.add(feedcall);

        feedcall.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                mFeeds = response.body().getFeeds();
                mRv.getAdapter().notifyDataSetChanged();
                mFeedsListCall.remove(call);
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                mFeedsListCall.remove(call);
            }
        });

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
//        public VideoView videoView;
        public View view;
        public StandardGSYVideoPlayer standardGSYVideoPlayer;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_avatar);
            textView = itemView.findViewById(R.id.textView_name);
//            videoView = itemView.findViewById(R.id.ijkPlayer);
            standardGSYVideoPlayer = itemView.findViewById(R.id.ijkPlayer);
        }
    }

}
