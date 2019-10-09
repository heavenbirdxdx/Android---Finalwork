package com.xdx.yyh.bit.finalwork.MiniDouyin.widget;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xdx.yyh.bit.finalwork.MiniDouyin.R;

public class Chatroom extends AppCompatActivity implements View.OnClickListener {
    private EditText editText;
    private TextView Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Name=findViewById(R.id.tv_with_name);
        Bundle bundle=this.getIntent().getExtras();
        Name.setText(bundle.getString("title"));
        editText=findViewById(R.id.ed_say);
        editText.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(!(editText.getText()==null||editText.getText().length()==0)) {
            TextView content = findViewById(R.id.tv_content_info);
            content.append("我：" + editText.getText() + "\n");
            editText.setText("");

        }
    }
}

