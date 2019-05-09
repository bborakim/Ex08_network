package com.example.a08_network;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadActivity extends AppCompatActivity {
    Button btnLoad;
    EditText edit_entry;
    FileInputStream fis;
    URL url;
    String lineEnd = "\r\n";
    String twoHyphens= "--";
    String boundary="*****";
    String result;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //EditText에 결과값 출력
            edit_entry.setText(result);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        edit_entry=(EditText)findViewById(R.id.edit_entry);
        makeFile();
        btnLoad=(Button)findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try{
                Log.i("test", "getPackagename():" + getPackageName());
                //서버에 업로드할 경로
                String file="/data/data/" + getPackageName() + "/files/test.txt";
                fileUpload(file);
            }catch (Exception e){
                e.printStackTrace();
            }
            }
        });
    }

    void makeFile() {
        // /data/data/패키지이름/files/파일이름 ==> 내장메모리 파일영역
        String path = "/data/data/" + getPackageName() + "/files";
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }

        File file = new File("/data/data/" + getPackageName() + "/flies/test.txt");
        try{
            FileOutputStream fos=new FileOutputStream(file);
            String str= "hello android";
            fos.write(str.getBytes()); //스트링을 바이트배열로 변환
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void fileUpload(final String file) {
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                //업로드를 처리할 웹서버의 url
                httpFileUpload("http://192.168.0.113:8000/upload/android_upload.jsp", file);
            }
        });
        th.start();
    }

    void httpFileUpload(String urlString, String file) {
        try{
            //sdcard의 파일입력스트림 생성
            fis=new FileInputStream(file);
            //URL 객체생성
            url = new URL(urlString);
            //서버 URL에 접근하여 연결을 확립시킴
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            //커넥션을 통해 입출력이 가능하도록 설정
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false); //캐쉬 사용 안함
            //post방식으로 업로드 처리
            conn.setRequestMethod("POST");
            //post방식으로 넘길 자료의 정보 설정
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //파일업로드이므로 다양한 파일포맷을 지원하기 위해 DataoutputStream 객체 생성
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //파일에 저장할 내용 기록
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data;name=\"uploadedfile\";filename=\""
                    +file + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            int bytesAvailable= fis.available();
            int maxBufferSize = 1024;
            //Math.min(값1, 값2) => 작은 값을 리턴
            int bufferSize=Math.min(bytesAvailable, maxBufferSize);
            //버퍼로 사용할 바이트 배열 생성
            byte[] buffer = new byte[bufferSize];
            //파일입력스트림을 통해 내용을 읽어서 버퍼에 저장
            int bytesRead = fis.read(buffer, 0, bufferSize);
            //내용이 있으면
            while (bytesRead > 0) {
                //버퍼사이즈만큼 읽어서 버퍼의 내용을 데이터출력스트림에 기록(반복처리)
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize=Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary  + twoHyphens + lineEnd);
            //파일 입력스트림 닫기
            fis.close();
            //데이터 출력 스트림을 비움
            dos.flush();
            int ch;
            //url커넥션으로 결과값을 받아서 스트링버퍼에 저장
            InputStream is = conn.getInputStream();
            StringBuffer b = new StringBuffer();
            //1q바이트씩 읽어서 내용이 있으면 스트링 버퍼에 추가
            //더이상 내용이 없으면 -1 리턴
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            //바이트 배열을 스트링으로 변환
            result=b.toString().trim();
            //데이터출력스트림을 닫음
            dos.close();
            handler.sendEmptyMessage(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
