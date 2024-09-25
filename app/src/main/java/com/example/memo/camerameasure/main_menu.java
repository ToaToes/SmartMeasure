package com.example.memo.camerameasure;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class main_menu extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.options);

        ImageButton btn_width = (ImageButton) findViewById(R.id.btn_width);
        ImageButton btn_height = (ImageButton) findViewById(R.id.btn_height);
        ImageButton btn_about = (ImageButton)findViewById(R.id.btn_abt);
        ImageButton btn_settings = (ImageButton)findViewById(R.id.btn_set);

        ShapeDrawable shapedrawable = new ShapeDrawable();
        shapedrawable.setShape(new RectShape());
        shapedrawable.getPaint().setColor(Color.LTGRAY);
        shapedrawable.getPaint().setStrokeWidth(10f);
        shapedrawable.getPaint().setStyle(Paint.Style.STROKE);

        btn_width.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_menu.this, Width.class));
            }
        });
        btn_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_menu.this, Height.class));
            }
        });
        btn_about.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                startActivity(new Intent(main_menu.this, About.class));
            }
        });
        btn_settings.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                startActivity(new Intent(main_menu.this, Settings.class));
            }
        });

    }
}