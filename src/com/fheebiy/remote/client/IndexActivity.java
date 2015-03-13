package com.fheebiy.remote.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.fheebiy.aidl.MyAIDLService;

public class IndexActivity extends Activity implements View.OnClickListener{

    private Button btn;

    private Button btn1;

    private Button btn2;

    private Button btn3;

    private MyAIDLService myAIDLService;

    private LocalBroadcastManager localBroadcastManager;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myAIDLService = MyAIDLService.Stub.asInterface(service);
            try {
                int result = myAIDLService.plus(50, 50);
                String upperStr = myAIDLService.toUpperCase("comes from ClientTest");
                Log.d("TAG", "result is " + result);
                Log.d("TAG", "upperStr is " + upperStr);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btn = (Button)findViewById(R.id.bind_service);
        btn1 = (Button)findViewById(R.id.send_broadcast);
        btn2 = (Button)findViewById(R.id.send_broadcast2);
        btn3 = (Button)findViewById(R.id.startBtn);

        btn.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bind_service:
                Intent intent = new Intent("com.fheebiy.service.MyRemoteService");
                bindService(intent, connection, BIND_AUTO_CREATE);
                break;
            case R.id.send_broadcast:   //能收到
                Intent intent1 = new Intent("com.fheebiy.receiver.msg.coming");
                intent1.putExtra("msg","msg comes from RemoteClient");
                sendBroadcast(intent1);
                break;

            case R.id.send_broadcast2:  //收不到(只有本应用程序内的BroadcastReceiver能收到)
                Intent intent2 = new Intent("com.fheebiy.receiver.msg.coming");
                intent2.putExtra("msg","msg comes from RemoteClient");
                localBroadcastManager.sendBroadcast(intent2);
                break;
            case R.id.startBtn:         //调用Fragment中的CombinationViewActivity(跨进程通信之---Activity)
                Intent intent3 = new Intent();
                intent3.setAction("com.fheebiy.activity.CombinationViewActivity");
                intent3.setData(Uri.parse("info://调用其他应用程序的Activity"));
                intent3.putExtra("msg","msg come from remote client");
                startActivity(intent3);
                break;
            default:



        }

    }
}
