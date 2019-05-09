package com.example.a08_network;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownHtmlActivity extends AppCompatActivity {
     String html;
     Handler handler= new Handler() {
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             TextView result = (TextView)findViewById(R.id.result);
             result.setText(html);
         }
     };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down_html);

        Button btn = (Button)findViewById(R.id.btnDown);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //백그라운드 스레드로 실행시킴
                Thread th=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        html = downloadHtml("http://192.168.0.113:8080/main.jsp");
                        handler.sendEmptyMessage(0);
                    }
                });
                th.start();
            }
            String downloadHtml(String addr){
                StringBuilder html=new StringBuilder();
                try{
                    //스트링을 url 객체로 생성
                    URL url = new URL(addr);
                    //url에 접속 연결
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //연결 성공
                    if(conn != null){
                        //타임아웃설절
                        conn.setConnectTimeout(10000);
                        //캐쉬 사용안함
                        conn.setUseCaches(false);
                        //conn.getResponseConde() 응답 코드 HTTP_OK 200
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                            while (true){
                                String line= br.readLine();
                                if(line == null) break;
                                html.append(line + "\n");
                            }
                            br.close();
                        }
                        conn.disconnect();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return html.toString();
            }
        });
    }
}
