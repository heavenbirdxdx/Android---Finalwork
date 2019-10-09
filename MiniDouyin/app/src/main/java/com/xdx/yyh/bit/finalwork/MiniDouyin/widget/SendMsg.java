package com.xdx.yyh.bit.finalwork.MiniDouyin.widget;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xdx.yyh.bit.finalwork.MiniDouyin.R;
import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.MsgClass;

import java.util.LinkedList;


public class SendMsg extends AppCompatActivity {


    public SendMsg() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);

        ImageView img_fans = findViewById(R.id.img_fans);
        ImageView img_great = findViewById(R.id.img_great);
        ImageView img_me = findViewById(R.id.img_me);
        ImageView img_comm = findViewById(R.id.img_comm);

        img_fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SendMsg.this, Chatroom.class);
                intent.putExtra("title","fans");
                startActivity(intent);
            }
        });
        img_great.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SendMsg.this, Chatroom.class);
                intent.putExtra("title","great");
                startActivity(intent);
            }
        });
        img_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SendMsg.this, Chatroom.class);
                intent.putExtra("title","me");
                startActivity(intent);
            }
        });
        img_comm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SendMsg.this, Chatroom.class);
                intent.putExtra("title","comm");
                startActivity(intent);
            }
        });

        LinkedList mdata = new LinkedList<MsgClass>();
        mdata.add(new MsgClass("陌生人消息","yaya: 转发[直播]：七舅老爷", "1 min 前",R.mipmap.session_robot));
        mdata.add(new MsgClass("系统消息","账号登陆提醒", "2 min 前",R.mipmap.session_system_notice));
        mdata.add(new MsgClass("抖音小助手","# 收下我的双下巴祝福", "3 min 前",R.mipmap.session_robot));
        mdata.add(new MsgClass("nono","转发[视频]", "4 min 前",R.mipmap.h));
        mdata.add(new MsgClass("yoyo","在吗？接下快递", "5 min 前",R.mipmap.tem));
        mdata.add(new MsgClass("拉拉","我是拉拉，我们开始聊天吧", "7 min 前",R.mipmap.g));
        mdata.add(new MsgClass("df","有时间吗", "10 min 前",R.mipmap.a));
        mdata.add(new MsgClass("shannel","[Hi]", "1 天 前",R.mipmap.b));
        mdata.add(new MsgClass("陌生人消息","yaya: 转发[直播]：七舅老爷", "1 min 前",R.mipmap.session_robot));
        mdata.add(new MsgClass("系统消息","账号登陆提醒", "2 min 前",R.mipmap.session_system_notice));
        mdata.add(new MsgClass("抖音小助手","# 收下我的双下巴祝福", "3 min 前",R.mipmap.session_robot));
        mdata.add(new MsgClass("nono","转发[视频]", "4 min 前",R.mipmap.h));
        mdata.add(new MsgClass("yoyo","在吗？接下快递", "5 min 前",R.mipmap.tem));
        mdata.add(new MsgClass("拉拉","我是拉拉，我们开始聊天吧", "7 min 前",R.mipmap.g));
        mdata.add(new MsgClass("df","有时间吗", "10 min 前",R.mipmap.a));
        mdata.add(new MsgClass("shannel","[Hi]", "1 天 前",R.mipmap.b));

        ListView listView = findViewById(R.id.listview);
        ListViewAdapter adapter = new ListViewAdapter(mdata,this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SendMsg.this, Chatroom.class);
                intent.putExtra("title", adapter.getTitle(position));
//                Intent intent = null;
//                try {
//                    intent =  new Intent(ListViewActivity.this, Class.forName("com.ss.android.ugc.chapter1.MainActivity"));
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
                startActivity(intent);
            }
        });
    }

    public static class ListViewAdapter extends BaseAdapter {

        private LinkedList<MsgClass> mData;
        private Context mContext;

        public ListViewAdapter(LinkedList<MsgClass> mData, Context mContext) {
            this.mData = mData;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public String getTitle(int postion){
            return mData.get(postion).getTitle();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_list_item,parent,false);
            ImageView img = (ImageView) convertView.findViewById(R.id.img);
            TextView msg = (TextView) convertView.findViewById(R.id.message);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            img.setImageResource(mData.get(position).getImg());
            msg.setText(mData.get(position).getMessage());
            time.setText(mData.get(position).getTime());
            title.setText(mData.get(position).getTitle());
            return convertView;
        }
    }


}
