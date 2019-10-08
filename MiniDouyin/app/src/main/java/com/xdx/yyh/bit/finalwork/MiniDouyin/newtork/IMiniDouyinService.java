package com.xdx.yyh.bit.finalwork.MiniDouyin.newtork;

import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.FeedResponse;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.PostVideoResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * @author Xavier.S
 * @date 2019.01.17 20:38
 */
public interface IMiniDouyinService {
    // TODO-C2 (7) Implement your MiniDouyin PostVideo Request here,
    @Multipart
    @POST()
    Call<PostVideoResponse> postVideo(
            @Url String url,
            @Part("student_id") String student_id,
            @Part("user_name") String user_name,
            @Part MultipartBody.Part cover_image,
            @Part MultipartBody.Part video
    );

    // TODO-C2 (8) Implement your MiniDouyin Feed Request here, url: (GET) http://test.androidcamp.bytedance.com/mini_douyin/invoke/video
    @GET()
    Call<FeedResponse> getVideo(@Url String url);


}
