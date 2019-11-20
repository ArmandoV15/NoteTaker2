package com.example.notetaker2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notesDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NOTES = "tableNotes";
    private static final String ID = "_id";
    static final String TITLE = "title";
    private static final String TYPE = "type";
    private static final String CONTENT = "content";

    public NoteOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreate = "CREATE TABLE " + TABLE_NOTES + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                TYPE+ " TEXT, " +
                CONTENT + " TEXT)";

        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertNote(Note note){
        String sqlInsert = "INSERT INTO " + TABLE_NOTES + " VALUES(null, '" +
                note.getTitle() + "', '" +
                note.getType() + "', '" +
                note.getContent() + "')";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlInsert);
        db.close();
    }

    public Cursor getSelectAllNotesCursor(){
        String sqlSelect = "SELECT * FROM " + TABLE_NOTES;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSelect, null);
        return cursor;
    }

    public void updateNoteById(int id, Note newNote){
        String sqlUpdate = "UPDATE " + TABLE_NOTES + " SET " + TITLE + " = '" +
                newNote.getTitle() + "', " + TYPE + " = '" +
                newNote.getType() + "', " + CONTENT+ " = '" +
                newNote.getContent() + "' WHERE " + ID + " = " + id;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlUpdate);
        db.close();
    }

    public void deleteAllNotes(){
        String sqlDelete = "DELETE FROM " + TABLE_NOTES;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlDelete);
        db.close();
    }

    public Note getNoteById(int id) {
        Note note = new Note();
        String sqlSelect = "SELECT * FROM " + TABLE_NOTES +
                " WHERE _id = " + id;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSelect, null);

        if (cursor.moveToNext()) {
            note.setId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setType(cursor.getString(2));
            note.setContent(cursor.getString(3));
            db.close();
            return note;
        }

        db.close();
        return null;
    }

    public void deleteIndividualNote(int id){
        String sqlDelete = "DELETE FROM " + TABLE_NOTES +
                " WHERE " + ID + " = " + id;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sqlDelete);
        db.close();
    }
}
