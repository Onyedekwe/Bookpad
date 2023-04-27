package com.ebookfrenzy.lecturenote.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "Lecturenote.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table CourseTable(_id INTEGER primary key autoincrement, course TEXT, title TEXT, description TEXT, month TEXT, year TEXT, day TEXT, time TEXT)");
        db.execSQL("create Table TodoTable(_id INTEGER primary key autoincrement, description TEXT, status INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            onCreate(db);
    }

    public boolean insertData(String course, String title, String desc, String month, String year, String day, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("course", course);
        contentValues.put("title", title);
        contentValues.put("description", desc);
        contentValues.put("month", month);
        contentValues.put("year", year);
        contentValues.put("day", day);
        contentValues.put("time", time);
        long result = db.insert("CourseTable", null, contentValues);
        return result != -1;
    }

    public boolean insertTask(String description, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", description);
        contentValues.put("status", status);
        long result = db.insert("TodoTable", null, contentValues);
        return result != -1;
    }


    public boolean updatedata(String course, String temptitle, String title, String tempdesc, String desc, String month, String year, String day, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("title", title);
        contentValues.put("description", desc);
        contentValues.put("month", month);
        contentValues.put("year", year);
        contentValues.put("day", day);
        contentValues.put("time", time);

        Cursor cursor = db.rawQuery("select * from CourseTable where course = ? ", new String[]{course});
        if (cursor.getCount() > 0) {
            long result = db.update("CourseTable", contentValues, "course = ? AND title = ? AND description = ?", new String[]{course, temptitle, tempdesc});
            return result != -1;

        } else {
            cursor.close();
            return false;
        }
    }

    public boolean updatetask(String description, String temp_description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", description);
        Cursor cursor = db.rawQuery("select * from TodoTable where description = ? ", new String[]{temp_description});
        if (cursor.getCount() > 0) {
            long result = db.update("TodoTable", contentValues, "description = ? ", new String[]{temp_description});
            return result != -1;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean updateStatus(String description, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        Cursor cursor = db.rawQuery("select * from TodoTable where description = ? ", new String[]{description});
        if (cursor.getCount() > 0) {
            long result = db.update("TodoTable", contentValues, "description = ? ", new String[]{description});
            return result != -1;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean editCourse(String course, String tempcourse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("course", course);
        Cursor cursor = db.rawQuery("select * from CourseTable where course = ? ", new String[]{tempcourse});
        if (cursor.getCount() > 0) {
            long result = db.update("CourseTable", contentValues, "course = ? ", new String[]{tempcourse});
            return result != -1;
        } else {
            cursor.close();
            return false;
        }
    }

    public void deletecourse(String course) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from CourseTable where course = ?", new String[]{course});
        if (cursor.getCount() > 0) {
            long result = db.delete("CourseTable", "course = ?", new String[]{course});
        }
    }

    public void deletetask(String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from TodoTable where description = ?", new String[]{description});
        if (cursor.getCount() > 0) {
            long result = db.delete("TodoTable", "description = ?", new String[]{description});
        } else {
        cursor.close();
        }
    }


    public boolean deletetopic(String topic) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from CourseTable where title = ?", new String[]{topic});
        if (cursor.getCount() > 0) {
            long result = db.delete("CourseTable", "title = ?", new String[]{topic});
            return result != -1;
        } else {
            cursor.close();
            return false;
        }
    }


    public Cursor getdata() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM CourseTable ", null);
    }

    public Cursor gettask() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM TodoTable ", null);
    }

    public Cursor getTopics(String course) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from CourseTable where course = ?", new String[]{course});
    }

    public Cursor getstatus(String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from TodoTable where description = ?", new String[]{description});
    }

    public Cursor getDate(String course, String topics) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from CourseTable where course = ? AND title = ?", new String[]{course, topics});
    }


    public Cursor getAll(String course, String topic) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from CourseTable where course = ? AND title = ?", new String[]{course, topic});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("CourseTable", null, null);
        db.close();
    }


}
