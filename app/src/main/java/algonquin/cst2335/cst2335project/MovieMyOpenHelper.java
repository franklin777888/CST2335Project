package algonquin.cst2335.cst2335project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class to set up SQL queries to create the database table
 */
public class MovieMyOpenHelper extends SQLiteOpenHelper {
    public static final String name = "TheMovieDatabase";
    public static final int version = 1;
    public static final String TABLE_NAME = "Movies";
    public static final String col_id = "_id";
    public static final String col_title = "MovieTitle";
    public static final String col_year = "Year";
    public static final String col_rating = "Rating";
    public static final String col_runtime = "Runtime";
    public static final String col_actors = "Actors";
    public static final String col_plot = "Plot";
    public static final String col_image = "ImageURL";

    /**
     * Constructor
     * @param context context of movie database
     */
    public MovieMyOpenHelper(Context context) {
        super(context, name, null, version);
    }

    /**
     * This method to create the table.
     * @param db database of movie
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + col_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + col_title + " TEXT, "
                + col_year + " INTEGER, "
                + col_rating + " TEXT, "
                + col_runtime + " TEXT, "
                + col_actors + " TEXT, "
                + col_plot + " TEXT, "
                + col_image + " TEXT);");
    }

    /**
     * This method to drop table if exits, and then create the table
     * @param db database
     * @param oldVersion old version
     * @param newVersion new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME + ";") ;
        onCreate(db);
    }
}
