package com.coolweather.activity;


import com.coolweather.app.R;
import com.coolweather.util.ActivityCollector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class FutrueWeather extends BaseActivity implements OnClickListener{

	private WebView webView;
	private Button back;
	private Button endApp;
	private ProgressDialog progressDialog;
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			setProgress(100*msg.what);
			Log.i("progress", Integer.toString(msg.what));
			super.handleMessage(msg);
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.futrue_weather_layout);
		Intent intent=getIntent();
		String weatherCode=intent.getStringExtra("weatherCode");
		String url="http://m.weather.com.cn/mweather/"+weatherCode+".shtml";
		webView=(WebView) findViewById(R.id.web_view);
		back=(Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		endApp=(Button) findViewById(R.id.end_from_web);
		endApp.setOnClickListener(this);
		WebSettings settings=webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
		});
		webView.loadUrl(url);
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				Message msg=new Message();
				if(newProgress==100){
					msg.what=0;
					handler.sendMessage(msg);
				}else{
					msg.what=newProgress;
					handler.sendMessage(msg);
				}
			}
			
			private void closeDialog(){
				if(progressDialog!=null&&progressDialog.isShowing()){
					progressDialog.dismiss();
					progressDialog=null;
				}
			}
			
			private void openDialog(int newProgress){
				if(progressDialog==null){
					progressDialog=new ProgressDialog(FutrueWeather.this);
					progressDialog.setTitle("正在加载");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progressDialog.setProgress(newProgress);
					progressDialog.show();
				}
				else{
					progressDialog.setProgress(newProgress);
				}
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
//			Toast.makeText(this, webView.getUrl(), Toast.LENGTH_SHORT).show();
			if(webView.canGoBack()){
				//返回上一页面
				webView.goBack();
				return true;
			}
			else{
				//退出程序
				System.exit(0);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.back:
			FutrueWeather.this.onBackPressed();
			break;
		case R.id.end_from_web:
			ActivityCollector.finishAll();
			break;
		default:
			break;
		}
	}
}
