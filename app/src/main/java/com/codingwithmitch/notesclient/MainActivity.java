package com.codingwithmitch.notesclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotesDisplay = findViewById(R.id.notesDisplay);


    }


    private void retrieveDataWithIntent() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.setType("vnd.codingwithmitch.text/vnd.codingwithmitch.intent-text");
        startActivityForResult(intent, Constants.MSG_GET_NOTES);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: called.");

        if (resultCode == RESULT_OK) {
            if(requestCode == Constants.MSG_GET_NOTES){

                Log.d(TAG, "onActivityResult: msg get notes.");

                String msg = data.getStringExtra("message_text_from_notes_app");
                Log.d(TAG, "onActivityResult: msg: " + msg);
                mNotesDisplay.setText(msg);
            }
        }
    }


    public void getNotesFromAppTwo(View view) {
        retrieveDataWithIntent();
    }
}
