package com.example.memo.camerameasure;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;


import static android.R.attr.bitmap;

/**
 * Created by team13 2.13
 */

public class Height extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor accelerateSensor;
    Sensor magenticSensor;
    float[] geomagnetic;
    float[] gravity;
    float[] RR = new float[9];
    float[] orientation = new float[3];
    Camera mCamera = null;
    CameraView mCameraView = null;
    ImageView cross_h;
    ImageView screenshot;
    DisplayMetrics dm;
    double down_angle, up_angle, object_height_from_ground, angle_with_ground, distance_from_object, length_of_object, human_length;
    Switch touch_ground_switch;
    SeekBar seek_human_length;
    TextView ORI, text_sek;
    String rolls;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.height);
        intialize_variables();
        try {
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }
        if (mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }
        //btn to close the application
        final ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ORI.setText("");
                ORI.setVisibility(View.VISIBLE);
                down_angle = 0;
                up_angle = 0;
                angle_with_ground = 0;
                touch_ground_switch.setVisibility(View.VISIBLE);
                object_height_from_ground = 0;
                length_of_object = 0;
                distance_from_object = 0;

            }
        });
        ORI = (TextView) findViewById(R.id.or);



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magenticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magenticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        cross_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                take_angles();
            }
        });
        screenshot.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                take_screenshot();
            }

        });


        text_sek.setText("height from ground = " + String.valueOf(human_length) + " CM");

        seek_human_length.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                human_length = i;
                text_sek.setText("height from ground = " + String.valueOf(i) + " CM");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        touch_ground_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (touch_ground_switch.isChecked())
                    touch_ground_switch.setText("On Ground");
                else
                    touch_ground_switch.setText("Above Ground");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magenticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerateSensor);
        sensorManager.unregisterListener(this, magenticSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent Event) {

        if (Event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = Event.values.clone();
        else if (Event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = Event.values.clone();
        if (geomagnetic != null && gravity != null) {
            SensorManager.getRotationMatrix(RR, null, gravity, geomagnetic);
            SensorManager.getOrientation(RR, orientation);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * measure object started from ground and taller than human.
     */
    void base_case() {
        down_angle = Math.abs(down_angle);
        up_angle = Math.abs(up_angle);
        distance_from_object = human_length / Math.tan(Math.toRadians(down_angle));
        length_of_object = human_length + Math.tan(Math.toRadians(up_angle)) * distance_from_object;
        if (length_of_object / 100 > 0) {
            ORI.setText("Length of object: " + String.valueOf(String.format("%.2f", (length_of_object / 100)) + " m\n" +
                    "\n" + "Distance from object: " + String.valueOf(String.format("%.2f", (distance_from_object / 100))) + " m\n"));
            ORI.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(Height.this, "Move Forward", Toast.LENGTH_LONG).show();
            down_angle = 0;
            up_angle = 0;
            touch_ground_switch.setVisibility(View.VISIBLE);
        }
    }

    /**
     * measure object started from ground and shorter than human.
     */
    void measure_small_object() {
        down_angle = Math.abs(down_angle);
        up_angle = Math.abs(up_angle);
        angle_with_ground = 90 - down_angle;
        distance_from_object = human_length * Math.tan(Math.toRadians(angle_with_ground));
        double part_of_my_tall = distance_from_object * Math.tan(Math.toRadians(up_angle));
        length_of_object = human_length - part_of_my_tall;
        if (length_of_object / 100 > 0) {
            ORI.setText("Length of object: " + String.valueOf(String.format("%.2f", (length_of_object / 100)) + " m\n" +
                    "\n" + "Distance from object: " + String.valueOf(String.format("%.2f", (distance_from_object / 100))) + " m\n"));
            ORI.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(Height.this, "Move Forward", Toast.LENGTH_LONG).show();
            down_angle = 0;
            up_angle = 0;
            touch_ground_switch.setVisibility(View.VISIBLE);
        }

    }

    /**
     * I-take angles from sensors.
     */
    void take_angles() {

        rolls += String.valueOf((Math.toDegrees(orientation[2])) % 360 + 90) + "\n";
        if (!touch_ground_switch.isChecked() && angle_with_ground == 0)
            angle_with_ground = adjust_angle_rotation(Math.toDegrees(orientation[2]) % 360 + 90);
        else if (down_angle == 0)
            down_angle = adjust_angle_rotation(Math.toDegrees(orientation[2]) % 360 + 90);
        else if (up_angle == 0) {
            up_angle = adjust_angle_rotation(Math.toDegrees(orientation[2]) % 360 + 90);
            touch_ground_switch.setVisibility(View.INVISIBLE);
            if (!touch_ground_switch.isChecked())
                object_calculations_doesnt_touch_ground();
            else
                object_calculations_touch_ground();
        }
    }

    /**
     * I-adjust angle rotation.
     */
    double adjust_angle_rotation(double angle) {
        double temp;
        temp = angle;
        if (temp > 90) {
            temp = 180 - temp;
        }
        return temp;
    }

    /**
     * I-choose automatically which method will be execute (on ground).
     */
    void object_calculations_touch_ground() {

        if (down_angle < 0 && up_angle > 0)//base case
        {
            double temp = up_angle;
            up_angle = down_angle;
            down_angle = temp;
            base_case();
        } else if ((down_angle > 0 && up_angle > 0) && (down_angle < up_angle))//smaller object
        {
            double temp = up_angle;
            up_angle = down_angle;
            down_angle = temp;
            measure_small_object();
        } else if (up_angle < 0 && down_angle > 0)//base case
            base_case();
        else //smaller object
            measure_small_object();
    }


    /**
     * I-choose automatically which method will be execute (above ground).
     */
    void object_calculations_doesnt_touch_ground() {
        if (angle_with_ground > 0 && down_angle > 0 && up_angle < 0)
            object_on_eyes_level_calc();
        else if (angle_with_ground > 0 && down_angle < 0 && up_angle < 0)
            object_upper_eyes_level_calc();
        else if (angle_with_ground > 0 && down_angle > 0 && up_angle > 0)
            object_below_eyes_level_calc();
    }

    /**
     * I-measure object started above ground and on eye's level.
     */
    void object_on_eyes_level_calc() {
        down_angle = Math.abs(down_angle);
        up_angle = Math.abs(up_angle);
        angle_with_ground = 90 - angle_with_ground;
        distance_from_object = human_length * Math.tan(Math.toRadians(angle_with_ground));
        double part_down = distance_from_object * Math.tan(Math.toRadians(down_angle));
        double part_up = distance_from_object * Math.tan(Math.toRadians(up_angle));
        length_of_object = part_down + part_up;
        object_height_from_ground = human_length - part_down;
        ORI.setText("Length of object: " + String.valueOf(String.format("%.2f", (length_of_object / 100)))
                + " m\n" + "\n" + "Distance from object: " + String.valueOf(String.format("%.2f", (distance_from_object / 100))) +
                " m\n" + "\n" + "Height from ground: " + String.valueOf(String.format("%.2f", (object_height_from_ground / 100))) + " m\n");
        ORI.setVisibility(View.VISIBLE);
    }

    /**
     * measure object started above ground and upper than eye's level.
     */
    void object_upper_eyes_level_calc() {
        down_angle = Math.abs(down_angle);
        up_angle = Math.abs(up_angle);

        angle_with_ground = 90 - angle_with_ground;
        distance_from_object = human_length * Math.tan(Math.toRadians(angle_with_ground));
        double part = distance_from_object * Math.tan(Math.toRadians(down_angle));
        double all = distance_from_object * Math.tan(Math.toRadians(up_angle));
        length_of_object = all - part;
        object_height_from_ground = human_length + part;
        ORI.setText("Length of object: " + String.valueOf(String.format("%.2f", (length_of_object / 100))) + " m\n" + "\n" + "distance_from_object: " + String.valueOf(String.format("%.2f", (distance_from_object / 100))) + " m\n");
        ORI.setVisibility(View.VISIBLE);
    }

    /**
     * I-measure object started above ground and shorter than eye's level.
     */
    /**
     * measure object started above ground and shorter than eye's level.
     */
    void object_below_eyes_level_calc() {
        down_angle = Math.abs(down_angle);
        up_angle = Math.abs(up_angle);
        angle_with_ground = 90 - angle_with_ground;
        distance_from_object = human_length * Math.tan(Math.toRadians(angle_with_ground));
        double all = distance_from_object * Math.tan(Math.toRadians(down_angle));
        double part = distance_from_object * Math.tan(Math.toRadians(up_angle));
        length_of_object = all + part;
        ORI.setText("Length of object: " + String.valueOf(String.format("%.2f", (length_of_object / 100))) + " m\n" + "\n" + "Distance from object: " + String.valueOf(String.format("%.2f", (distance_from_object / 100))) + " m\n");
        ORI.setVisibility(View.VISIBLE);
    }

    /**
     * I-darw measured line.
     */
    void intialize_variables() {
        rolls = "";
        down_angle = 0;
        up_angle = 0;
        angle_with_ground = 0;
        human_length = 162;
        seek_human_length = (SeekBar) findViewById(R.id.seekBar);
        seek_human_length.setProgress(160);
        text_sek = (TextView) findViewById(R.id.text_seek);
        touch_ground_switch = (Switch) findViewById(R.id.switch1);
        cross_h = (ImageView) findViewById(R.id.crosshair);
        screenshot = (ImageView)findViewById(R.id.cam);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        cross_h.getLayoutParams().width = (dm.widthPixels * 15 / 100);
        cross_h.getLayoutParams().height = (dm.widthPixels * 15 / 100);

    }

    /*

    public void take_screenshot(){
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();

            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = screenShot(v1);
            v1.setDrawingCacheEnabled(false);
            Toast.makeText(Height.this, "screenshot", Toast.LENGTH_LONG).show();

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    */

    /*
    public void take_screenshot(int i){
        View rootView = getWindow().getDecorView();

        File screenShot = ScreenShot(rootView);
        if(screenShot!=null){
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
        }
    }

    public File ScreenShot(View view){
        view.setDrawingCacheEnabled(true);

        Bitmap screenBitmap = view.getDrawingCache();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();
        String time = df.format(currentTime);

        String filename = "screenshot" + time + ".png";
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", filename);
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }
    */

    //캡쳐버튼클릭
    public void take_screenshot(){
        //전체화면
        View rootView = getWindow().getDecorView();

        File screenShot = ScreenShot(rootView);
        if(screenShot!=null){
            //갤러리에 추가
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
        }
    }

    //화면 캡쳐하기
    public File ScreenShot(View view){
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());

        Toast.makeText(Height.this, timeStamp, Toast.LENGTH_LONG).show();

        int i = (int) new Date().getTime();

        String filename = "screenshot"+ i +".jpg";
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", filename);  //Pictures폴더 screenshot.png 파일
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //비트맵을 PNG파일로 변환
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }



}
