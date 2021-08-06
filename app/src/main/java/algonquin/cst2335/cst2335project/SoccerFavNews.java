package algonquin.cst2335.cst2335project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This class is to set up the soccer favorite news functions
 */
public class SoccerFavNews extends AppCompatActivity {
    /**
     * initialize variables. Use array to store the soccer news elements.
     */
    SQLiteDatabase socDb;
    ArrayList<SoccerNews> elements=new ArrayList<>();
    MyAdapter adt=new MyAdapter();
    public static final String SOCCER_TITLE = "TITLE";
    public static final String SOCCER_IMG = "IMG";
    public static final String SOCCER_DATE = "DATE";
    public static final String SOCCER_DESC = "DESC";
    public static final String SOCCER_LINK = "LINK";
    private boolean isTablet;

    /**
     * set up onCreate Function
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soccer_fav_news);
        loadDataFromDatabase();
        RecyclerView myList = findViewById(R.id.soccerFavNewsList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        myList.setLayoutManager(layoutManager);
        myList.setAdapter(adt);
        //create an adapter object and send it to the songFavourite listVIew

        ImageView goToHome = findViewById(R.id.backHome);
        Intent goBackHome = new Intent(SoccerFavNews.this, SoccerMainActivity.class);
        goToHome.setOnClickListener(click ->{
            startActivity(goBackHome);

        });
        isTablet = findViewById(R.id.soccerFragmentLocation) != null;
    }

    /**
     * set up database loading
     */
    private void loadDataFromDatabase() {
        /**
         * get database connection:
         */
        SoccerOpenHelper dbOpener = new SoccerOpenHelper(this);
        socDb = dbOpener.getWritableDatabase();

        /**
         * query all the results from the database, then find the column index
         */
        String [] columns = {SoccerOpenHelper.COL_ID, SoccerOpenHelper.COL_SOCCERIMG, SoccerOpenHelper.COL_SOCCERTITLE, SoccerOpenHelper.COL_SOCCERDATE, SoccerOpenHelper.COL_SOCCERDESC, SoccerOpenHelper.COL_SOCCERLINK};
        Cursor results = socDb.query(false, SoccerOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        int soccerImgColumnIndex = results.getColumnIndex(SoccerOpenHelper.COL_SOCCERIMG);
        int soccerTitleColumnIndex = results.getColumnIndex(SoccerOpenHelper.COL_SOCCERTITLE);
        int soccerDateColumnIndex = results.getColumnIndex(SoccerOpenHelper.COL_SOCCERDATE);
        int soccerDescColumnIndex = results.getColumnIndex(SoccerOpenHelper.COL_SOCCERDESC);
        int soccerLinkColumnIndex = results.getColumnIndex(SoccerOpenHelper.COL_SOCCERLINK);

        /**
         * iterate over the results, return true if there is a next item:
         */
        while(results.moveToNext())
        {
            String img = results.getString(soccerImgColumnIndex);
            String title = results.getString(soccerTitleColumnIndex);
            String date = results.getString(soccerDateColumnIndex);
            String desc = results.getString(soccerDescColumnIndex);
            String link = results.getString(soccerLinkColumnIndex);

            //add the new Contact to the array list:
            SoccerNews soccerFav = new SoccerNews();
            soccerFav.setSoccerImg(img);
            soccerFav.setSoccerTitle(title);
            soccerFav.setSoccerDate(date);
            soccerFav.setSoccerDescription(desc);
            soccerFav.setSoccerLink(link);
            elements.add(soccerFav);
        }

    }

    /**
     * Use RecyclerView to hold the soccer news elements
     */
    private class MyRowViews extends RecyclerView.ViewHolder{
        TextView titleText;
        public MyRowViews(View itemView) {
            super(itemView);
            titleText=itemView.findViewById(R.id.soccerTitle);

            itemView.setOnClickListener(c->{
                int position=getAbsoluteAdapterPosition();
                Bundle dataToPass = new Bundle();
                dataToPass.putString(SOCCER_IMG,elements.get(position).getSoccerImg());
                dataToPass.putString(SOCCER_TITLE,elements.get(position).getSoccerTitle());
                dataToPass.putString(SOCCER_DATE,elements.get(position).getSoccerDate());
                dataToPass.putString(SOCCER_DESC,elements.get(position).getSoccerDescription());
                dataToPass.putString(SOCCER_LINK,elements.get(position).getSoccerLink());
                if(isTablet)
                {
                    SoccerFragment soccerFragment = new SoccerFragment();
                    soccerFragment.setArguments( dataToPass );
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.soccerFragmentLocation, soccerFragment)
                            .commit();
                }
                else
                {
                    Intent nextActivity = new Intent(SoccerFavNews.this, SoccerEmptyActivity.class);
                    nextActivity.putExtras(dataToPass);
                    startActivity(nextActivity);
                }
            });
        }

    }

    /**
     * This is the class to set up adapter
     */
    private class MyAdapter extends RecyclerView.Adapter<MyRowViews>{

        /**
         * This method is to get the view
         * @param parent viewGroup
         * @param viewType int
         * @return View newView
         */
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=getLayoutInflater();
            View view=inflater.inflate(R.layout.soccer_item,parent,false);
            MyRowViews row=new MyRowViews(view);
            return row;
        }

        /**
         * This method is to show the view
         * @param holder show the elements view
         * @param position int
         */
        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.titleText.setText(elements.get(position).getSoccerTitle());
        }

        /**
         * This method is to count the size of the elements
         * @return int elements.size()
         */
        @Override
        public int getItemCount() {
            return elements.size();
        }
    }
}