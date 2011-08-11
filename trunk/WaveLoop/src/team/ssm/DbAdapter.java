package team.ssm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {	//DB 어댑터. 데이터베이스에 접근하여 수행하는 작업들을 추상화시켜주는 역할. 
	
	//필드 이름들
	public static final String KEY_FILEPATH = "filepath";
	public static final String KEY_WAVEPATH = "wavepath";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_MEDIA_DB_ID = "media_db_id";
	
	public static final int FIND_BY_FILEPATH = 0;
	public static final int FIND_BY_WAVEPATH = 1;
	public static final int FIND_BY_MEDIA_DB_ID = 2;
	
	
	//필드 이름들2
		public static final String KEY_START_TIME = "start_time";
		public static final String KEY_END_TIME = "end_time";
		public static final String KEY_DATA_ID = "data_id";
		public static final String KEY_MEMO = "memo";
		public static final String KEY_STAR_RATE = "star_rate";
		public static final String KEY_COLOR = "color";
		
		public static final int FIND_BY_START_TIME = 0;
		public static final int FIND_BY_END_TIME = 1;
		public static final int FIND_BY_DATA_ID = 2;
		public static final int FIND_BY_MEMO = 3;
		public static final int FIND_BY_STAR_RATE = 4;
		public static final int FIND_BY_COLOR = 5;
	
	private static final String TAG = "DbAdapter";
	//private DatabaseHelper mDbHelper;
	//private SQLiteDatabase mDb; // 데이터베이스를 저장
	public DatabaseHelper mDbHelper;
	public SQLiteDatabase mDb; // 데이터베이스를 저장
	
	
	//DB 초기화에 필요한 SQL문장 (재생목록 테이블)
	private static final String DATABASE_CREATE =
		"create table data (_id integer primary key autoincrement,"+
		"filepath text not null, wavepath text not null, media_db_id text not null);";
	
	//DB 초기화에 필요한 SQL문장 (문장노트 테이블)
		private static final String DATABASE_CREATE2 =
			"create table sentence (data_id integer primary key,"+
			"start_time text not null, end_time text not null, memo text not null, star_rate integer not null, color integer not null);";
	
	//데이터베이스 정보 (테이블 이름, 데이터베이스 이름 등)
	private static final String DATABASE_NAME = "waveloop.db";
	private static final String DATABASE_TABLE = "data";
	private static final String DATABASE_TABLE2 = "sentence";
	private static final int DATABASE_VERSION = 1;
	
	private final Context mCtx;
	
	public class DatabaseHelper extends SQLiteOpenHelper{	//오픈헬퍼클래스. DB 열기/닫기를 담당.

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE2);
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			Log.w(TAG, "Upgrading db from version" + oldVersion + " to" +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS data");
			db.execSQL("DROP TABLE IF EXISTS sentence");
			onCreate(db);
		}
		
	
		
	}
	
	public DbAdapter(Context ctx){
		this.mCtx = ctx;
	}
	
	public DbAdapter open() throws SQLException{
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;	
	}
	
	public void close(){
		mDbHelper.close();
	}
	
	public long createBook(String filepath, String wavepath, String media_db_id){		//레코드 생성(추가)
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_FILEPATH, filepath);
		initialValues.put(KEY_WAVEPATH, wavepath);
		initialValues.put(KEY_MEDIA_DB_ID, media_db_id);
		
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public long createBook2(long data_id , String start_time, String end_time, String memo, long star_rate, long color){		//레코드 생성(추가)
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DATA_ID, data_id);
		initialValues.put(KEY_START_TIME, start_time);
		initialValues.put(KEY_END_TIME, end_time);
		initialValues.put(KEY_MEMO, memo);
		initialValues.put(KEY_STAR_RATE, star_rate);
		initialValues.put(KEY_COLOR, color);
		
		
		return mDb.insert(DATABASE_TABLE2, null, initialValues);
	}

	public boolean deleteBook(long rowID){		//레코드 삭제(_id)
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowID, null) > 0;
	}
	
	public boolean deleteBook(String media_DB_ID){		//레코드 삭제(media_db_id)
		return mDb.delete(DATABASE_TABLE, KEY_MEDIA_DB_ID + "=" + media_DB_ID, null) > 0;
	}
	
	public boolean deleteBook2(long data_id){		//레코드 삭제(_id)
		return mDb.delete(DATABASE_TABLE2, KEY_DATA_ID + "=" + data_id, null) > 0;
	}
	
	
	public void createTable() {	//테이블 생성.
		mDb.execSQL(DATABASE_CREATE);
	}
	
	public void createTable2() {	//테이블 생성.
		mDb.execSQL(DATABASE_CREATE2);
	}
	
	public void dropTable(){		//모든 레코드 삭제
		mDb.execSQL("DROP TABLE IF EXISTS data");
	}
	
	public void dropTable2(){		//모든 레코드 삭제
		mDb.execSQL("DROP TABLE IF EXISTS sentence");
	}
	
	public Cursor fetchAllBooks(){		//모든 레코드 반환(_id의 역순으로!)
		return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FILEPATH, KEY_WAVEPATH, KEY_MEDIA_DB_ID}, null, null, null, null, "_id desc");
		
	}
	
	public Cursor fetchAllBooks2(){		//모든 레코드 반환(_id의 역순으로!)
		return mDb.query(DATABASE_TABLE2, new String[]{KEY_DATA_ID, KEY_START_TIME, KEY_END_TIME, KEY_MEMO, KEY_STAR_RATE, KEY_COLOR}, null, null, null, null, "_id desc");
		
	}
	
	public Cursor fetchBook(long rowID) throws SQLException{		//특정 레코드 반환(rowID를 이용)
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FILEPATH, KEY_WAVEPATH,  KEY_MEDIA_DB_ID}, KEY_ROWID + "=" + rowID, null, null, null, null, null);
		if(mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor fetchBook2(long data_id) throws SQLException{		//특정 레코드 반환(rowID를 이용)
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE2, new String[]{KEY_DATA_ID, KEY_START_TIME, KEY_END_TIME,  KEY_MEMO, KEY_STAR_RATE, KEY_COLOR}, KEY_DATA_ID + "=" + data_id, null, null, null, null, null);
		if(mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}
	
	/*
	public Cursor fetchBook(String filepath) throws SQLException{		//특정 레코드 반환(filepath를 이용)
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FILEPATH, KEY_WAVEPATH,  KEY_MEDIA_DB_ID}, KEY_FILEPATH + "=" + filepath, null, null, null, null, null);
		if(mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor fetchBook(String wavepath) throws SQLException{		//특정 레코드 반환(wavepath를 이용)
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FILEPATH, KEY_WAVEPATH,  KEY_MEDIA_DB_ID}, KEY_WAVEPATH + "=" + wavepath, null, null, null, null, null);
		if(mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}
	
	public Cursor fetchBook4(String media_db_id) throws SQLException{		//특정 레코드 반환(media_db_id를 이용)
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FILEPATH, KEY_WAVEPATH,  KEY_MEDIA_DB_ID}, KEY_MEDIA_DB_ID + "=" + media_db_id, null, null, null, null, null);
		if(mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}
	*/
	
	public boolean updateBook(long rowID, String filepath, String wavepath, String media_db_id){	//레코드 업데이트(수정)
		ContentValues args = new ContentValues();
		args.put(KEY_FILEPATH, filepath);
		args.put(KEY_WAVEPATH, wavepath);
		args.put(KEY_WAVEPATH, media_db_id);
		
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowID, null) > 0;
	}
	
	public boolean updateBook2(long data_id, String start_time, String end_time, String memo, String star_rate, long color){	//레코드 업데이트(수정)
		ContentValues args = new ContentValues();
		//args.put(KEY_DATA_ID, data_id);
		args.put(KEY_START_TIME, start_time);
		args.put(KEY_END_TIME, end_time);
		args.put(KEY_MEMO, memo);
		args.put(KEY_STAR_RATE, star_rate);
		args.put(KEY_COLOR, color);
		
		return mDb.update(DATABASE_TABLE2, args, KEY_DATA_ID + "=" + data_id, null) > 0;
	}

	
}

