package algonquin.cst2335.cst2335project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *this class is to store database
 */
public class SoccerOpenHelper extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "SoccerFavDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "SOCCERNEWS";
    public final static String COL_SOCCERTITLE = "SOCCER_TITLE";
    public final static String COL_SOCCERDATE = "SOCCER_DATE";
    public final static String COL_SOCCERIMG = "SOCCER_IMG";
    public final static String COL_SOCCERDESC = "SOCCER_DESC";
    public final static String COL_SOCCERLINK = "SOCCER_LINK";
    public final static String COL_ID = "ID";

    /**
     * constructor
     * @param ctx
     */
    public SoccerOpenHelper(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    /**
     *
     * @param db
     */
    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SOCCERTITLE + " text,"
                + COL_SOCCERDATE + " text,"
                + COL_SOCCERIMG + " text,"
                + COL_SOCCERDESC + " text,"
                + COL_SOCCERLINK  + " text);");
    }


    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create the new table:
        onCreate(db);
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create the new table:
        onCreate(db);
    }
}
