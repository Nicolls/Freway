package com.freway.ebike.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.freway.ebike.db.TravelEntity.TravelColumns;

public class DBHelper extends SQLiteOpenHelper {
	private static DBHelper _instance;
	private static final String DB_NAME = "freway.db";
	private static final int DB_VERSION = 1;
	private static Context mContext;

	public static DBHelper getInstance(Context context) {
		mContext = context;
		if (_instance == null)
			_instance = new DBHelper(context, DB_NAME, null, DB_VERSION);
		return _instance;
	}

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase) {
		//建立表
		sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TravelEntity.TABLE_NAME + " (" 
		+ TravelColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ TravelColumns.MILEAGE + " TEXT," 
		+ TravelColumns.MAXSPEED + " TEXT,"
				+  TravelColumns.MINSPEED + " TEXT,"
		+ TravelColumns.AVERAGESPEDD+ " TEXT,"
				+ TravelColumns.SPENDTIME + " TEXT" 
		+");");

//				+ " TEXT DEFAULT 'false'," + DevicesColumns.ONLINE + " TEXT DEFAULT 'true');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TravelEntity.TABLE_NAME);
		onCreate(db);
	}

	public long insertTravel(TravelEntity travel) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TravelColumns.MILEAGE, travel.getMileage());
		contentValues.put(TravelColumns.MAXSPEED, travel.getMaxSpeed());
		contentValues.put(TravelColumns.MINSPEED, travel.getMinSpeed());
		contentValues.put(TravelColumns.AVERAGESPEDD, travel.getAverageSpeed());
		contentValues.put(TravelColumns.SPENDTIME,travel.getSpendTime());
		return sqliteDatabase.insert(TravelEntity.TABLE_NAME, null, contentValues);
	}

	public void updateTravel(TravelEntity travel) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TravelColumns.MILEAGE, travel.getMileage());
		contentValues.put(TravelColumns.MAXSPEED, travel.getMaxSpeed());
		contentValues.put(TravelColumns.MINSPEED, travel.getMinSpeed());
		contentValues.put(TravelColumns.AVERAGESPEDD, travel.getAverageSpeed());
		contentValues.put(TravelColumns.SPENDTIME,travel.getSpendTime());
		sqliteDatabase.update(TravelEntity.TABLE_NAME, contentValues, TravelColumns._ID + "=?",new String[]{travel.getId()+""});
	}


	public List<TravelEntity> listTravel() {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + TravelEntity.TABLE_NAME , null);
		List<TravelEntity> list = new ArrayList<TravelEntity>();
		while(result.moveToNext()){
			TravelEntity travel = new TravelEntity();
			travel.setId(result.getInt(result.getColumnIndex(TravelColumns._ID)));
			travel.setMaxSpeed(result.getString(result.getColumnIndex(TravelColumns.MAXSPEED)));
			travel.setMinSpeed(result.getString(result.getColumnIndex(TravelColumns.MINSPEED)));
			travel.setAverageSpeed(result.getString(result.getColumnIndex(TravelColumns.AVERAGESPEDD)));
			travel.setSpendTime(result.getString(result.getColumnIndex(TravelColumns.SPENDTIME)));
			list.add(travel);
		}
		result.close();
		return list;
	}
	public void deleteTravel(String travel_id) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		sqlDb.delete(TravelEntity.TABLE_NAME, TravelColumns._ID + " = '" + travel_id + "'", null);
	}

}
