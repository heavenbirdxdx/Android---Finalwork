package com.xdx.yyh.bit.finalwork.MiniDouyin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xdx.yyh.bit.finalwork.MiniDouyin.R;

public class Chatroom extends AppCompatActivity{
    private EditText editText;
    private TextView Name;
    private TextView content ;
    private boolean input = false;
    private ImageView portraitMsg;
    private ImageView btn_send_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Name=findViewById(R.id.tv_with_name);
        Bundle bundle=this.getIntent().getExtras();
        Name.setText(bundle.getString("title"));

        content = findViewById(R.id.tv_content_info);
        portraitMsg = findViewById(R.id.portraitMsg);
        portraitMsg.setImageResource(bundle.getInt("portraitInt"));
        btn_send_info = findViewById(R.id.btn_send_info);

        editText=findViewById(R.id.ed_say);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!input){
                    editText.setFocusableInTouchMode(true);
                    input = true;
                    btn_send_info.setImageResource(R.drawable.icon_blacksend_untouch);
                }
                else {
                    if (!(editText.getText() == null || editText.getText().length() == 0)) {
                        content.append("我：" + editText.getText() + "\n");
                        Log.i("要欧亚",content.getText().toString());
                        editText.setText("");
                    }
                    editText.setFocusableInTouchMode(false);
                    btn_send_info.setImageResource(R.drawable.selector_bg_send);
                    input = false;
                }
            }
        });
    }
}

