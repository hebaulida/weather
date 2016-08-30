/**
 * ��ȡ��ǰ�������
 */

package com.leeda.weather;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.R.string;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Location {  
	private static LocationManager locationManager;
	private static String provider;
    public static String ShowLocation(){
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// ��װ����������Ľӿڵ�ַ
					StringBuilder url = new StringBuilder();
//					url.append("http://api.map.baidu.com/geocoder/v2/?ak=nUvlnh8wk6cDupYs5foacyXz&location=");
//					url.append(location.getLatitude()).append(",")
//							.append(location.getLongitude());
//					url.append("&output=json&pois=1");
					url.append("http://api.map.baidu.com/geocoder/v2/?ak=nUvlnh8wk6cDupYs5foacyXz&location=");
					url.append("38.85,115.48");
					url.append("&output=json&pois=1");
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url.toString());
					// ��������Ϣͷ��ָ�����ԣ���֤�������᷵���������
					httpGet.addHeader("Accept-Language", "zh-CN");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						JSONObject jsonObject = new JSONObject(response);
						if(jsonObject.get("status").toString().equals("0")){
							StringBuilder address =new StringBuilder();
							address.append(jsonObject.getJSONObject("result").getString("addressComponent"));
							String aString =address.toString();
							Log.d("Location",aString);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
//    	return aString;
		return null;
    	
    }
}