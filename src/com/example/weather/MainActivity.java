package com.example.weather;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	private Button zidong;
	private Button shoudong;
	private LocationManager locationManager;

	private String provider;
	private String cityname = null; 
	//缓存地址信息
	SharedPreferences sharedPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
		String cityName = sharedPreferences.getString("cityName", "");
		String cityCode = sharedPreferences.getString("cityCode", "");
		if(cityName!=null&cityName.length()>0){
			Log.d(cityName, "---------"+cityCode);
			Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
			intent.putExtra("city_name", cityName);
			intent.putExtra("city_code", cityCode);
			startActivity(intent);
		}
		
		
		
		
		setContentView(R.layout.start);
		zidong = (Button)findViewById(R.id.zidong);
		shoudong = (Button)findViewById(R.id.shoudong);
		zidong.setOnClickListener(this);
		shoudong .setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.zidong:
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// 获取所有可用的位置提供器
			List<String> providerList = locationManager.getProviders(true);
			if (providerList.contains(LocationManager.GPS_PROVIDER)) {
				provider = LocationManager.GPS_PROVIDER;
			} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
				provider = LocationManager.NETWORK_PROVIDER;
			} else {
				// 当没有可用的位置提供器时，弹出Toast提示用户
				Toast.makeText(this, "没有可用的位置提供器",
						Toast.LENGTH_SHORT).show();
				return;
			}
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				// 显示当前设备的位置信息
				showLocation(location);
			}else{
				Log.d("mainactivity","location=null");
//				showLocation(location);
			}
//			locationManager.requestLocationUpdates(provider, 5000, 1,
//					locationListener);
			
			break;
		case R.id.shoudong:
			Intent intent = new Intent(MainActivity.this,ChooseAreaActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			// 更新当前设备的位置信息
//			showLocation(location);
		}
	};
	private void showLocation(final Location location) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 组装反向地理编码的接口地址
					StringBuilder url = new StringBuilder();
					url.append("http://api.map.baidu.com/geocoder/v2/?ak=PuGUhYp9FXHyPPjEnOObIbrw&location=");
					url.append(location.getLatitude()).append(",")
							.append(location.getLongitude());
					url.append("&output=json&pois=1");
//					url.append("http://api.map.baidu.com/geocoder/v2/?ak=nUvlnh8wk6cDupYs5foacyXz&location=");
//					url.append("38.85,115.48");
//					url.append("&output=json&pois=1");
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url.toString());
					// 在请求消息头中指定语言，保证服务器会返回中文数据
					httpGet.addHeader("Accept-Language", "zh-CN");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						JSONObject jsonObject = new JSONObject(response);
						if(jsonObject.get("status").toString().equals("0")){
							String dizhi1 = jsonObject.getJSONObject("result").getString("addressComponent");
							String dizhi ="["+dizhi1+"]";
							JSONArray dizhiArray = new JSONArray(dizhi);
							for(int i=0;i<dizhiArray.length();i++){
								JSONObject object = dizhiArray.getJSONObject(i);
//								Log.d("json",object.getString("city")); 
								String citynamequancheng = object.getString("city");
//								String citynamequancheng = "保定市";
								Log.d("MainActivity", citynamequancheng);
								cityname = subStrOfByte(citynamequancheng,6);
								Log.d("MainActivity 截取 cityname", cityname);
								Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
								intent.putExtra("city_name_zidong", cityname);
								/*
								 * 上边城市名有问题，还有“市”，数据查找出问题
								 */
								startActivity(intent);
								finish();
							}
							
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * 
	 * @param str 要截取的字符串
	 * @param num 要截取的个数（字节）
	 * @return 截取后的字符串
	 */
	public static String subStrOfByte(String str, int num) {
		byte[] b = str==null?new byte[0]:str.getBytes();
		int i,n=0,index=0,bl=b.length;
		index=bl>num?num:bl;
		for(i=0;i<index;i++){
			if(b[i]<0){
				n++;
			}
		}
		String str1=null;
		if(n%2==0){
			str1=new String(b,0,i);
		}else{
			str1=new String(b,0,i-1);
		}
		return str1;
	}
	
}