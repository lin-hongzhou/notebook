package com.example.notebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 对数据库进行操作
 * 进行增删改查操作
 * DAO层业务
 */
public class CRUD {

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    private static final String[] columns  = {
            NoteDatabase.ID,
            NoteDatabase.CONTENT,
            NoteDatabase.TIME,
            NoteDatabase.MODE
    };

    public CRUD(Context context) {
        dbHandler = new NoteDatabase(context);
    }

    public void open() {
        db = dbHandler.getWritableDatabase();
    }

    public void close() {
        dbHandler.close();
    }

    //把note加入到database中
    public Note addNote(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.CONTENT,note.getContent());
        contentValues.put(NoteDatabase.TIME,note.getTime());
        contentValues.put(NoteDatabase.MODE,note.getTag());
        long insertId = db.insert(NoteDatabase.TABLE_NAME, null, contentValues);
        note.setId(insertId);
        return note;
    }

    public Note getNote(long id){
        //get a note from database using cursor index
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME,columns,NoteDatabase.ID + "=?",
                new String[]{String.valueOf(id)},null,null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Note e = new Note(cursor.getString(1),cursor.getString(2), cursor.getInt(3));
        return e;
    }

    public List<Note> getAllNote() {
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME,columns,null,null,null,null,null);

        //创建List集合，用来存放note对象
        List<Note> notes = new ArrayList<>();
        //判断游标数值是否大于0
        if (cursor.getCount() > 0) {
            //通过游标进行遍历每条数据的属性，并赋值给对象属性
            while(cursor.moveToNext()) {
                Note note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(NoteDatabase.ID)));
                note.setContent(cursor.getString(cursor.getColumnIndex(NoteDatabase.CONTENT)));
                note.setTime(cursor.getString(cursor.getColumnIndex(NoteDatabase.TIME)));
                note.setTag(cursor.getInt(cursor.getColumnIndex(NoteDatabase.MODE)));
                notes.add(note);
            }
        }
        return notes;
    }

    public int updateNote(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.CONTENT, note.getContent());
        contentValues.put(NoteDatabase.TIME, note.getTime());
        contentValues.put(NoteDatabase.MODE, note.getTag());

        return db.update(NoteDatabase.TABLE_NAME, contentValues,
                NoteDatabase.ID + "=?", new String[] {String.valueOf(note.getId())});
    }

    public void removeNote(Note note) {
        db.delete(NoteDatabase.TABLE_NAME, NoteDatabase.ID + "=" + note.getId(), null);
    }


}
