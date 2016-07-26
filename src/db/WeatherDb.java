package db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import model.City;
import model.Province;
import model.Weather;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherDb {
	public static final String DB_NAME = "Weather.db";
	public static final int VERSION = 1;
	private static WeatherDb WeatherDb;
	private SQLiteDatabase db;
	
	private WeatherDb(Context context){
		WeatherOpenHelper dbHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/*
	 * 获取weatherDBd的实例
	 */
	public synchronized static WeatherDb getInstance(Context context){
		if(WeatherDb == null){
			WeatherDb = new WeatherDb(context);
		}
		return WeatherDb;
	}
	
	/*
	 * 从数据库读取省份信息
	 */
	public List<Province>loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setID(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
	
	/*
	 * 从数据库读取城市信息
	 */
	public List<City>loadCities(String province_name){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_name = ?", new String[]{String.valueOf(province_name)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setID(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceName(province_name);
				list.add(city);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
	
	/*
	 * 从数据库读取城市天气
	 */
//	public Weather weather(String city_name){
//		String sysdate = SimpleDateFormat.format(new java.util.Date());  
//		Log.d("time", sysdate);
//		Cursor cursor = db.rawQuery("select * from Weather where city_name=? and date like ?", 
//				new String[]{city_name,"%" + sysdate + "%"});
//		if(cursor.moveToFirst()){
//			do{
//				Weather weather= new Weather();
//				weather.setID(cursor.getInt(cursor.getColumnIndex("id")));
//				weather.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
//				weather.setfengxiang(cursor.getString(cursor.getColumnIndex("fengxiang")));
//				weather.setFengLi(cursor.getString(cursor.getColumnIndex("fengli")));
//				weather.setHigh(cursor.getString(cursor.getColumnIndex("high")));
//				weather.setType(cursor.getString(cursor.getColumnIndex("type")));
//				weather.setLow(cursor.getString(cursor.getColumnIndex("low")));
//				weather.setDate(cursor.getString(cursor.getColumnIndex("date")));
//			}while(cursor.moveToNext());
//		}
//		return weather(null);
//	}
}
