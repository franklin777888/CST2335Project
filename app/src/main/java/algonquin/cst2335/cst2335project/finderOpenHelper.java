package algonquin.cst2335.cst2335project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DecimalFormat;


public class finderOpenHelper extends SQLiteOpenHelper {
        public static final String name = "TheChargingStationDatabase";
        public static final int version =1;
        public static final String TABLE_NAME = "Elc-car Charging stations";
        public static final String col_title = "LocationTitle";
        public static final String col_latitude = "Latitude";
        public static final String col_Longitude= "Longitude";
        public static final String col_contact= "TelephoneNumber";
        public static final String col_direction="googleMapDirectionURL";

    public finderOpenHelper(Context context) {
        super(context, name,null, version);
    }

    @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("Create table " + TABLE_NAME +"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + col_title +" TEXT,"
                    + col_latitude + " DECIMAL,"
                    + col_Longitude + " DECIMAL,"
                    + col_contact + " INTEGER,"
                    + col_direction + " URL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /* DELETE the exisitng table */
            db.execSQL("drop table if exists "+ TABLE_NAME);
            onCreate(db);

        }
    }


