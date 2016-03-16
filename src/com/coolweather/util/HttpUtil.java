package com.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
							response.append(line);
					}
					if(listener!=null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					// TODO: handle exception
					if(listener!=null){
						listener.onError(e);
					}
				}finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
			
		}).start();
	}
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener,boolean flag){
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					StringBuilder responseTemp=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null&&(responseTemp.length()<6000)){
						responseTemp.append(line);
						if(responseTemp.length()>4000){
							response.append(line);
						}
					}
					responseTemp.setLength(0);
					if(listener!=null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					// TODO: handle exception
					if(listener!=null){
						listener.onError(e);
					}
				}finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
			
		}).start();
	}
}
