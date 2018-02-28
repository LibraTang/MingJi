package com.example.mingnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public static final String CreateNote = "create table note ("
            +"id integer primary key autoincrement, "
            +"content text, "
            +"date text)";
    public Database(Context context){super(context,"note",null,1);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateNote);//执行不是SELECT或返回数据的单个SQL语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
