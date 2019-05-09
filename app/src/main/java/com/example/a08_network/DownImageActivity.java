package com.example.a08_network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownImageActivity extends AppCompatActivity {
    Button btnLoad;
    ImageView img1;
    String imgUrl="http://192.168.0.113:8080/images/tomato.jpg";
    Bitmap bm;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //이미지뷰어에 비트맵을 설정(출력)
            img1.setImageBitmap(bm);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down_image);

        btnLoad=(Button)findViewById(R.id.btnLoad);
        img1=(ImageView)findViewById(R.id.img1);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downImg(imgUrl);
            }
        });
    }

    public void downImg(final String file){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try{
                    //url 객체 생성
                    url=new URL(file);
                    //url에 접속하는 객체
                    HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                    //연결확립
                    conn.connect();
                    //입력스트림생성
                    InputStream is= conn.getInputStream();
                    //비트맵으로 변환
                    bm= BitmapFactory.decodeStream(is);
                    handler.sendEmptyMessage(0);
                    //연결종료
                    conn.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
}
