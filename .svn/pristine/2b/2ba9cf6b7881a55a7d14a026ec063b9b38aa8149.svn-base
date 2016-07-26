package db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherOpenHelper extends SQLiteOpenHelper {
	/*
	 * Province 表建表语句
	 */
	public static final String CREATE_PROVINCE="create table Province("
			+"id integer primary key autoincrement,"
			+"province_name text)";
	/*
	 * City表建表语句
	 */
	public static final String CREATE_CITY="create table City("
			+"id integer primary key autoincrement,"
			+"province_name text,"
			+"city_name text,"
			+"city_code text)";
	public static final String CREATE_WEATHER="create table Weather("
			+"id integer primary key autoincrement,"
			+"city_name text,"
			+"fengxiang text,"
			+"fengli text,"
			+"high text,"
			+"type text,"
			+"low text,"
			+"date text)";
	public WeatherOpenHelper(Context context,String name,CursorFactory factory,int version){
		super(context,name,factory,version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_WEATHER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
		public boolean tabIsExist(String tabName){  
            boolean result = false;  
            if(tabName == null){  
                    return false;  
            }  
            SQLiteDatabase db = null;  
            Cursor cursor = null;  
            try {  
                    db = this.getReadableDatabase();//此this是继承SQLiteOpenHelper类得到的  
                    String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";  
                    cursor = db.rawQuery(sql, null);  
                    if(cursor.moveToNext()){  
                            int count = cursor.getInt(0);  
                            if(count>0){  
                                    result = true;  
                            }  
                    }  
                      
            } catch (Exception e) {  
                    // TODO: handle exception  
            }                  
            return result;  
    } 

}
