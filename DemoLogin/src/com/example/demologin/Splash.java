package com.example.demologin;

import java.util.Timer;
import java.util.TimerTask;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Splash extends Activity {
	Context context;
	SharedPreferences pref;
	public static String MYPREF="shared";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=this;		
		
		setContentView(R.layout.splash_laytout);
		pref=getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
		
		
	new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(!pref.getBoolean("isLogin", false))
				context.startActivity(new Intent(context,MainActivity.class));
				
				else{
					context.startActivity(new Intent(context,Home.class));
				}
				
				finish();
			}
		},2500);
		
	}

}
