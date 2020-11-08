package com.freway.ebike.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;

public class TutorialActivity extends BaseActivity implements OnClickListener {
    private ImageView leftButton;
    private TextView titleTv;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuorial);
        leftButton = (ImageView) findViewById(R.id.top_bar_left);
        titleTv = (TextView) findViewById(R.id.top_bar_title);
        titleTv.setText(R.string.tutorial);
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageView=findViewById(R.id.data_img);
    }

    @Override
    public void dateUpdate(int id, Object obj) {

    }

    @Override
    public void onClick(View v) {

    }
}
