package algonquin.cst2335.cst2335project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyOpenHelper extends SQLiteOpenHelper {
    public static final String name="TheDatabase";
    public static final int version=1;
    public static final String TABLE_NAME="BusStop";
    public static final String col_stop_number="StopNumber";
    public static final String col_route_number="RouteNumber";
    public static final String col_heading_destination="HeadingDestination";


    public MyOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +col_stop_number+" TEXT,"+col_route_number+" TEXT,"+col_heading_destination+" TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);

    }
}
