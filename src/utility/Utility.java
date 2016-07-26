package utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class Utility {
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("data");
			String wendu = weatherInfo.getString("wendu");
			String ganmao = weatherInfo.getString("ganmao");
			String forecast = weatherInfo.getString("forecast");
			 //将获取的嵌套的JSON串,再解释一次, 得到可使用的对象. 
		        JSONArray forecastArray = new JSONArray(forecast);
			        for(int i=0;i<forecastArray.length();i++){
			        	JSONObject forecastObject = forecastArray.getJSONObject(i);
			        	String fengxiang = forecastObject.getString("fengxiang");
			        	String fengli = forecastObject.getString("fengli");
			        	String high = forecastObject.getString("high");
			        	String type = forecastObject.getString("type");
			        	String low = forecastObject.getString("low");
			        	String date = forecastObject.getString("date");
//			        	db.execSQL("insert into Weather(city_name,fengxiang,fengli,high,type,low,date)values(?,?,?,?,?,?,?)",new String[]{cityname,fengxiang,fengli,high,type,low,date});
				    	Log.d("forecast",date+":"+fengxiang+":"+fengli+":"+high+":"+type+":"+low);  
			        }
			String yesterday = weatherInfo.getString("yesterday");
//				//将获取的嵌套的JSON串,再解释一次, 得到可使用的对象. 
//		        JSONArray yesterdayArray = new JSONArray(yesterday);
//		        
//			        for(int j=0;j<yesterdayArray.length();j++){
//			        	JSONObject yesterdayObject = yesterdayArray.getJSONObject(j);
//			        	String fengxiang = yesterdayObject.getString("fx");
//			        	String fengli = yesterdayObject.getString("fl");
//			        	String high = yesterdayObject.getString("high");
//			        	String type = yesterdayObject.getString("type");
//			        	String low = yesterdayObject.getString("low");
//			        	String date = yesterdayObject.getString("date");
	//		        	db.execSQL("insert into City(province_name,city_name,city_code)values(?,?,?)",new String[]{sheng,cityName,cityCode});
				    	Log.d("yesterday",yesterday);  
//			        }
			String aqi = weatherInfo.getString("aqi");
			String city = weatherInfo.getString("city");
			Log.d("forecast2", wendu+":"+ganmao+":"+aqi+":"+city);
//			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
//					weatherDesp, publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
