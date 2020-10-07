package com.example.atelierjava;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    private static final String OUTPUT_FILE_NAME = "MyVideo.mp4";
    private static final String VIDEO_URL = "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    private LocalBroadcastManager localBroadcastManager;
    private Thread sThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Download", "Start Downloading");
        Toast.makeText(DownloadService.this, "Start downloading", Toast.LENGTH_SHORT).show();
        sThread = new DownloadThread();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.sThread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    private class DownloadThread extends Thread {

        public DownloadThread() {
        }

        public void run() {

            try {
                String rootDir = Environment.getExternalStorageDirectory() + "/" + "Video";
                File rootFile = new File(rootDir);
                rootFile.mkdir();
                URL url = new URL(VIDEO_URL);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                int status = httpURLConnection.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "HTTP CONNECTION ERROR");
                }

                File localFile = new File(rootFile, OUTPUT_FILE_NAME);
                String output = "Path of local file : " + localFile.getPath();
                if (rootFile.exists()) Log.d(TAG, output);
                if (!localFile.exists()) {
                    localFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(localFile);
                InputStream in = httpURLConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int nbOfPaquetsReceived = 0;
                int len1 = 0;
                try {
                while ((len1 = in.read(buffer)) > 0) {
                    nbOfPaquetsReceived++;
                    fos.write(buffer, 0, len1);
                }
                } catch (IOException se) {
                    Log.d(TAG, "Exception");
                }
                fos.close();
                in.close();


            } catch (IOException e) {
                Log.d("Error....", e.toString());
            }
        }
    }

}

