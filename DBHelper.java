package com.pnu.cse.termspring2018;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS User("
                + "id char(20) not null primary key,"
                + "password varchar(16), "
                + "name varchar(16))";

        String s = "CREATE TABLE IF NOT EXISTS Score("
                + "indx integer primary key autoincrement,"
                + "score integer(20) ,"
                + "name varchar(16))";

        db.execSQL(sql);
        db.execSQL(s);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String id, String pw, String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO User VALUES('" + id + "', '" + pw + "', '" + name + "');");
        db.close();
    }

    // 스코어 입력
    public void insertScore(String name, int sss) {
        SQLiteDatabase db = getWritableDatabase();
        String a = String.valueOf(sss) + ",";
        db.execSQL("INSERT INTO Score" + "(score, name)" + " VALUES(" + a + " '" + name + "');");
        db.close();
    }

    // 스코어 가져오기
    public ArrayList<ScoreModel> getScoreResult() {
        ArrayList<ScoreModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();


        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Score ORDER BY score DESC" , null);

        if(cursor.getCount() != 0) {
            while(cursor.moveToNext()) {
                ScoreModel score = new ScoreModel();

                score.score = cursor.getInt(1);
                score.name = cursor.getString(2);
                list.add(score);
            }
        }

        return list;
    }

    public boolean isExist(String id) {
        SQLiteDatabase db = getReadableDatabase();

        @SuppressLint("Recycle")
        //Cursor cursor = db.rawQuery("SELECT * FROM User WHERE id = " + id, null);
                Cursor cursor = db.rawQuery("SELECT * FROM User WHERE id = " + '"' + id + '"', null);

        if(cursor.getCount() != 0) {
            return true;
        }

        return false;
    }

    public String getResult(String userId, String password) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE id = " + '"' + userId + '"' + " AND password = " + '"' + password + '"' , null);

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            result = cursor.getString(2);

            return result;
        }

        return result;
    }
}



