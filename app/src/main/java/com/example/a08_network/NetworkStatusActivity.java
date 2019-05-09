package com.example.a08_network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class NetworkStatusActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_status);

        TextView result=(TextView)findViewById(R.id.result);
        String sResult = "";

        //네트워크 연결 관리 객체 생성(안드로이드 시스템 서비스)
        ConnectivityManager mgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //현재 연결 가능한 네트워크 정보
        NetworkInfo activeNetwork = mgr.getActiveNetworkInfo();
        if(activeNetwork !=null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "인터넷에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        if(activeNetwork != null){
            sResult += "Active:\n" + activeNetwork.toString() + "\n";
            result.setText(sResult);
        }
    }
}
