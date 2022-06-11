package com.example.diarycloud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DiaryCloud";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Diary";

    private static final String ID_FIELD = "id";
    private static final String TITLE_FIELD = "title";
    private static final String FEEL_FIELD = "feeling";
    private static final String MAIN_FIELD = "main";
    private static final String DATE_FIELD = "date";
    private static final String LOC_FIELD = "location";
    private static final String ADDRESS_FIELD = "address";
    private static final String MEDIA_FIELD = "media";
    private static final String DELETED_FIELD = "deleted";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "("
                + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE_FIELD + " TEXT, "
                + FEEL_FIELD + " TEXT, "
                + MAIN_FIELD + " TEXT, "
                + DATE_FIELD + " TEXT, "
                + LOC_FIELD + " TEXT, "
                + ADDRESS_FIELD + " TEXT, "
                + MEDIA_FIELD + " TEXT, "
                + DELETED_FIELD + " INTEGER DEFAULT 0) ";
        db.execSQL(query);
    }

    public void readDB() {
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor result = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DATE_FIELD + " DESC"
        )) {
            if (result.getCount() != 0) {
                while (result.moveToNext()) {
                    if (result.getInt(8) == 0) {
                        int id = result.getInt(0);
                        String title = result.getString(1);
                        String feeling = result.getString(2);
                        String mainText = result.getString(3);
                        String date = result.getString(4);
                        String location = result.getString(5);
                        String address = result.getString(6);
                        String media = result.getString(7);
                        Memory mem = new Memory(id, title, feeling, mainText, date, location, address, media);
                        ListDiaryActivity.memList.add(mem);
                    }
                }
            }
        }
    }

    public void addMemory(Memory mem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE_FIELD, mem.getMemTitle());
        contentValues.put(FEEL_FIELD, mem.getMemFeeling());
        contentValues.put(MAIN_FIELD, mem.getMemMain());
        contentValues.put(DATE_FIELD, mem.getMemDate());
        contentValues.put(LOC_FIELD, mem.getMemLocation());
        contentValues.put(ADDRESS_FIELD, mem.getMemAddress());
        contentValues.put(MEDIA_FIELD, mem.getMemMedia());

        db.insert(TABLE_NAME, null, contentValues);
        ListDiaryActivity.memList.add(mem);
    }

    public void deleteMemory(Memory mem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DELETED_FIELD, 1);

        String selection = ID_FIELD + " LIKE ?";
        String[] selectionArgs = {Integer.toString(mem.getMemID())};

        db.update(TABLE_NAME, values, selection, selectionArgs);
        ListDiaryActivity.memList.remove(mem);
    }

    public int getDatabaseSize() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor sizeCursor = db.rawQuery("SELECT COUNT(*) FROM" + TABLE_NAME, null);
        sizeCursor.moveToFirst();
        int size = sizeCursor.getInt(0);
        sizeCursor.close();
        return size;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
