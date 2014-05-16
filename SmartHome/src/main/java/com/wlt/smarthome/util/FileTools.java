package com.wlt.smarthome.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.*;
import java.util.Date;

public class FileTools {

    public File savePicture(Bitmap bitmap) {

        BufferedOutputStream bos = null;
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + new Date().getTime() + ".jpg");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return file;
    }
}
