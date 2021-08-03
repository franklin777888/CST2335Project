package algonquin.cst2335.cst2335project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class SoccerFavNews extends AppCompatActivity {
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
     * set up database laoding
     */
    private void loadDataFromDatabase() {
        //get a database connection:
        SoccerOpener dbOpener = new SoccerOpener(this);
        socDb = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer

        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {SoccerOpener.COL_ID, SoccerOpener.COL_SOCCERIMG, SoccerOpener.COL_SOCCERTITLE, SoccerOpener.COL_SOCCERDATE, SoccerOpener.COL_SOCCERDESC, SoccerOpener.COL_SOCCERLINK};
        //query all the results from the database:
        Cursor results = socDb.query(false, SoccerOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        //Now the results object has rows of results that match the query.
        //find the column index
        int soccerImgColumnIndex = results.getColumnIndex(SoccerOpener.COL_SOCCERIMG);
        int soccerTitleColumnIndex = results.getColumnIndex(SoccerOpener.COL_SOCCERTITLE);
        int soccerDateColumnIndex = results.getColumnIndex(SoccerOpener.COL_SOCCERDATE);
        int soccerDescColumnIndex = results.getColumnIndex(SoccerOpener.COL_SOCCERDESC);
        int soccerLinkColumnIndex = results.getColumnIndex(SoccerOpener.COL_SOCCERLINK);

        //iterate over the results, return true if there is a next item:
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
    private class MyAdapter extends RecyclerView.Adapter<MyRowViews>{
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=getLayoutInflater();
            View view=inflater.inflate(R.layout.soccer_item,parent,false);
            MyRowViews row=new MyRowViews(view);
            return row;
        }

        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.titleText.setText(elements.get(position).getSoccerTitle());
        }

        @Override
        public int getItemCount() {
            return elements.size();
        }
    }
}