package com.freway.ebike.db;

import java.util.ArrayList;
import java.util.List;

import com.freway.ebike.db.EBikeTable.TravelBluetoothEntry;
import com.freway.ebike.db.EBikeTable.TravelEntry;
import com.freway.ebike.db.EBikeTable.TravelLocationEntry;
import com.freway.ebike.db.EBikeTable.TravelSpeedEntry;
import com.freway.ebike.utils.LogUtils;
import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

/** 数据库帮助类 */
public class DBHelper extends SQLiteOpenHelper {
	private final static String TAG = DBHelper.class.getSimpleName();
	private static DBHelper _instance;
	private static final String DB_NAME = "freway.db";
	private static final int DB_VERSION = 1;
	private static Context mContext;
	private static Gson gson = new Gson();

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
		// 建立表
		// 位置表
		sqliteDatabase
				.execSQL("CREATE TABLE IF NOT EXISTS " + TravelLocationEntry.TABLE_NAME + " (" + TravelLocationEntry._ID
						+ " INTEGER PRIMARY KEY AUTOINCREMENT," + TravelLocationEntry.COLUMN_TRAVEL_ID + " INTEGER,"
						+ TravelLocationEntry.COLUMN_ISPAUSE + " INTEGER," + TravelLocationEntry.COLUMN_DESCRIPTION
						+ " TEXT," + TravelLocationEntry.COLUMN_LOCATION + " TEXT," + TravelLocationEntry.COLUMN_SPEED + " REAL"+");");
		// 平均速度表
		sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TravelSpeedEntry.TABLE_NAME + " (" + TravelSpeedEntry._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + TravelSpeedEntry.COLUMN_TRAVEL_ID + " INTEGER,"+ TravelSpeedEntry.COLUMN_SPEED + " REAL"+");");

		// 蓝牙数据表
		sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TravelBluetoothEntry.TABLE_NAME + " ("
				+ TravelBluetoothEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ TravelBluetoothEntry.COLUMN_TRAVEL_ID + " INTEGER" + ");");

		// 行程表
		sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TravelEntry.TABLE_NAME + " (" + TravelEntry._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ TravelEntry.COLUMN_TYPE + " INTEGER,"
				+ TravelEntry.COLUMN_SYNC + " INTEGER,"
				+ TravelEntry.COLUMN_STARTTIME + " INTEGER,"
				+ TravelEntry.COLUMN_ENDTIME + " INTEGER," + TravelEntry.COLUMN_AVGSPEED + " REAL,"
				+ TravelEntry.COLUMN_MAXSPEED + " REAL," + TravelEntry.COLUMN_SPENDTIME + " INTEGER,"
				+ TravelEntry.COLUMN_DISTANCE + " REAL," + TravelEntry.COLUMN_CALORIE + " REAL,"
				+ TravelEntry.COLUMN_CADENCE + " REAL," + TravelEntry.COLUMN_ALTITUDE + " REAL" + ");");

		// + " TEXT DEFAULT 'false'," + DevicesColumns.ONLINE +
		// " TEXT DEFAULT 'true');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TravelLocationEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TravelSpeedEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TravelBluetoothEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TravelEntry.TABLE_NAME);
		onCreate(db);
	}

	// 行程
	public long insertTravel(Travel travel) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TravelEntry.COLUMN_TYPE, travel.getType());
		contentValues.put(TravelEntry.COLUMN_SYNC, travel.getSync());
		contentValues.put(TravelEntry.COLUMN_STARTTIME, travel.getStartTime());
		contentValues.put(TravelEntry.COLUMN_ENDTIME, travel.getEndTime());
		contentValues.put(TravelEntry.COLUMN_AVGSPEED, travel.getAvgSpeed());
		contentValues.put(TravelEntry.COLUMN_MAXSPEED, travel.getMaxSpeed());
		contentValues.put(TravelEntry.COLUMN_SPENDTIME, travel.getSpendTime());
		contentValues.put(TravelEntry.COLUMN_DISTANCE, travel.getDistance());
		contentValues.put(TravelEntry.COLUMN_CALORIE, travel.getCalorie());
		contentValues.put(TravelEntry.COLUMN_CADENCE, travel.getCadence());
		contentValues.put(TravelEntry.COLUMN_ALTITUDE, travel.getAltitude());
		long id = sqliteDatabase.insert(TravelEntry.TABLE_NAME, null, contentValues);
		travel.setId(id);
		LogUtils.i(TAG, "insertTravel" + id);
		return id;
	}

	public void updateTravel(Travel travel) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TravelEntry.COLUMN_TYPE, travel.getType());
		contentValues.put(TravelEntry.COLUMN_SYNC, travel.getSync());
		contentValues.put(TravelEntry.COLUMN_STARTTIME, travel.getStartTime());
		contentValues.put(TravelEntry.COLUMN_ENDTIME, travel.getEndTime());
		contentValues.put(TravelEntry.COLUMN_AVGSPEED, travel.getAvgSpeed());
		contentValues.put(TravelEntry.COLUMN_MAXSPEED, travel.getMaxSpeed());
		contentValues.put(TravelEntry.COLUMN_SPENDTIME, travel.getSpendTime());
		contentValues.put(TravelEntry.COLUMN_DISTANCE, travel.getDistance());
		contentValues.put(TravelEntry.COLUMN_CALORIE, travel.getCalorie());
		contentValues.put(TravelEntry.COLUMN_CADENCE, travel.getCadence());
		contentValues.put(TravelEntry.COLUMN_ALTITUDE, travel.getAltitude());
		int row = sqliteDatabase.update(TravelEntry.TABLE_NAME, contentValues, TravelEntry._ID + "=?",
				new String[] { travel.getId() + "" });
		LogUtils.i(TAG, "updateTravel" + row);
	}

	public List<Travel> listTravel() {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + TravelEntry.TABLE_NAME, null);
		List<Travel> list = new ArrayList<Travel>();
		while (result.moveToNext()) {
			Travel travel = new Travel();
			travel.setId(result.getLong(result.getColumnIndex(TravelEntry._ID)));
			travel.setType(result.getInt(result.getColumnIndex(TravelEntry.COLUMN_TYPE)));
			travel.setSync(result.getInt(result.getColumnIndex(TravelEntry.COLUMN_SYNC)));
			travel.setAltitude(result.getDouble(result.getColumnIndex(TravelEntry.COLUMN_ALTITUDE)));
			travel.setAvgSpeed(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_AVGSPEED)));
			travel.setCadence(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_CADENCE)));
			travel.setCalorie(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_CALORIE)));
			travel.setDistance(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_DISTANCE)));
			travel.setEndTime(result.getLong(result.getColumnIndex(TravelEntry.COLUMN_ENDTIME)));
			travel.setMaxSpeed(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_MAXSPEED)));
			travel.setSpendTime(result.getLong(result.getColumnIndex(TravelEntry.COLUMN_SPENDTIME)));
			travel.setStartTime(result.getLong(result.getColumnIndex(TravelEntry.COLUMN_STARTTIME)));
			list.add(travel);
		}
		LogUtils.i(TAG, "listTravel" + list.size());
		result.close();
		return list;
	}

	public Travel findTravelById(long travelId) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery(
				"SELECT * FROM " + TravelEntry.TABLE_NAME + " WHERE " + TravelEntry._ID + "=?",
				new String[] { travelId + "" });
		Travel travel = new Travel();
		while (result.moveToNext()) {
			travel.setId(result.getInt(result.getColumnIndex(TravelEntry._ID)));
			travel.setType(result.getInt(result.getColumnIndex(TravelEntry.COLUMN_TYPE)));
			travel.setSync(result.getInt(result.getColumnIndex(TravelEntry.COLUMN_SYNC)));
			travel.setAltitude(result.getDouble(result.getColumnIndex(TravelEntry.COLUMN_ALTITUDE)));
			travel.setAvgSpeed(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_AVGSPEED)));
			travel.setCadence(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_CADENCE)));
			travel.setCalorie(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_CALORIE)));
			travel.setDistance(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_DISTANCE)));
			travel.setEndTime(result.getLong(result.getColumnIndex(TravelEntry.COLUMN_ENDTIME)));
			travel.setMaxSpeed(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_MAXSPEED)));
			travel.setSpendTime(result.getLong(result.getColumnIndex(TravelEntry.COLUMN_SPENDTIME)));
			travel.setStartTime(result.getLong(result.getColumnIndex(TravelEntry.COLUMN_STARTTIME)));
			break;
		}
		LogUtils.i(TAG, "findTravelById" + travelId);
		result.close();
		return travel;
	}
	
	public List<Travel> listTravelUnSync() {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery(
				"SELECT * FROM " + TravelEntry.TABLE_NAME + " WHERE " + TravelEntry.COLUMN_SYNC + "=?",
				new String[] { 0 + "" });
		List<Travel> list = new ArrayList<Travel>();
		while (result.moveToNext()) {
			Travel travel = new Travel();
			travel.setId(result.getLong(result.getColumnIndex(TravelEntry._ID)));
			travel.setType(result.getInt(result.getColumnIndex(TravelEntry.COLUMN_TYPE)));
			travel.setSync(result.getInt(result.getColumnIndex(TravelEntry.COLUMN_SYNC)));
			travel.setAltitude(result.getDouble(result.getColumnIndex(TravelEntry.COLUMN_ALTITUDE)));
			travel.setAvgSpeed(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_AVGSPEED)));
			travel.setCadence(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_CADENCE)));
			travel.setCalorie(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_CALORIE)));
			travel.setDistance(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_DISTANCE)));
			travel.setEndTime(result.getLong(result.getColumnIndex(TravelEntry.COLUMN_ENDTIME)));
			travel.setMaxSpeed(result.getFloat(result.getColumnIndex(TravelEntry.COLUMN_MAXSPEED)));
			travel.setSpendTime(result.getLong(result.getColumnIndex(TravelEntry.COLUMN_SPENDTIME)));
			travel.setStartTime(result.getLong(result.getColumnIndex(TravelEntry.COLUMN_STARTTIME)));
			list.add(travel);
		}
		LogUtils.i(TAG, "listTravel" + list.size());
		result.close();
		return list;
	}


	public void deleteTravel(long travelId) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		int travelRow = sqlDb.delete(TravelEntry.TABLE_NAME, TravelEntry._ID + " = '" + travelId + "'", null);
		int travelLocationRow=deleteTravelLocationByTravelId(travelId);
		int travelSpeedRow=deleteTravelSpeedByTravelId(travelId);
		LogUtils.i(TAG, "deleteTravel-travelRow=" + travelRow+"--locationRow="+travelLocationRow+"--speedRow="+travelSpeedRow);
	}

	// 位置
	public long insertTravelLocation(TravelLocation travelLocation) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TravelLocationEntry.COLUMN_TRAVEL_ID, travelLocation.getTravelId());
		contentValues.put(TravelLocationEntry.COLUMN_ISPAUSE, travelLocation.isPause() ? 1 : 0);
		contentValues.put(TravelLocationEntry.COLUMN_DESCRIPTION, travelLocation.getDescription());
		contentValues.put(TravelLocationEntry.COLUMN_LOCATION,
				travelLocation.getLocation() == null ? "" : gson.toJson(travelLocation.getLocation()));
		contentValues.put(TravelLocationEntry.COLUMN_SPEED, travelLocation.getSpeed());
		long id = sqliteDatabase.insert(TravelLocationEntry.TABLE_NAME, null, contentValues);
		travelLocation.setId(id);
		LogUtils.i(TAG, "insertTravelLocation" + id);
		return id;
	}

	public void updateTravelLocation(TravelLocation travelLocationEntry) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TravelLocationEntry.COLUMN_TRAVEL_ID, travelLocationEntry.getTravelId());
		contentValues.put(TravelLocationEntry.COLUMN_ISPAUSE, travelLocationEntry.isPause() ? 1 : 0);
		contentValues.put(TravelLocationEntry.COLUMN_DESCRIPTION, travelLocationEntry.getDescription());
		contentValues.put(TravelLocationEntry.COLUMN_LOCATION,
				travelLocationEntry.getLocation() == null ? "" : gson.toJson(travelLocationEntry.getLocation()));
		contentValues.put(TravelLocationEntry.COLUMN_SPEED, travelLocationEntry.getSpeed());
		int row = sqliteDatabase.update(TravelLocationEntry.TABLE_NAME, contentValues, TravelLocationEntry._ID + "=?",
				new String[] { travelLocationEntry.getId() + "" });
		LogUtils.i(TAG, "updateTravelLocation" + row);
	}

	public List<TravelLocation> listTravelLocation(long travelId) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + TravelLocationEntry.TABLE_NAME + " WHERE "
				+ TravelLocationEntry.COLUMN_TRAVEL_ID + "=?", new String[] { travelId + "" });
		List<TravelLocation> list = new ArrayList<TravelLocation>();
		while (result.moveToNext()) {
			String locationJson = result.getString(result.getColumnIndex(TravelLocationEntry.COLUMN_LOCATION));
			Location l = gson.fromJson(locationJson, Location.class);
			TravelLocation travel = new TravelLocation(l);
			travel.setId(result.getLong(result.getColumnIndex(TravelLocationEntry._ID)));
			travel.setTravelId(result.getInt(result.getColumnIndex(TravelLocationEntry.COLUMN_TRAVEL_ID)));
			travel.setPause(
					result.getInt(result.getColumnIndex(TravelLocationEntry.COLUMN_ISPAUSE)) == 1 ? true : false);
			travel.setDescription(result.getString(result.getColumnIndex(TravelLocationEntry.COLUMN_DESCRIPTION)));
			travel.setSpeed(result.getFloat(result.getColumnIndex(TravelLocationEntry.COLUMN_SPEED)));
			list.add(travel);
		}
		LogUtils.i(TAG, "listTravelLocation" + list.size());
		result.close();
		return list;
	}

	public int deleteTravelLocationByTravelId(long travelId) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		int row = sqlDb.delete(TravelLocationEntry.TABLE_NAME,
				TravelLocationEntry.COLUMN_TRAVEL_ID + " = '" + travelId + "'", null);
		LogUtils.i(TAG, "deleteTravelLocationByTravelId" + row);
		return row;
	}

	public void deleteTravelLocationById(long travelLocationId) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		int row = sqlDb.delete(TravelLocationEntry.TABLE_NAME,
				TravelLocationEntry._ID + " = '" + travelLocationId + "'", null);
		LogUtils.i(TAG, "deleteTravelLocationById" + row);
	}

	// 速度
	public long insertTravelSpeed(TravelSpeed travelSpeed) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TravelSpeedEntry.COLUMN_TRAVEL_ID, travelSpeed.getTravelId());
		contentValues.put(TravelSpeedEntry.COLUMN_SPEED, travelSpeed.getSpeed());
		long id = sqliteDatabase.insert(TravelSpeedEntry.TABLE_NAME, null, contentValues);
		travelSpeed.setId(id);
		LogUtils.i(TAG, "insertTravelSpeed" + id);
		return id;
	}

	public void updateTravelSpeed(TravelSpeed travelSpeed) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TravelSpeedEntry.COLUMN_TRAVEL_ID, travelSpeed.getTravelId());
		contentValues.put(TravelSpeedEntry.COLUMN_SPEED, travelSpeed.getSpeed());
		int row = sqliteDatabase.update(TravelSpeedEntry.TABLE_NAME, contentValues, TravelSpeedEntry._ID + "=?",
				new String[] { travelSpeed.getId() + "" });
		LogUtils.i(TAG, "updateTravelSpeed" + row);
	}

	public List<TravelSpeed> listTravelSpeed(long travelId) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery(
				"SELECT * FROM " + TravelSpeedEntry.TABLE_NAME + " WHERE " + TravelSpeedEntry.COLUMN_TRAVEL_ID + "=?",
				new String[] { travelId + "" });
		List<TravelSpeed> list = new ArrayList<TravelSpeed>();
		while (result.moveToNext()) {
			TravelSpeed travel = new TravelSpeed();
			travel.setId(result.getLong(result.getColumnIndex(TravelSpeedEntry._ID)));
			travel.setTravelId(result.getInt(result.getColumnIndex(TravelSpeedEntry.COLUMN_TRAVEL_ID)));
			travel.setSpeed(result.getInt(result.getColumnIndex(TravelSpeedEntry.COLUMN_SPEED)));
			list.add(travel);
		}
		LogUtils.i(TAG, "listTravelSpeed" + list.size());
		result.close();
		return list;
	}

	public int deleteTravelSpeedByTravelId(long travelId) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		int row = sqlDb.delete(TravelSpeedEntry.TABLE_NAME, TravelSpeedEntry.COLUMN_TRAVEL_ID + " = '" + travelId + "'",
				null);
		LogUtils.i(TAG, "deleteTravelSpeedByTravelId" + row);
		return row;
	}

	public void deleteTravelSpeedById(long travelSpeedEntryId) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		int row = sqlDb.delete(TravelSpeedEntry.TABLE_NAME, TravelSpeedEntry._ID + " = '" + travelSpeedEntryId + "'",
				null);
		LogUtils.i(TAG, "deleteTravelSpeedById" + row);
	}

}
