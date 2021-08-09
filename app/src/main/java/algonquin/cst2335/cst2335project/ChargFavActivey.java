package algonquin.cst2335.cst2335project;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 *
 * lab section: CST2335 022
 * This activity class keep list of saved favourite electric car charging stations
 *
 */

public class ChargFavActivey extends AppCompatActivity {
    ArrayList<ChargingInfo> favouriteInfo = new ArrayList<>();  //hold input location name
    ChargOpenHelper myOpener;

    SQLiteDatabase chargdb;
    FavAdapt adt = new FavAdapt();

    /**
     * This function passes you a layoutInflater object.
     * @param savedInstanceState save instances in bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_charging);


        RecyclerView favLocation = findViewById(R.id.favchargrecycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        favLocation.setLayoutManager(layoutManager);
        favLocation.setAdapter(adt);
        myOpener= new ChargOpenHelper(this);
        chargdb = myOpener.getWritableDatabase();
;
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(closeclick ->{
            Intent mainPage = new Intent(ChargFavActivey.this, CarMainActivity.class);
            startActivity(mainPage);
        });

        Cursor results = chargdb.rawQuery("Select * from " + ChargOpenHelper.TABLE_NAME,null);
        int _idCol = results.getColumnIndex("_id");
        int titleCol = results.getColumnIndex(ChargOpenHelper.col_title);
        int laticol = results.getColumnIndex(ChargOpenHelper.col_latitude);
        int longcol = results.getColumnIndex(ChargOpenHelper.col_Longitude);
        int telcol = results.getColumnIndex(ChargOpenHelper.col_contact);
    //    int dircol = results.getColumnIndex(ChargOpenHelper.col_direction);

        while(results.moveToNext()){  //move to next row, return false if past last row
            long id = results.getInt(_idCol);
            String locationTile = results.getString(titleCol);
            String latitude = results.getString(laticol);
            String longitude = results.getString(longcol);
            String telNumber = results.getString(telcol);
            favouriteInfo.add(new ChargingInfo(locationTile, latitude, longitude, telNumber, id));
        }
    }

    /**
     *this class that inherits from RecyclerView, which is used to represent a favourite saved row
     */
    private class FavRowView extends RecyclerView.ViewHolder {
        TextView favChargItem;
        Button deleteBtn;
        int clickedPosition = -1;
        public static final  String location_title = "LocationTitle";
        public static final String latitude_dt = "Latitude";
        public static final String longitude_dt = "Longitude";
        public static final String telNumber_dt= "TelNumber";

        /**
         * The view that is passed in as a parameter represents the ConstraintLayout that is the root of the row
         *favChargIem button for viewing the details of the clicked station
         * deleteBtn button for delete the information of the station and also delete from the database
         * @param itemView view of the item
         */
        public FavRowView(View itemView) {
            super(itemView);
            favChargItem = itemView.findViewById(R.id.favChargItem);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            //get lacation detail info by click, receive data from ChargFavActivity and launch to CarEmptyActivity
            favChargItem.setOnClickListener(click -> {
                clickedPosition=getAbsoluteAdapterPosition();
                ChargingInfo chargInfo = favouriteInfo.get(clickedPosition);
                Bundle details = new Bundle();
                details.putString(location_title,chargInfo.getLocationTitle());
                details.putString(latitude_dt,chargInfo.getLatitude());
                details.putString(longitude_dt,chargInfo.getLongitude());
                details.putString(telNumber_dt,chargInfo.getTelNumber());

                Intent nextPage = new Intent(ChargFavActivey.this, CarEmptyActivity.class);
                nextPage.putExtras(details);   //passing data from one activity to another activity
                startActivity(nextPage);

            });

            deleteBtn.setOnClickListener(delclick -> {

                clickedPosition = getAbsoluteAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(ChargFavActivey.this);
                builder.setMessage(R.string.deletequestion)
                        .setTitle(R.string.question)
                        .setNegativeButton(R.string.charg_no, (dialog, cl) -> {
                        })
                        .setPositiveButton(R.string.charg_yes, (dialog, cl) -> {
                            //position = getAbsoluteAdapterPosition();
                            ChargingInfo deleteMessage = favouriteInfo.get(clickedPosition);
                            favouriteInfo.remove(clickedPosition);
                            adt.notifyItemRemoved(clickedPosition);

                            chargdb.delete(ChargOpenHelper.TABLE_NAME, "_id=?", new String[]{Long.toString(deleteMessage.getId())});


                            Snackbar.make(deleteBtn, getResources().getString(R.string.charg_deleted) + deleteMessage.getLocationTitle(), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.charg_undo, clk -> {
                                        favouriteInfo.add(clickedPosition, deleteMessage);
                                        adt.notifyItemRemoved(clickedPosition);

                            chargdb.execSQL("Insert into " + ChargOpenHelper.TABLE_NAME + " values('" + deleteMessage.getId()+
                                    "','" + deleteMessage.getLocationTitle() +
                                    "','" + deleteMessage.getLatitude() +
                                    "','" + deleteMessage.getLongitude() +
                                    "','" + deleteMessage.getTelNumber() +"')");
                                    })
                                    .show();
                        })
                    .create().show();
            });
}

    }
    /**
     * This class is responsible for making a View for each station in the data set
     */
        private class FavAdapt extends RecyclerView.Adapter<FavRowView> {

        /**
         * This function is responsible for creating a layout for a favourite station row, and setting the TextViews in code
         * @param parent parent is a parent view that can contain other views
         * @param viewType  the view type of the items
         * @return return the initiated object
         */
            @Override
            public FavRowView onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = getLayoutInflater();
                View loadView = inflater.inflate(R.layout.favorite_charing_row, parent, false);
                FavRowView initRow = new FavRowView(loadView);
                return initRow;
            }
        /**
         * This function is call by RecycleView to display the data at the specified position.
         * @param holder holder of ChargRowViews
         * @param position the specified position of the station in data
         */
            @Override
            public void onBindViewHolder(FavRowView holder, int position) {
                holder.favChargItem.setText(favouriteInfo.get(position).getLocationTitle());
            }

        /**
         * This method count the size of the locationFound array list
         * @return return the size of the Arroy of the location list
         */
            @Override
            public int getItemCount() {
                return favouriteInfo.size();
            }

        }


    }



