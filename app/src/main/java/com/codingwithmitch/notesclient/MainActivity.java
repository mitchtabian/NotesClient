package com.codingwithmitch.notesclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //ui components
    private TextView mNotesDisplay;

    //vars
    private Messenger mMessenger = null;
    private IpcHandler mIpcHandler = new IpcHandler(this);
    private Messenger mIncomingMessenger = null;
    private boolean mIsBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotesDisplay = findViewById(R.id.notesDisplay);


    }

    public void bindToAppTwoService(View view) {
        bindService();
    }


    static class IpcHandler extends Handler {

        private final WeakReference<MainActivity> mMainActivity;

        public IpcHandler(MainActivity activity) {
            mMainActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: got incoming message from server.");
            Log.d(TAG, "handleMessage: what: " + msg.what);
            switch (msg.what) {

                case Constants.MSG_RECEIVED_NOTES:{
                    Log.d(TAG, "handleMessage: received incoming notes.");

                    String notes = msg.getData().getString("ipc_notes");
                    Log.d(TAG, "handleMessage: notes: " + notes);

                    mMainActivity.get().mNotesDisplay.setText(notes);

                    break;
                }

                default: {
                    Log.d(TAG, "handleMessage: default case.");
                    super.handleMessage(msg);
                    break;
                }
            }
        }
    }

    public void bindService(){
        Intent serviceBindIntent =  new Intent();
        serviceBindIntent.setComponent(new ComponentName("com.codingwithmitch.notes", "com.codingwithmitch.notes.NotesService"));
        bindService(serviceBindIntent, serviceConnection, 0);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.");
            mMessenger = new Messenger(iBinder);
            mIsBound = true;
            mIncomingMessenger = new Messenger(mIpcHandler);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "ServiceConnection: disconnected from service.");
            mMessenger = null;
            mIsBound = false;
        }

        @Override
        public void onNullBinding(ComponentName name) {
            Log.d(TAG, "ServiceConnection: onNullBinding: called.");
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.d(TAG, "ServiceConnection: onBindingDied: called.");
        }
    };

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: called.");
        super.onStop();
        if (mMessenger != null) {
            unbindService(serviceConnection);
            mMessenger = null;
        }
    }

    public void getNotesFromAppTwo(View view) {
        retrieveDataFromService();
    }

    private void retrieveDataFromService(){
        Log.d(TAG, "retrieveDataFromService: asking service from application two for data.");
        mNotesDisplay.setText("");

        if (mIsBound) {
            if (mMessenger != null) {
                try {
                    Message msg = Message.obtain(null, Constants.MSG_GET_NOTES);
                    msg.replyTo = mIncomingMessenger;
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    Log.e(TAG, "retrieveDataFromService: RemoteException: " + e.getMessage() );
                    // Service crashed
                }
            }
        }
    }

}
