package Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DbAdapter {
    private final Adapters.dbHelper dbHelper;
    private SQLiteDatabase database;

    public DbAdapter(Context context)
    {
        dbHelper = new Adapters.dbHelper(context.getApplicationContext());
    }

    public DbAdapter open()
    {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbHelper.close();
    }

    private Cursor getAllEntries()
    {
        String[] columns = new String[] {Adapters.dbHelper.COLUMN_ID, Adapters.dbHelper.COLUMN_COLOR,
                Adapters.dbHelper.COLUMN_TITLE, Adapters.dbHelper.COLUMN_PREPARE, Adapters.dbHelper.COLUMN_WORK,
                Adapters.dbHelper.COLUMN_CHILL, Adapters.dbHelper.COLUMN_CYCLES, Adapters.dbHelper.COLUMN_SETS,
                Adapters.dbHelper.COLUMN_SETCHILL};
        return  database.query(Adapters.dbHelper.TABLE, columns, null, null,
                null, null, null);
    }

    public List<Exercise> getExes()
    {
        ArrayList<Exercise> exercises = new ArrayList<>();
        Cursor cursor = getAllEntries();
        if(cursor.moveToFirst())
        {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ID));
                int color = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_COLOR));
                String title = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_TITLE));
                int prepare = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_PREPARE));
                int work = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_WORK));
                int chill = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_CHILL));
                int cycles = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_CYCLES));
                int sets = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_SETS));
                int setChill = cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_SETCHILL));
                exercises.add(new Exercise(id, color, title, prepare, work, chill, cycles, sets, setChill));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return exercises;
    }

    public long getCount()
    {
        return DatabaseUtils.queryNumEntries(database, Adapters.dbHelper.TABLE);
    }

    public Exercise getExe(int id)
    {
        Exercise exe = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?", Adapters.dbHelper.TABLE, Adapters.dbHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst())
        {
            int color = cursor.getInt(cursor.getColumnIndex(Adapters.dbHelper.COLUMN_COLOR));
            String title = cursor.getString(cursor.getColumnIndex(Adapters.dbHelper.COLUMN_TITLE));
            int prepare = cursor.getInt(cursor.getColumnIndex(Adapters.dbHelper.COLUMN_PREPARE));
            int work = cursor.getInt(cursor.getColumnIndex(Adapters.dbHelper.COLUMN_WORK));
            int chill = cursor.getInt(cursor.getColumnIndex(Adapters.dbHelper.COLUMN_CHILL));
            int cycles = cursor.getInt(cursor.getColumnIndex(Adapters.dbHelper.COLUMN_CYCLES));
            int sets = cursor.getInt(cursor.getColumnIndex(Adapters.dbHelper.COLUMN_SETS));
            int setChill = cursor.getInt(cursor.getColumnIndex(Adapters.dbHelper.COLUMN_SETCHILL));
            exe = new Exercise(id, color, title, prepare, work, chill, cycles, sets, setChill);
        }
        cursor.close();
        return exe;
    }

    public long insert(Exercise exercise)
    {

        ContentValues cv = new ContentValues();
        cv.put(dbHelper.COLUMN_COLOR, exercise.color);
        cv.put(dbHelper.COLUMN_TITLE, exercise.title);
        cv.put(dbHelper.COLUMN_PREPARE, exercise.prepare);
        cv.put(dbHelper.COLUMN_WORK, exercise.work);
        cv.put(dbHelper.COLUMN_CHILL, exercise.chill);
        cv.put(dbHelper.COLUMN_CYCLES, exercise.cycles);
        cv.put(dbHelper.COLUMN_SETS, exercise.sets);
        cv.put(dbHelper.COLUMN_SETCHILL, exercise.setChill);
        return  database.insert(dbHelper.TABLE, null, cv);
    }

    public long delete(int id)
    {

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        return database.delete(dbHelper.TABLE, whereClause, whereArgs);
    }

    public long update(Exercise exe)
    {
        String whereClause = dbHelper.COLUMN_ID + "=" + String.valueOf(exe.id);
        ContentValues cv = new ContentValues();
        cv.put(dbHelper.COLUMN_COLOR, exe.color);
        cv.put(dbHelper.COLUMN_TITLE, exe.title);
        cv.put(dbHelper.COLUMN_PREPARE, exe.prepare);
        cv.put(dbHelper.COLUMN_WORK, exe.work);
        cv.put(dbHelper.COLUMN_CHILL, exe.chill);
        cv.put(dbHelper.COLUMN_CYCLES, exe.cycles);
        cv.put(dbHelper.COLUMN_SETS, exe.sets);
        cv.put(dbHelper.COLUMN_SETCHILL, exe.setChill);
        return database.update(dbHelper.TABLE, cv, whereClause, null);
    }

    public void clear()
    {
        dbHelper.onUpgrade(database, 0, 0);
    }
}
