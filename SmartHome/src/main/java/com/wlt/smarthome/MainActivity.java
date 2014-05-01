package com.wlt.smarthome;

import java.util.Timer;
import java.util.TimerTask;

import com.wlt.smarthome.util.SendMessage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {
	private static final int UPDATE=1;
	private static final int REFRESH=2;
	private static final int VIDEO=3;
	
	private TextView temperature=null;
	private TextView humidity=null;
	private TextView lightText=null;
	
	private ImageButton lightButton=null;
	private ImageButton greenButton=null;
	private ImageButton yellowButton=null;
	private ImageButton redButton=null;
	
	private boolean lightFlag=true;
	private SendMessage sendMessage=new SendMessage();
	private Timer timer=null;
	
	private Handler mainHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case UPDATE:
				if(!"".equals(sendMessage.getTemperature())&&sendMessage.getTemperature()!=null){
					MainActivity.this.temperature.setText(100+"℃");
				}
				if(!"".equals(sendMessage.getHumidity())&&sendMessage.getHumidity()!=null){
					MainActivity.this.humidity.setText(sendMessage.getHumidity()+"%");
				}
				break;
			default:
				break;
			}
			Toast.makeText(MainActivity.this, "update", Toast.LENGTH_SHORT).show();
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTheme(16974123);
		
		this.temperature=(TextView)findViewById(R.id.temperature);
		this.humidity=(TextView)findViewById(R.id.humidity);
		this.lightText=(TextView)findViewById(R.id.lightText);
		this.lightButton=(ImageButton)findViewById(R.id.lightButton);
		this.greenButton=(ImageButton)findViewById(R.id.greenButton);
		this.yellowButton=(ImageButton)findViewById(R.id.yellowButton);
		this.redButton=(ImageButton)findViewById(R.id.redButton);
		
		this.lightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(lightFlag==true){
					MainActivity.this.lightButton.setImageResource(R.drawable.light_off);
					MainActivity.this.lightText.setText("电灯关");
					lightFlag=false;
				}else {
					MainActivity.this.lightButton.setImageResource(R.drawable.light_on);
					MainActivity.this.lightText.setText("电灯开");
					lightFlag=true;
				}
				sendMessage.sendCmd("mote=2&led=1&status=1");
			}
		});
		
		this.greenButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "窗帘开", Toast.LENGTH_SHORT).show();
				sendMessage.sendCmd("mote=3&led=3&status=1");
			}
		});
		
		this.yellowButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "窗帘停", Toast.LENGTH_SHORT).show();
				sendMessage.sendCmd("mote=3&led=2&status=1");
			}
		});

		this.redButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "窗帘关", Toast.LENGTH_SHORT).show();
				sendMessage.sendCmd("mote=3&led=1&status=1");
			}
		});
		
		this.greenButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
		               ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.gray));                              
		            }else if(event.getAction() == MotionEvent.ACTION_UP){
		                ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.green));     
		            }  
				return false;
			}
		});
		
		this.yellowButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
		               ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.gray));                              
		            }else if(event.getAction() == MotionEvent.ACTION_UP){
		                ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.yellow));     
		            }  
				return false;
			}
		});

		this.redButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
		               ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.gray));                              
		            }else if(event.getAction() == MotionEvent.ACTION_UP){
		                ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.red));     
		            }  
				return false;
			}
		});
		
		timer=new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendMessage.openDataCon();
				Message msg=new Message();
				msg.what=UPDATE;
				mainHandler.sendMessage(msg);
			}
		}, 1000,2000);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		timer.cancel();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		MenuItem refreshItem=menu.add(0,REFRESH,0,"");
		MenuItem videoItem=menu.add(0,VIDEO,1,"");
		refreshItem.setIcon(R.drawable.refresh);
		videoItem.setIcon(R.drawable.video);
		refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		videoItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem mi){
		switch (mi.getItemId()){
			case REFRESH:
				refresh();
				break;
			case VIDEO:
				video();
				break;
		}
		return true;
	}
	
	private void refresh(){
		Toast.makeText(MainActivity.this, "refresh", Toast.LENGTH_SHORT).show();
	}
	private void video(){
		Intent intent=new Intent(this,VideoActivity.class);
		startActivity(intent);
	}
}
