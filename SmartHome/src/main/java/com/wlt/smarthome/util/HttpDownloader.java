package com.wlt.smarthome.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloader {

    private URL url;

    public String download(String urlStr) {
        StringBuffer stringBuffer = new StringBuffer();
        String string = null;
        BufferedReader bufferedReader = null;
        try {

            url = new URL(urlStr);

            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(urlConn
                    .getInputStream()));

            while ((string = bufferedReader.readLine()) != null) {
                stringBuffer.append(string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }
}
