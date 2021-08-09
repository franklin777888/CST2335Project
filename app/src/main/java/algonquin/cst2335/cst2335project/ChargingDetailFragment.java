package algonquin.cst2335.cst2335project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * lab section: CST2335 022
 * This class represent the fragment of the detail information of the clicked car charging station
 */

public class ChargingDetailFragment extends Fragment {
    ChargingInfo sationInfo;
    Bundle dataFromEmp;
    SQLiteDatabase db;
    Button addBtn;
    Button closeButton;
    Button directionBtn;
    String locationDt;
    String latitudeDt;
    String longitudeDt;
    String telNumDt;
    private AppCompatActivity parentActivity;

    /**
     * This method creates and returns the view herachy asscoiated with the fragment about the detail information of the saved favourite charging station
     * pass data from search page to chargingDetailFragment
     * delete data if click the delete button
     * @param inflater inflate views in the fragment
     * @param container contain other views
     * @param savedInstanceState saved instance in bundle
     * @return detail view of the saved favourite charging station
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsView = inflater.inflate(R.layout.charging_detail,container,false);

        dataFromEmp = getArguments();

        TextView locationView = detailsView.findViewById(R.id.loacationTitle);
        TextView latitudeView = detailsView.findViewById(R.id.latitudeView);
        TextView longitudeView = detailsView.findViewById(R.id.longitudeView);
        TextView telNumView = detailsView.findViewById(R.id.telView);

        locationDt = dataFromEmp.getString(CarMainActivity.location_title);
        locationView.setText(getResources().getString(R.string.locat_title)+ locationDt);

        latitudeDt = dataFromEmp.getString(CarMainActivity.latitude_dt);
        latitudeView.setText(getResources().getString(R.string.charg_lat) + latitudeDt);

        longitudeDt =  dataFromEmp.getString(CarMainActivity.longitude_dt);
        longitudeView.setText(getResources().getString(R.string.charg_long) + longitudeDt);

        telNumDt = dataFromEmp.getString(CarMainActivity.telNumber_dt);
        telNumView.setText(getResources().getString(R.string.tel_num) + telNumDt);


        addBtn = detailsView.findViewById(R.id.addButton);
        addBtn.setOnClickListener(addClicked -> {
           // notifyMessageAdded(sationInfo);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Electric car charging station: "+ locationDt)
                    .setTitle(R.string.charging_station_add_favorite)
                    .setNegativeButton(R.string.station_cancel_favorite, (dialog, cl) ->{ })
                    .setPositiveButton(R.string.station_add_favorite, (dialog, cl) ->{
                        ChargOpenHelper opener = new ChargOpenHelper(getContext());
                        db = opener.getWritableDatabase();
                        Cursor results=db.rawQuery("Select * from " + ChargOpenHelper.TABLE_NAME,null);
                     //  if(results.moveToNext()){
                           // Toast.makeText(getContext(), R.string.station_dupulicate_add, Toast.LENGTH_LONG).show();
                      //  }else {
                          ChargingInfo info = new ChargingInfo(locationDt,latitudeDt,longitudeDt,telNumDt,getId());
                            ContentValues newRow = new ContentValues();
                            newRow.put(ChargOpenHelper.col_title, locationDt);
                            newRow.put(ChargOpenHelper.col_latitude, latitudeDt);
                            newRow.put(ChargOpenHelper.col_Longitude, longitudeDt);
                            newRow.put(ChargOpenHelper.col_contact, telNumDt);
                            long newId = db.insert(ChargOpenHelper.TABLE_NAME, null, newRow);
                            info.setId(newId);

                            Toast.makeText(getContext(), R.string.saved_Station_Info, Toast.LENGTH_LONG).show();

                     //   }
                    }
                    )
                    .create().show();
        });


        // get direction from google map
        ImageView direction = detailsView.findViewById(R.id.directionview);
        direction.setOnClickListener(directionClicked->{
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitudeDt+","+ longitudeDt+"&mode=d");
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        });

        return detailsView;
    }

}