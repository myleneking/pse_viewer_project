package com.p130001.pseviewer.activity;

import com.p130001.pseviewer.R;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class SplashActivity extends Activity {

	private static int SPLASH_TIME_OUT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		if (activeNetworkInfo == null) {
			Toast.makeText(this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
		}
		
		findViewById(R.id.llTitle).setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				AllActivity.show(SplashActivity.this);
			}
		}, SPLASH_TIME_OUT);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
