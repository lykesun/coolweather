package com.coolweather.activity;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.coolweather.app.R;
import com.coolweather.documentoperation.DocumentUtil;
import com.coolweather.service.AutoUpdateService;
import com.coolweather.util.ActivityCollector;
import com.coolweather.util.HttpCallbackListener;
import com.coolweather.util.HttpUtil;
import com.coolweather.util.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends BaseActivity implements OnClickListener{

	private LinearLayout weatherInfoLayout;
	/**
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/**
	 * ������ʾ����ʱ��
	 */
	private TextView publishText;
	/**
	 * ������ʾ����������Ϣ
	 */
	private TextView weatherDespText;
	/**
	 * ������ʾ����1
	 */
	private TextView temp1Text;
	/**
	 * ������ʾ����2
	 */
	private TextView temp2Text;
	/**
	 * ������ʾ��ǰ����
	 */
	private TextView currentDateText;
	
	/**
	 * �л����а�ť
	 */
	private Button switchCity;
	/**
	 * ����������ť
	 */
	private Button refreshWeather;
	/**
	 * �鿴δ������
	 */
	private Button futrueWeather;
	
	/**
	 * �˳�
	 */
	private Button appEnd;
	
	
	//private Button testButton;
	/**
	 * Test��ҳ����
	 */
	private TextView correctWeather;;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		

		//��ʼ�����ؼ�
		correctWeather=(TextView) findViewById(R.id.correct_weather);
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText=(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDespText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);                                  
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_date);
		String countyCode=getIntent().getStringExtra("county_code");
		switchCity=(Button) findViewById(R.id.switch_city);
		refreshWeather=(Button) findViewById(R.id.refresh_weather);

		appEnd=(Button) findViewById(R.id.app_end);
		futrueWeather=(Button) findViewById(R.id.futrue_weather_button);
		appEnd.setOnClickListener(this);
		futrueWeather.setOnClickListener(this);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		

		
		if(!TextUtils.isEmpty(countyCode)){
			//���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			showWeather();
		}
	}
	
	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 */
	private void queryWeatherCode(String countyCode){
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address,"countyCode");
	}
	
	/**
	 * ��ѯ������������Ӧ������
	 */
	private void queryWeatherInfo(String weatherCode){
		String address="http://www.weather.com.cn/adat/cityinfo/"+weatherCode+".html";
		Log.d("WeatherActivity", "weatherCode="+weatherCode);
		queryFromServer(address,"weatherCode");
	}
	
	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 */
	private void queryFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//�ӷ��������ص������н�������������
						Log.d("weatherCode","response="+response);
						String[] array=response.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode=array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					//������������ص�������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("ͬ��ʧ��");
					}});
			}});
	}
	
	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather(){
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp2",""));
		temp2Text.setText(prefs.getString("temp1",""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("����"+prefs.getString("publish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		correctWeather.setText("�������������ʱ���ʾ����û��������ʾ\n�������Ͻ�ˢ�°�ť");
		//����Ƿ���Ҫ������������������Զ�����
		checkIsNeedSaveWeather(prefs.getString("weather_code", ""));
		//DocumentUtil.saveWeather(prefs.getString("weather_code", ""), this);
		String weather=DocumentUtil.getWeather(this);
		String[] weathers=weather.split("and");
		if(weathers.length>1){
			correctWeather.setText(weathers[0]+"\n"+weathers[1]);
		}
		Intent intent=new Intent(this,AutoUpdateService.class);
		startService(intent);
	}



	private void checkIsNeedSaveWeather(String weatherCode) {
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCodeOld=prefs.getString("weather_code_old", "");
		if(!weatherCode.equals(weatherCodeOld)){
			DocumentUtil.saveWeather(weatherCode, this);
			SharedPreferences.Editor editor=prefs.edit();
			editor.putString("weather_code_old", weatherCode);			
			/*String weather=DocumentUtil.getWeather(this);
			String[] weathers=weather.split("and");
			correctWeather.setText(weathers[0]+"\n"+weathers[1]);*/
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("ͬ����");
			SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		case R.id.futrue_weather_button:
			Intent intent1=new Intent(this,FutrueWeather.class);
			SharedPreferences prefs1=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode1=prefs1.getString("weather_code", "");
			intent1.putExtra("weatherCode", weatherCode1);
			startActivity(intent1);
			break;
		case R.id.app_end:
			ActivityCollector.finishAll();
			break;
		default:
			break;
		}
	}
	
    	
}

