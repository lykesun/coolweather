package com.coolweather.documentoperation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.coolweather.util.HttpCallbackListener;
import com.coolweather.util.HttpUtil;

public class DocumentUtil {

	public static void saveWeather(final String weatherCode,final Context context) {
		new Thread(new Runnable(){

			@Override
			public void run() {
				String url="http://m.weather.com.cn/mweather/"+weatherCode+".shtml";
	    		HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
	    			
	    			@Override
	    			public void onFinish(String response) {
	    				FileOutputStream out=null;
	    				BufferedWriter writer=null;
	    				try {
	    					out=context.openFileOutput("response_data", Context.MODE_PRIVATE);
	    					writer=new BufferedWriter(new OutputStreamWriter(out));
	    					writer.write(parserWebContent(response));
	    					//writer.write(response);
	    				} catch (Exception e) {
	    					e.printStackTrace();
	    				}finally{
	    					try {
	    						if(writer!=null){
	    							writer.close();
	    						}
	    					} catch (Exception e2) {
	    						e2.printStackTrace();
	    					}
	    				}
	    			}
	    			
	    			@Override
	    			public void onError(Exception e) {
	    				e.printStackTrace();
	    			}
	    		},true);
			}}).start();
		
	}
	private static String parserWebContent(String response){
		String newDate=new SimpleDateFormat("yyyy��M��d�� HH:mm:ss",Locale.CHINA).format(new Date());
		String newTime=newDate.split(" ")[1];
		if(newTime.compareTo("17:59:59")<0&&newTime.compareTo("06:00:00")>0){
			 String frontStr="</div>--><div class=\"days7\"><ul><li><b>����</b><i>";
			    String belowStr="</li><li><b>����</b><i>";
			    //�õ���������ȫ����Ϣ
			    String result=para(response,frontStr,belowStr);
			    //�õ������¶���Ϣ
			    String todayTemp=para(result,"<span>","</span>");
			    String[] temps1=todayTemp.split("/");
			//    
//			    //�õ������������
			    String state1=para(result,"alt=\"","\"/>");
			    String temp1=result.substring(result.indexOf("alt=\"", result.indexOf("alt=\"")+"alt=\"".length()));
			    String state2=para(temp1,"alt=\"","\"/>");
			    
			    //�õ������������
			    String temp2=temp1.substring(temp1.indexOf("alt=\""));
			    String state3=para(temp2,"alt=\"","\"/>");
			    String temp3=temp2.substring(temp2.indexOf("alt=\"", temp2.indexOf("alt=\"")+"alt=\"".length()));
			    String state4=para(temp3,"alt=\"","\"/>");
			    
			    //�õ������¶�
			    String tomorrowTemp=para(temp3,"<span>","</span>");
			    String []temps2=tomorrowTemp.split("/");

			     final String weatherState=("����"+"  "+state1+" ת "+state2+"  "+temps1[0]+"~"+temps1[1]+"and"+
			        "����"+"  "+state3+" ת "+state4+"  "+temps2[0]+"~"+temps2[1]);
			    return weatherState;
		}
		else{
			String frontStr="</div>--><div class=\"days7\"><ul><li><b>ҹ��</b>";
		    String belowStr="</li><li><b>����</b><i>";
		    //�õ���������ȫ����Ϣ
		    String result=para(response,frontStr,belowStr);
		    //�õ������¶���Ϣ
		    String todayTemp=para(result,"<span>","</span>");  
//		    //�õ������������
		    String state1=para(result,"alt=\"","\"/>");
		    
		  //�õ������������
		    String temp1=result.substring(result.indexOf("alt=\"", result.indexOf("alt=\"")+"alt=\"".length()));
		    String state3=para(temp1,"alt=\"","\"/>");
		    String temp3=temp1.substring(temp1.indexOf("alt=\"", temp1.indexOf("alt=\"")+"alt=\"".length()));
		    String state4=para(temp3,"alt=\"","\"/>");
		    		    
		    //�õ������¶�
		    String tomorrowTemp=para(temp3,"<span>","</span>");
		    String []temps2=tomorrowTemp.split("/");

		     final String weatherState=("ҹ��"+"  "+state1+"  "+todayTemp+"and"+
		        "����"+"  "+state3+" ת "+state4+"  "+temps2[0]+"~"+temps2[1]);
		    return weatherState;
		}
		
		
		
	}
	public static String getWeather(Context context){
		String str=load(context);
		if(TextUtils.isEmpty(str)){
			return "";
		}
		return str;	   
	}		    	
    private static String para(String str1,String str2,String str3){
    		String result="";
    		int i=str1.indexOf(str2);
    		int j=str1.indexOf(str3);
    		if(i!=-1&&j!=-1){
    			if(i<j){
    				return str1.substring(i+str2.length(), j);
    			}else{
    				j=str1.indexOf(str3, str1.indexOf(str2));
    				return str1.substring(i+str3.length()+1, j);
    			}
    		}
    		return result;
    	}
    private static String load(Context context){
    	FileInputStream in=null;
    	BufferedReader reader=null;
    	StringBuilder content=new StringBuilder();
    	//Toast.makeText(this, "aaaaaaaaaa", Toast.LENGTH_SHORT).show();
    	try {
			in=context.openFileInput("response_data");
			reader=new BufferedReader(new InputStreamReader(in));
			String line="";
			while((line=reader.readLine())!=null){
				content.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
    	return content.toString();
    }
}
