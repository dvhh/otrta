package com.nibdev.otrtav2.activities;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class ActivityUserLayout extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		WebView web = new WebView(this);
		setContentView(web);
		
		
		File skinfolder = new File(getIntent().getStringExtra("SKINPATH"));
		File indexhtml = new File(skinfolder, "index.html");
		if (indexhtml.exists()){
			web.getSettings().setJavaScriptEnabled(true);
			
			web.getSettings().setLoadWithOverviewMode(true);
			web.getSettings().setUseWideViewPort(true);
			web.getSettings().setSupportZoom(false);
			web.setInitialScale(1);
			
			web.loadUrl("file:///" + indexhtml.getAbsolutePath());
			
		}else{
			
			Toast.makeText(this, "Cant find file " + indexhtml.getPath(), Toast.LENGTH_SHORT).show();
			finish();
			
		}
		

		
		
		
		
		
		
		
	}
}
