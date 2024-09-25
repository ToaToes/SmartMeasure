package com.example.memo.camerameasure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by team13 3.14
 */

public class Settings extends Activity {


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        RelativeLayout rl_help = (RelativeLayout)findViewById(R.id.help);

        rl_help.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Help.class));
            }
        });
    }

    //ImageButton btn_width = (ImageButton) findViewById(R.id.btn_width);
}
