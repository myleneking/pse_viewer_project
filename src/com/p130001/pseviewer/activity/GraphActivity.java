package com.p130001.pseviewer.activity;

import com.p130001.pseviewer.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GraphActivity extends Activity {

	private static final String PARAM_CODE = "code";
	private String mCode;
	
	public static void show(Context context, String code) {
		Intent i = new Intent(context, GraphActivity.class);
		i.putExtra(PARAM_CODE, code);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		mCode = getIntent().getStringExtra(PARAM_CODE);
		
		TextView tvHeader = (TextView) findViewById(R.id.tvHeader);
		tvHeader.setText(mCode);
	}

}
