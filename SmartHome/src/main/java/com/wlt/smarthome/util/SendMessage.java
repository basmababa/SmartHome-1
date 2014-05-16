package com.wlt.smarthome.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendMessage {

    public static final String STRURL = "http://192.168.1.109/n/wsn/ControlOther.asp?";
    String[] strings;
    private URL realURL = null;
    private HttpURLConnection httpConnection = null;
    private HttpDownloader httpDownloader = null;

    public SendMessage() {
        httpDownloader = new HttpDownloader();
    }

    public boolean sendCmd(String s) {
        try {
            realURL = new URL(STRURL + s);
            httpConnection = (HttpURLConnection) realURL.openConnection();
            OutputStream outputstream = null;
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoOutput(true);
            outputstream = httpConnection.getOutputStream();
            outputstream.flush();
            outputstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpConnection.disconnect();
        }
        return true;
    }

    public void openDataCon() {
        try {
            String str = httpDownloader.download("http://192.168.1.109/n/wsn/webdata.txt");
            strings = str.split(",");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getTemperature() {
        if (strings != null && strings.length >= 37) {
            return this.strings[36];
        } else {
            return null;
        }
    }

    public String getHumidity() {
        if (strings != null && strings.length >= 38) {
            return this.strings[37];
        } else {
            return null;
        }
    }
}
