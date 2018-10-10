package com.codingwithmitch.notesclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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

import com.codingwithmitch.notesclient.models.Note;
import com.codingwithmitch.notesclient.persistence.AppDatabase;
import com.codingwithmitch.notesclient.util.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //ui components
    private TextView mNotesDisplay;

    //vars
    private Context mNotesAppContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotesDisplay = findViewById(R.id.notesDisplay);

        setNotesAppContext();
    }

    private void setNotesAppContext(){
        try{
            mNotesAppContext =
            this.createPackageContext("com.codingwithmitch.notes", Context.CONTEXT_INCLUDE_CODE);
        } catch (PackageManager.NameNotFoundException | SecurityException e) {
            Log.e(TAG, "retrieveData: " + e.getMessage());
        }
    }



    static class DatabaseOperationsAsyncTask extends AsyncTask<Void, Void, ArrayList<Note>> {

        private static final String TAG = "DatabaseOperationsAsync";
        private WeakReference<MainActivity> activityReference;

        public DatabaseOperationsAsyncTask(MainActivity context) {
            super();
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Executed on UI Thread
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {

            return retrieveNotesAsync();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            // Executed on UI Thread
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);

            // Executed on UI Thread
            StringBuilder sb = new StringBuilder();
            for(Note note: notes){
                String s = note.getTitle() + "\n";
                sb.append(s);
            }
            activityReference.get().mNotesDisplay.setText(sb.toString());
        }

        private ArrayList<Note> retrieveNotesAsync(){
            Log.d(TAG, "retrieveNotesAsync: retrieving notes. This is from thread: " + Thread.currentThread().getName());
            AppDatabase db = AppDatabase.getDatabase(activityReference.get().mNotesAppContext);
            return new ArrayList<>(db.noteDataDao().getAllNotes());
        }
    }

    public void getNotesFromAppTwo(View view) {
        if(mNotesAppContext != null){
            Log.d(TAG, "getNotesFromAppTwo: called.");
            DatabaseOperationsAsyncTask task = new DatabaseOperationsAsyncTask(this);
            task.execute();
        }

    }
}












