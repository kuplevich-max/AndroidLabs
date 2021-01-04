package Adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbHelper extends SQLiteOpenHelper
{
    private static final String MY_DATABASE = "myDatabase.db";
    private static final int SCHEMA = 1;
    static final String TABLE = "exes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PREPARE = "prepare";
    public static final String COLUMN_WORK = "work";
    public static final String COLUMN_CHILL = "chill";
    public static final String COLUMN_CYCLES = "cycles";
    public static final String COLUMN_SETS = "sets";
    public static final String COLUMN_SETCHILL = "setChill";


    public dbHelper(Context context) {
        super(context, MY_DATABASE, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_COLOR
                + " INTEGER, " + COLUMN_TITLE
                + " TEXT, " + COLUMN_PREPARE
                + " INTEGER, " + COLUMN_WORK
                + " INTEGER, " + COLUMN_CHILL
                + " INTEGER, " + COLUMN_CYCLES
                + " INTEGER, " + COLUMN_SETS
                + " INTEGER, " + COLUMN_SETCHILL + " INTEGER);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}