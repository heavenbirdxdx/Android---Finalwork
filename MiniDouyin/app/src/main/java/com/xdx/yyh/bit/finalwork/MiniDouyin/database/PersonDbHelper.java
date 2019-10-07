package com.xdx.yyh.bit.finalwork.MiniDouyin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PersonDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "PersonDbhelper";
    // TODO 定义数据库名、版本；创建数据库
    private static final String SQL_CREATE_LIST =
            "CREATE TABLE " + PersonDb.PersonInfo.TABLE_NAME + "("
                    + PersonDb.PersonInfo.COLUMN_ID_TITLE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PersonDb.PersonInfo.COLUMN_NUM_TITLE + " TEXT UNIQUE,"
                    + PersonDb.PersonInfo.COLUMN_NAME_TITLE + " TEXT, "
                    + PersonDb.PersonInfo.COLUMN_BIRTHDAY_TITLE + " TEXT,"
                    + PersonDb.PersonInfo.COLUMN_DATE_TITLE + " TEXT,"
                    + PersonDb.PersonInfo.COLUMN_PORTRAIT_TITLE + " TEXT DEFAULT \"R.mipmap.yellowchic\");";
    private static final  String SQL_DELETE_LIST =
            "DROP TABLE IF EXISTS" + PersonDb.PersonInfo.TABLE_NAME;
    private static final String EXTRA = "ADD";



    public PersonDbHelper(Context context) {
        super(context, PersonDb.PersonInfo.DATABASE_NAME, null, PersonDb.PersonInfo.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,SQL_CREATE_LIST);
        db.execSQL(SQL_CREATE_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i =oldVersion; i < newVersion; i++){
            switch (i){
                case 1:
                    try{
                        db.execSQL("ALTER TABLE " + PersonDb.PersonInfo.TABLE_NAME  + " ADD " + EXTRA +"TEXT");
                        Log.d(TAG,"ALTER TABLE" + PersonDb.PersonInfo.TABLE_NAME  + "ADD" + EXTRA +"TEXT");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
