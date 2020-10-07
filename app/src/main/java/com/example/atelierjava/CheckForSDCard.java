package com.example.atelierjava;

import android.os.Environment;

public class CheckForSDCard {
    public boolean isPresent() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}