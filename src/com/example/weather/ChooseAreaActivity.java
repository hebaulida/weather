package com.example.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.City;
import model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import db.WeatherDb;
import db.WeatherOpenHelper;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherDb weatherDb;
	private WeatherOpenHelper dbHelper;
	private final static String fileName = "city.json";
	private List<String> dataList = new ArrayList<String>();
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	/**
	 * 城市列表
	 */
	private List<City> cityList;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		weatherDb = WeatherDb.getInstance(this);
		
		/*
		 * 表为空就插入城市
		 */
		if (IsEmpty()) {  
			getJson(fileName);
        }else {
//			Log.d("tga", "非空");
		} 
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
//					selectedCity = cityList.get(index);
					String cityname = cityList.get(index).getCityName();
					String citycode = cityList.get(index).getCityCode();
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("city_name", cityname);
					intent.putExtra("city_code", citycode);
					startActivity(intent);
//					finish();
				}
			}
		});
		queryProvinces();  // 加载省级数据
	}

	private void queryProvinces() {
		provinceList = weatherDb.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} 
	}

	/**
	 * 从数据库查询选中省内的城市
	 */
	private void queryCities() {
		cityList = weatherDb.loadCities(selectedProvince.getProvinceName());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} 
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
	
	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表列表、还是直接退出。
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}

}
