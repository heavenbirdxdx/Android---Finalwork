package com.xdx.yyh.bit.finalwork.MiniDouyin.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.18 17:53
 */
public class PostVideoResponse {

    // TODO-C2 (3) Implement your PostVideoResponse Bean here according to the response json
    @SerializedName("student_id") private String student_id;
    @SerializedName("user_name") private String user_name;
    @SerializedName("image_url") private String cover_image;
    @SerializedName("video_url") private String video;
    @SerializedName("url") private String url;

    public String getCover_image() {
        return cover_image;
    }

    public String getVideo() {
        return video;
    }

    public String getStudent_id() {
        return student_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUrl() {
        return url;
    }
}
