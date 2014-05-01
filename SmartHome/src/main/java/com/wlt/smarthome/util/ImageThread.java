package com.wlt.smarthome.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageThread extends Thread{

	private static char map[];
	private URL realURL;
	@SuppressWarnings("unused")
	private String strPort ;
	private String strIP ;
	private InputStream inputStream = null;
	private HttpURLConnection httpConnection = null;
	private boolean startFlag = false;
	public Integer i = 0;
	public Object synch;
	public Bitmap bitmap = null;

	public ImageThread(Context mContext, Object synch,String ip,String port) {
		super();
		this.synch = synch;
		this.strIP = ip;
		this.strPort = port;
	}
	
	public ImageThread() {
		super();
	}
	
	public void againit(){
		this.startFlag = false;
	}

	@Override
	public void run() {
		try {
			realURL = new URL("http://" + this.strIP +":81/videostream.cgi");
			httpConnection = (HttpURLConnection) realURL.openConnection();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			httpConnection.setRequestMethod("GET");
			httpConnection.setRequestProperty("Authorization", "Basic "
					+ base64Encode(("admin:" + "").getBytes()));
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			Integer i = httpConnection.getResponseCode();
			if (i != 200) {
				return;
			}
			inputStream = httpConnection.getInputStream();
		} catch (Exception e) {
		}
		while (!startFlag) {	
			try {
				byte byte1 = 64;
				int k = byte1;
				byte abyte2[] = new byte[k];
				boolean flag3 = ReadLen(abyte2, k, 0);
				flag3 = true;
				int k1 = 0;
				if (flag3) {					
					String s4 = new String(abyte2);					
					int j2 = s4.indexOf("Content-Length:");				
					byte abyte4[] = new byte[2];
					abyte4[0] = 13;
					abyte4[1] = 10;
					String s13 = new String(abyte4);
					int l2 = s4.indexOf(s13, j2 + 15); // this is fifteen					
					flag3 = false;
					if (j2 > -1 && l2 > -1) {
						s4 = s4.substring(j2 + 16, l2); // this is L2
						k = Integer.parseInt(s4);
						if (k > 0 && k < 0xf4240){
							flag3 = true;
							k1 = 64 - l2 - 4;
						}
					}
				}
				if (flag3) {
					byte abyte3[] = new byte[k];
					for (int k2 = 0; k2 < k1; k2++){
						abyte3[k2] = abyte2[(byte1 - k1) + k2];
					}
					flag3 = ReadLen(abyte3, k, k1);
					if (flag3) {
						synchronized (synch) {
							try {
								bitmap = BitmapFactory.decodeByteArray(abyte3,0, abyte3.length);	
							} catch (Exception e) {
							}
						}
					}
					flag3 = ReadLen(abyte3, 2, 0);
					Thread.sleep(30l);
				}
			} catch (Exception ex) {	
				ex.printStackTrace();
			}
		}
		try {
			inputStream.close();
			httpConnection.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public void sendCmd(String s) {
		int count = 3;
		while ((count--) != 0) {
			String strRealUrl = "http://" + this.strIP + ":81/" + s;
			System.out.println(strRealUrl);
			HttpURLConnection httpconnection = null;
			InputStream inputstream = null;
			try {
				URL strUrl = new URL(strRealUrl);
				httpconnection = (HttpURLConnection) strUrl.openConnection();
				httpconnection.setRequestMethod("GET");
				httpconnection.setRequestProperty("Authorization", "Basic "
						+ base64Encode(("admin" + ":" + "").getBytes()));
				if (httpconnection.getResponseCode() == 200) {
					Log.i("ddddddddd","连接已经成功！！！！！");
				}
				inputstream = httpconnection.getInputStream();
				inputstream.close();
				httpconnection.disconnect();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void stopit() {
		this.startFlag = true;
	}

	private boolean ReadLen(byte abyte0[], int i, int j) {
		int k = j;
		int l;
		try {
			for (; k != i; k += l) {
				System.out.println(inputStream);
				l = inputStream.read(abyte0, k, i - k);
				if (l == -1)
					return false;
			}
		} catch (Exception e) {
			System.out.println(e);
			return true;
		}
		return true;
	}

	public static String base64Encode(byte abyte0[]) {
		int i = abyte0.length;
		int j = (i * 4 + 2) / 3;
		int k = ((i + 2) / 3) * 4;
		char ac[] = new char[k];
		int l = 0;
		for (int i1 = 0; l < i; i1++) {
			int j1 = abyte0[l++] & 0xff;
			int k1 = l >= i ? 0 : abyte0[l++] & 0xff;
			int l1 = l >= i ? 0 : abyte0[l++] & 0xff;
			int i2 = j1 >>> 2;
			int j2 = (j1 & 3) << 4 | k1 >>> 4;
			int k2 = (k1 & 0xf) << 2 | l1 >>> 6;
			int l2 = l1 & 0x3f;
			ac[i1++] = map[i2];
			ac[i1++] = map[j2];
			ac[i1] = i1 >= j ? '=' : map[k2];
			i1++;
			ac[i1] = i1 >= j ? '=' : map[l2];
		}
		return new String(ac);
	}

	static {
		map = new char[64];
		int i = 0;
		for (char c = 'A'; c <= 'Z'; c++){
			map[i++] = c;
		}
		for (char c1 = 'a'; c1 <= 'z'; c1++){
			map[i++] = c1;
		}
		for (char c2 = '0'; c2 <= '9'; c2++){
			map[i++] = c2;
		}
		map[i++] = '+';
		map[i++] = '/';
	}
}

