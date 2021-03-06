package com.iBeiKe.InfoPortal.campus;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.util.Log;

import com.iBeiKe.InfoPortal.common.ComTimes;
import com.iBeiKe.InfoPortal.common.LoginHelper;
import com.iBeiKe.InfoPortal.database.Database;

/**
 * 校园卡消费记录与基本信息的数据库辅助类。
 * 提供对数据库的访问，为消费记录的listView中的cursor提供更新，
 * 获取数据库中的消费记录内容条数，获取存储的意基本信息与消费记录
 * 以及提供对从服务器获取到的数据的保存功能，
 * 对登录信息的格式化存储，以及对时间与钱数的特定格式化。
 * 
 */
public class CampusHelper {
	private Context context;
	private SQLiteCursor cursor;

	public CampusHelper(Context context) {
		this.context = context;
	}
    
    public Map<String,String> getCampInfoData() {
    	Database db = new Database(context);
    	Map<String,String> map = new HashMap<String,String>();
    	db.read();
    	SQLiteCursor cursor = db.getCursor(Campus.campInfoTable, Campus.campInfoColumns, null, null);
    	cursor.moveToNext();
    	int length = Campus.campInfoColumns.length;
    	for(int i=0;i<length;i++) {
    		map.put(Campus.campInfoColumns[i], cursor.getString(i));
    	}
    	db.close();
    	return map;
    }
    
    public SQLiteCursor updateCampDetailCursor() {
    	Database db = new Database(context);
    	db.read();
    	cursor = db.getCursor(Campus.campDetailTable, Campus.campDetailColumns, null, null);
    	Log.d("cursor count", cursor.getCount() + "");
    	db.close();
    	return cursor;
    }
    
    public Map<String,String> getCampDetailData(int position) {
    	cursor.moveToPosition(position);
    	Map<String,String> map = new HashMap<String,String>();
    	String[] str = Campus.campDetailColumns;
    	String value;
    	int length = str.length;
    	for(int i=0;i<length;i++) {
    		switch(i) {
    		case 0:
    			value = parseTime(cursor.getLong(i));
    			break;
    		case 1:
    			value = cursor.getString(i);
    			break;
    		default:
    			value = parseMoney(cursor.getInt(i));
    		}
    		map.put(str[i], value);
    	}
    	return map;
    }
    
    public int getCampDetailCount() {
    	int num = cursor.getCount();
    	return num;
    }
	
	public void saveContentValues(ContentValues cv, String tableName) {
		Database db = new Database(context);
		db.write();
		if(tableName.equals(Campus.campInfoTable)) {
			String where = Campus.campInfoColumns[1] + "=" +
					cv.getAsString(Campus.campInfoColumns[1]);
			db.delete(Campus.campInfoTable, where);
			db.clean(Campus.campDetailTable);
		} else if(tableName.equals(Campus.campDetailTable)) {
			String where = Campus.campDetailColumns[0] + "<=" +
					cv.getAsString(Campus.campDetailColumns[0]);
			db.delete(Campus.campDetailTable, where);
		}
		db.insert(tableName, cv);
		db.close();
	}
	
	public void saveLoginData(String userName, String password, String type) {
		ContentValues loginData = new ContentValues();
		LoginHelper lh = new LoginHelper(context);
		loginData.put("name", Campus.campInfoTable);
		loginData.put("user", userName);
		loginData.put("passwd", password);
		loginData.put("type", type);
		lh.saveLoginData(loginData);
	}
	
	private String parseTime(long timeMillis) {
		ComTimes ct = new ComTimes(context);
		return ct.getTimes(timeMillis, "yyyy-MM-dd kk:mm:ss", null);
	}
	
	private String parseMoney(int money) {
		String result = "";
		result += money / 100 + ".";
		int remainder = money % 100;
		if(remainder<10) {
			result += "0";
		}
		result += remainder;
		return result;
	}
}
