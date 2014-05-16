package com.wlt.smarthome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.*;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.wlt.smarthome.util.FileTools;
import com.wlt.smarthome.util.ImageThread;

import java.io.File;
import java.net.InetAddress;

public class VideoActivity extends Activity {

    private static final int SETTING = 1;
    private static final int REFRESH = 2;
    private static final int SAVE = 3;
    private static final int WINDOW = 4;
    public Object synch = new Object();
    boolean flag = false;
    int pictureWidth = 0, prePictureWidth = 0;
    byte flip = 0, flip01 = 0;
    String IPstr = "192.168.1.101", port = "81";
    InetAddress address = null;
    Bitmap b = null;
    private TextView brightText = null;
    private TextView resolutionText = null;
    private SeekBar brightSeekBar = null;
    private SeekBar resolutionSeekBar = null;
    private SurfaceView videoView = null;
    private SurfaceHolder holder = null;
    private int bright = 0;
    private int resolution = 0;
    private Vibrator vibrator = null;
    private ImageThread mImageThread;
    private DrawImage mDrawImage;
    private Canvas c = null;
    private float startX, startY;
    private float moveX, moveY;
    private StringBuffer stringBuffer;
    private double arg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        setTheme(16974123);
        this.vibrator = (Vibrator) VideoActivity.this.getSystemService(VIBRATOR_SERVICE);
        this.videoView = (SurfaceView) findViewById(R.id.videoView);
        this.holder = videoView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // ������ָ�������¶���
                // ��ȡ���View����꣬���Դ�View���Ͻ�Ϊԭ��
                moveX = event.getX();
                moveY = event.getY();
                startX = event.getX();
                startY = event.getY();
                stringBuffer = new StringBuffer();
                break;

            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                arg = Math.atan(Math.abs((startX - moveX) / (startY - moveY))) / Math.PI * 180;
                if (moveX - startX > 30 && arg >= 60) {
                    add('r');
                    startX = moveX;
                    startY = moveY;
                }
                if (moveX - startX < -30 && arg >= 60) {
                    add('l');
                    startX = moveX;
                    startY = moveY;
                }
                if (moveY - startY > 30 && arg <= 30) {
                    add('d');
                    startX = moveX;
                    startY = moveY;
                }
                if (moveY - startY < -30 && arg <= 30) {
                    add('u');
                    startX = moveX;
                    startY = moveY;
                }
                break;
            case MotionEvent.ACTION_UP:
                moveX = 0;
                moveY = 0;
                if (stringBuffer.toString().length() == 1) {
                    Toast.makeText(VideoActivity.this, stringBuffer.toString(), Toast.LENGTH_SHORT).show();
                    char c = stringBuffer.toString().charAt(0);
                    switch (c) {
                        case 'r':
                            if (mImageThread != null) {
                                mImageThread.sendCmd("decoder_control.cgi?command=6");
                                mImageThread.sendCmd("decoder_control.cgi?command=7");
                            }
                            break;
                        case 'l':
                            if (mImageThread != null) {
                                mImageThread.sendCmd("decoder_control.cgi?command=4");
                                mImageThread.sendCmd("decoder_control.cgi?command=5");
                            }
                            break;
                        case 'd':
                            if (mImageThread != null) {
                                mImageThread.sendCmd("decoder_control.cgi?command=2");
                                mImageThread.sendCmd("decoder_control.cgi?command=3");
                            }
                            break;
                        case 'u':
                            if (mImageThread != null) {
                                mImageThread.sendCmd("decoder_control.cgi?command=0");
                                mImageThread.sendCmd("decoder_control.cgi?command=1");
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (stringBuffer.toString().length() == 0) {
                    vibrator.vibrate(40);
                    if (this.b != null) {
                        File file = new FileTools().savePicture(b);
                        Toast toast = Toast.makeText(VideoActivity.this,
                                "存储文件名为：" + file.getName(), Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(VideoActivity.this,
                                "存储失败", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                break;
        }
        return true;
    }

    private void add(char c) {
        if (stringBuffer.length() == 0 || !stringBuffer.toString().endsWith(String.valueOf(c))) {
            stringBuffer.append(c);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem settingItem = menu.add(0, SETTING, 0, "");
        MenuItem refreshItem = menu.add(0, REFRESH, 1, "");
        MenuItem saveItem = menu.add(0, SAVE, 2, "");
        MenuItem windowItem = menu.add(0, WINDOW, 3, "");
        settingItem.setIcon(R.drawable.setting);
        refreshItem.setIcon(R.drawable.refresh);
        saveItem.setIcon(R.drawable.save);
        windowItem.setIcon(R.drawable.window);
        settingItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        windowItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch (mi.getItemId()) {
            case SETTING:
                setting();
                break;
            case REFRESH:
                refresh();
                break;
            case SAVE:
                save();
                break;
            case WINDOW:
                window();
                break;
        }
        return true;
    }

    private void setting() {
        Toast.makeText(VideoActivity.this, "setting", Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = LayoutInflater.from(VideoActivity.this);
        View view = inflater.inflate(R.layout.video_setting, null);
        Dialog dialog = new AlertDialog.Builder(VideoActivity.this)
                .setTitle("视频设置")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                }).create();

        this.brightText = (TextView) view.findViewById(R.id.brightText);
        this.resolutionText = (TextView) view.findViewById(R.id.resolutionText);

        this.brightSeekBar = (SeekBar) view.findViewById(R.id.brightSeekBar);
        this.resolutionSeekBar = (SeekBar) view.findViewById(R.id.resolutionSeekBar);

        this.brightSeekBar.setProgress(bright);
        this.resolutionSeekBar.setProgress(resolution);

        this.brightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                VideoActivity.this.brightText.setText(String.valueOf(seekBar.getProgress() + 1));
                bright = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                VideoActivity.this.brightText.setText(String.valueOf(seekBar.getProgress() + 1));
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                VideoActivity.this.brightText.setText(String.valueOf(seekBar.getProgress() + 1));
            }
        });

        this.resolutionSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                setResolution(seekBar.getProgress());
                resolution = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                setResolution(seekBar.getProgress());
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                setResolution(seekBar.getProgress());
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (mImageThread != null) {
                    switch (bright) {
//					case 0:
//						mImageThread.sendCmd("camera_control.cgi?param=1&value=16");
//						break;
//					case 1:
//						mImageThread.sendCmd("camera_control.cgi?param=1&value=32");
//						break;
                        case 2:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=48");
                            break;
                        case 3:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=64");
                            break;
                        case 4:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=80");
                            break;
                        case 5:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=96");
                            break;
                        case 6:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=112");
                            break;
                        case 7:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=128");
                            break;
                        case 8:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=144");
                            break;
                        case 9:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=160");
                            break;
//					case 10:
//						mImageThread.sendCmd("camera_control.cgi?param=1&value=176");
//						break;
//					case 11:
//						mImageThread.sendCmd("camera_control.cgi?param=1&value=192");
//						break;
                        case 12:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=208");
                            break;
                        case 13:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=224");
                            break;
                        case 14:
                            mImageThread.sendCmd("camera_control.cgi?param=1&value=240");
                            break;
                    }
                    switch (resolution) {
                        case 0:
                            mImageThread.sendCmd("camera_control.cgi?param=0&value=2");//160*120
                            break;
                        case 1:
                            mImageThread.sendCmd("camera_control.cgi?param=0&value=8");//320*240
                            break;
                        case 2:
                            mImageThread.sendCmd("camera_control.cgi?param=0&value=32");//640*480
                            break;
                    }
                }
            }
        });
    }

    private void setResolution(int i) {
        String string = null;
        switch (i) {
            case 0:
                string = "160*120";
                break;
            case 1:
                string = "320*240";
                break;
            case 2:
                string = "640*480";
                break;
            default:
                break;
        }
        VideoActivity.this.resolutionText.setText(string);
    }

    private void refresh() {
        Toast.makeText(VideoActivity.this, "refresh", Toast.LENGTH_SHORT).show();
        mImageThread = new ImageThread(VideoActivity.this,
                VideoActivity.this.synch, VideoActivity.this.IPstr,
                VideoActivity.this.port);
        mImageThread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mDrawImage = new DrawImage();
        mDrawImage.start();
    }

    private void save() {
        //Toast.makeText(VideoActivity.this, "save", Toast.LENGTH_SHORT).show();
        if (this.b != null) {
            File file = new FileTools().savePicture(b);
            Toast toast = Toast.makeText(VideoActivity.this,
                    "存储文件名为：" + file.getName(), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(VideoActivity.this,
                    "存储失败", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void window() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void clearDraw() {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);
            canvas.drawColor(Color.BLACK);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    ;

    public Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale((float) (640.0 / bitmap.getWidth()),
                (float) (480.0 / bitmap.getHeight()));
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees,
                    (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return b;
    }

    class DrawImage extends Thread {
        private boolean flag = true;

        public DrawImage() {
        }

        public void run() {
            while (flag) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                synchronized (synch) {
                    if (mImageThread.bitmap != null) {
                        b = mImageThread.bitmap;
                        pictureWidth = b.getWidth();
                        if (prePictureWidth != pictureWidth) {
                            clearDraw();
                        }
                        System.out.println(b.getHeight());
                        System.out.println(b.getWidth());
                        b = big(b);
                        if (VideoActivity.this.flag) {
                            b = VideoActivity.this.rotate(b, 90);
                        }
                        c = holder.lockCanvas(new Rect(0, 0, b
                                .getWidth(), b.getHeight()));
                        c.drawBitmap(b, 0, 0, new Paint());
                        holder.unlockCanvasAndPost(c);
                        prePictureWidth = pictureWidth;

                    } else {
                    }
                }
            }
        }

        public void stopit() {
            flag = false;
        }

        public void againit() {
            flag = true;
        }
    }

}
