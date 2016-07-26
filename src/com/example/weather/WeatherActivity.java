package com.example.weather;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import db.WeatherOpenHelper;

public class WeatherActivity extends Activity{
	private TextView titleText;
	private TextView dateText;
	private TextView typeText;
	private TextView fengText;
	private TextView wenduText;
	private String cityname = null;
	private String citycode = null;
	private String citynamezidong = null;
	private final static String fileName = "city.json";
	
	protected static final int SHOW_RESPONSE = 0;
	private Handler handler;
	private WeatherOpenHelper dbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		titleText = (TextView)findViewById(R.id.title);
		dateText = (TextView)findViewById(R.id.date_view);
		typeText = (TextView)findViewById(R.id.type_view);
		fengText = (TextView)findViewById(R.id.feng_view);
		wenduText = (TextView)findViewById(R.id.wendu_view);
//		cityname = getIntent().getStringExtra("city_name");
		citynamezidong = getIntent().getStringExtra("city_name_zidong");
		handler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case SHOW_RESPONSE:
						String city = (String) msg.getData().get("city");
						String date = (String) msg.getData().get("date");
						String fengxiang = (String) msg.getData().get("fengxiang");
						String fengli = (String) msg.getData().get("fengli");
						String high = (String) msg.getData().get("high");
						String type = (String) msg.getData().get("type");
						String low = (String) msg.getData().get("low");
						// 在这里进行UI操作，将结果显示到界面上
						titleText.setText(city);
						dateText.setText(date);
						typeText.setText(type);
						fengText.setText(fengxiang+":"+fengli);
						wenduText.setText(low+"~"+high);
						Log.d("show",date+":"+type+":"+fengxiang+":"+fengli+":"+low+":"+high);
						break;
					default:
						break;
					}
				}
			};
		/*
		 * 先判断是从哪个选项来的，两个方法给citycode赋值
		 */
		if (citynamezidong!=null) {
			Log.d("WeatherActivity 自动定位", citynamezidong);
			/*
			 * 还要判断城市表是否为空
			 */
			if (IsEmpty()) {  
				getJson(fileName);
	        }
			cityname = citynamezidong;
			dbHelper = new WeatherOpenHelper(this, "Weather.db", null, 1);
			SQLiteDatabase db= dbHelper.getWritableDatabase();
				Cursor cursor = db.rawQuery("select * from City where city_name like ?", 
						new String[]{citynamezidong});
				if(cursor.moveToFirst()){
					do{
						citycode = cursor.getString(cursor.getColumnIndex("city_code"));
						Log.d("citycodezidongdingwei", citycode);
					}while(cursor.moveToNext());
				}
				cursor.close();
		}else {
			cityname = getIntent().getStringExtra("city_name");
			citycode = getIntent().getStringExtra("city_code");
			Log.d("citycode", citycode);
		}
		Log.d("最终的citycode", citycode);
		getWeather(citycode);
	}
	/*
	 * 显示天气信息
	 */
	private void showWeather(String cityname) {
		dbHelper = new WeatherOpenHelper(this, "Weather.db", null, 1);
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("dd");     
		String    sysdate = sDateFormat.format(new java.util.Date());  
//		Log.d("time", sysdate);
		Cursor cursor = db.rawQuery("select * from Weather where city_name=? and date like ?", 
				new String[]{cityname,"%" + sysdate + "%"});
		if(cursor.moveToFirst()){
			do{
				final String city = cursor.getString(cursor.getColumnIndex("city_name"));
				final String fengxiang = cursor.getString(cursor.getColumnIndex("fengxiang"));
	        	final String fengli = cursor.getString(cursor.getColumnIndex("fengli"));
	        	final String high = cursor.getString(cursor.getColumnIndex("high"));
	        	final String type = cursor.getString(cursor.getColumnIndex("type"));
	        	final String low = cursor.getString(cursor.getColumnIndex("low"));
	        	final String date = cursor.getString(cursor.getColumnIndex("date")); 
	        	Log.d("forecast from sql",date+":"+fengxiang+":"+fengli+":"+high+":"+type+":"+low+':'+city);  
	      
				Message message = Message.obtain();
				message.what = SHOW_RESPONSE;
				Bundle bundle = new Bundle();  
				bundle.putString("city", city);
				bundle.putString("date", date);
				bundle.putString("type", type);
				bundle.putString("fengxiang", fengxiang);
				bundle.putString("fengli", fengli);
				bundle.putString("high", high);
				bundle.putString("low", low);
				message.setData(bundle);  
				handler.sendMessage(message);
	    	
			}while(cursor.moveToNext());
		}
		cursor.close();
	}
	/*
	 * 获取天气信息
	 */
	private void getWeather(String citycode) {
		RequestQueue mQueue = Volley.newRequestQueue(this); 
		String url = "http://wthrcdn.etouch.cn/weather_mini?citykey="+citycode;
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
//				Log.d("TAG", response.toString());
//				Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
				handleWeatherResponse(response.toString());
				
			}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
				Log.e("TAG", error.getMessage(), error);
				}
			})
		{//覆盖parseNetworkResponse这个方法，直接使用UTF-8对服务器的返回数据进行转码。 
			protected Response<JSONObject>  parseNetworkResponse(NetworkResponse response)
			{
			JSONObject jsonObject;
				try {
				jsonObject = new JSONObject(new String(response.data,"UTF-8"));
				return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
				} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.error(new ParseError(e));
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.error(new ParseError(e));
				}
			}
		};
		mQueue.add(jsonObjectRequest);
	}
	/*
	 * 解析、存储天气数据
	 */
	public void handleWeatherResponse(String response) {
		dbHelper = new WeatherOpenHelper(this, "Weather.db", null, 1);
//		Toast.makeText(getApplicationContext(), "1234", Toast.LENGTH_SHORT).show();
		SQLiteDatabase db= dbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from Weather where city_name like ?", 
					new String[]{cityname});
			
			if(cursor.getCount()!=0){
				db.execSQL("delete from Weather where city_name like ?",new String[]{cityname});
			}
			cursor.close();
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("data");
			String wendu = weatherInfo.getString("wendu");
			String ganmao = weatherInfo.getString("ganmao");
			String forecast = weatherInfo.getString("forecast");
		        JSONArray forecastArray = new JSONArray(forecast);
			        for(int i=0;i<forecastArray.length();i++){
			        	JSONObject forecastObject = forecastArray.getJSONObject(i);
			        	String fengxiang = forecastObject.getString("fengxiang");
			        	String fengli = forecastObject.getString("fengli");
			        	String high = forecastObject.getString("high");
			        	String type = forecastObject.getString("type");
			        	String low = forecastObject.getString("low");
			        	String date = forecastObject.getString("date");
			        	db.execSQL("insert into Weather(city_name,fengxiang,fengli,high,type,low,date)values(?,?,?,?,?,?,?)",new String[]{cityname,fengxiang,fengli,high,type,low,date});
//				    	Log.d("forecast",date+":"+fengxiang+":"+fengli+":"+high+":"+type+":"+low+':'+city);  
			        }
			String yesterday = weatherInfo.getString("yesterday");
//			Log.d("yesterdayinfo",yesterday);  
//			String aqi = weatherInfo.getString("aqi");
//			Log.d("forecast2", wendu+":"+ganmao+":"+":"+city);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		db.close();
		showWeather(cityname);
	}
	
	/*
	 * 判断表是否为空
	 */
	private boolean IsEmpty() {	
		boolean isEmpty=false;
		dbHelper = new WeatherOpenHelper(this, "Weather.db", null, 1);
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.getCount()==0){
			isEmpty=true;
		}
		cursor.close();  
		db.close();
		return isEmpty;
	 }
	
	/*
	 * 插入城市数据
	 */
	private void getJson(String fileName) {
		StringBuilder stringBuilder = new StringBuilder();
		dbHelper = new WeatherOpenHelper(this, "Weather.db", null, 1);
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
		    String line;
		    while ((line = bf.readLine()) != null) {
		    	stringBuilder.append(line);
		    }
		    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
		    JSONArray jsonArray = jsonObject.getJSONArray("城市代码");
			    for (int i = 0; i < jsonArray.length(); i++) {
			    	JSONObject object = jsonArray.getJSONObject(i);
			    	String sheng = object.getString("省");  
		            db.execSQL("insert into Province(province_name)values(?)",new String[]{sheng});
			        String city = object.getString("市");  
		            //将获取的嵌套的JSON串,再解释一次, 得到可使用的对象. 
			        JSONArray cityArray = new JSONArray(city);
				        for(int j=0;j<cityArray.length();j++){
				        	JSONObject cityObject = cityArray.getJSONObject(j);
				        	String cityName = cityObject.getString("市名");
				        	String cityCode = cityObject.getString("编码");  
//				        	String cityNameutf = new String(cityName.getBytes(),"utf-8");
//				        	String cityCodeutf = new String(cityCode.getBytes(),"utf-8");
				        	db.execSQL("insert into City(province_name,city_name,city_code)values(?,?,?)",new String[]{sheng,cityName,cityCode});
//					    	Log.d("json",sheng +":"+cityName+ ":" + cityCode);  
				        }
			     }
		     } catch (UnsupportedEncodingException e) {
	             e.printStackTrace();
	         } catch (IOException e) {
	             e.printStackTrace();
	         } catch (JSONException e) {
	             e.printStackTrace();
	         }
		db.close();
	    }
}
