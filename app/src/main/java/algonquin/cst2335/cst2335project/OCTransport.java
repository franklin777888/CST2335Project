package algonquin.cst2335.cst2335project;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
public class OCTransport extends AppCompatActivity {
    private String stringURL,routeNum,routeHeadingFor,stopNum;
    RecyclerView routeListView;
    ArrayList<ListItem> routesFound=new ArrayList<>();
    MyTransportAdapter adt;
    Button enter;
    ListItem thisItem;
    SQLiteDatabase db;
    MyOpenHelper opener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transportlayout);
        routeListView=findViewById(R.id.TransportRecycler);
        adt=new MyTransportAdapter();
        routeListView.setAdapter(adt);
        routeListView.setLayoutManager(new LinearLayoutManager(this));
        enter=findViewById(R.id.buttonEnter);
        EditText input=findViewById(R.id.TransporteditText);
        enter.setOnClickListener(click->{
            //reload new recycler adapter to clear former results
            adt=new MyTransportAdapter();
            routeListView.setAdapter(adt);
            routeListView.setLayoutManager(new LinearLayoutManager(this));
            routesFound=new ArrayList<>();
            //check the user input and if not valid, give Alert info.
            String userInput=input.getText().toString();
            if(checkOnlyNumberEntered(userInput)){
                AlertDialog dialog=new AlertDialog.Builder(OCTransport.this)
                        .setTitle("Getting data")
                        .setMessage("Downloading the data...")
                        .setView(new ProgressBar(OCTransport.this))
                        .show();
                //open database and prepare to store list item infomation into database
                opener = new MyOpenHelper(this);
                db=opener.getWritableDatabase();
                ContentValues newRow = new ContentValues();
                // can't deal with url in main thread, so open another thread
                Executor newThread= Executors.newSingleThreadExecutor();
                newThread.execute(()->{
                    try{
                        //make connection
                        stringURL="https://api.octranspo1.com/v2.0/GetRouteSummaryForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo="+ URLEncoder.encode(userInput,"UTF-8")+"&format=json";
                        URL url = new URL(stringURL);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        String text = (new BufferedReader(
                                new InputStreamReader(in, StandardCharsets.UTF_8)))
                                .lines()
                                .collect(Collectors.joining("\n"));

                        //get data from the server
                        JSONObject theDocument = new JSONObject(text);
                        boolean notExistingStopNumber=theDocument.getJSONObject("GetRouteSummaryForStopResult").isNull("StopDescription");
                       //check first if the stop number exists.
                        if(notExistingStopNumber){
                            //alert must run on main ui thread
                            runOnUiThread(()->{
                                dialog.hide();
                                AlertDialog.Builder builder = new AlertDialog.Builder( OCTransport.this );
                                builder.setMessage("No such stop Number currently exists.");
                                builder.setTitle("No Such Stop Number");
                                builder.setNegativeButton("OK",(dia,cl)->{
                                });
                                builder.create().show();
                            });
                        }else{
                            // can't write main ui item in new thread, so go back to main ui thread
                            runOnUiThread(()->{
                                try {
                                    JSONArray routeArray = theDocument.getJSONObject("GetRouteSummaryForStopResult").getJSONObject("Routes").getJSONArray ( "Route" );
                                    for(int i=0;i<routeArray.length();i++) {
                                        JSONObject positioni = routeArray.getJSONObject(i);
                                        routeNum = positioni.getString("RouteNo");
                                        routeHeadingFor = positioni.getString("RouteHeading");
                                        stopNum=theDocument.getJSONObject("GetRouteSummaryForStopResult").getString("StopNo");
                                        //use the retrieved data to load the list in recycler view
                                        thisItem = new ListItem(routeNum, routeHeadingFor,stopNum);
                                        routesFound.add(thisItem);
                                        adt.notifyItemInserted(routesFound.size()-1);
                                        //insert columns in database table
                                        newRow.put(MyOpenHelper.col_stop_number,stopNum);
                                        newRow.put(MyOpenHelper.col_route_number,routeNum);
                                        newRow.put(MyOpenHelper.col_heading_destination,routeHeadingFor);
                                        db.insert(MyOpenHelper.TABLE_NAME,MyOpenHelper.col_route_number,newRow);
                                    }
                                  }catch (JSONException e) {
                                    dialog.hide();
                                    AlertDialog.Builder builder = new AlertDialog.Builder( OCTransport.this );
                                    builder.setMessage("No such stop Number currently exists.");
                                    builder.setTitle("No Such Stop Number");
                                    builder.setNegativeButton("OK",(dia,cl)->{
                                    });
                                    builder.create().show();
                                }
                                dialog.hide();
                            });
                        }
                    }
                    catch (IOException | JSONException ioe){
                        Log.e("Connection error:",ioe.getMessage());
                    }
                });
            }
        });
        //get the user previous input from onpause() function.
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String number = prefs.getString("number", "");
        input.setText(number);
    }
    public class ListItem{
        String routeNumber;
        String routeHeading;
        String stopNum;
        String latitude,longitude,startTime,adjustedScheduleTime;

        public ListItem(String routeNumber, String routeHeading,String stopNum) {
            this.routeNumber = routeNumber;
            this.routeHeading = routeHeading;
            this.stopNum=stopNum;
        }
        public ListItem(String routeNumber, String routeHeading,String stopNum,String latitude,String longitude,String startTime,String adjustedScheduleTime) {
            this.routeNumber = routeNumber;
            this.routeHeading = routeHeading;
            this.stopNum=stopNum;
            this.latitude=latitude;
            this.longitude=longitude;
            this.startTime=startTime;
            this.adjustedScheduleTime=adjustedScheduleTime;
        }
        public String getRouteNumber() {
            return routeNumber;
        }
        public String getRouteHeading() {
            return routeHeading;
        }
        public void setRouteNumber(String routeNumber) {
            this.routeNumber = routeNumber;
        }
        public void setRouteHeading(String routeHeading) {
            this.routeHeading = routeHeading;
        }
    }

    private class MyTransportAdapter extends RecyclerView.Adapter<MyRowViews>{
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View loadedRow = inflater.inflate(R.layout.route_list, parent, false);
            MyRowViews initRow = new MyRowViews(loadedRow);
            return initRow;
        }
        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.route.setText("");
            //after adding the retrieved routes to arraylist(see button listener at on create at the top), set text.
            holder.route.setText("Route No."+routesFound.get(position).routeNumber+" heading for "+routesFound.get(position).routeHeading);
      }
        @Override
        public int getItemCount() {
            return routesFound.size();
        }
    }

    private class MyRowViews extends RecyclerView.ViewHolder{
        TextView route;
        int position=-1;
        public MyRowViews(View itemView) {
            super(itemView);
            route=itemView.findViewById(R.id.routeItem);
            //get route detail info by click
            itemView.setOnClickListener(click->{
                int position=getAdapterPosition();
                userClickedMessage(routesFound.get(position));
           });
            //delete item info by long click
            itemView.setOnLongClickListener(click->{
                AlertDialog.Builder builder = new AlertDialog.Builder( OCTransport.this );
                builder.setMessage("Do you want to delete the item?");
                builder.setTitle("Question:");
                builder.setNegativeButton("No",(dialog,cl)->{
                });
                builder.setPositiveButton("Yes",(dialog,cl)->{
                    position=getAdapterPosition();
                    ListItem removedItem=routesFound.get(position);
                    routesFound.remove(position);
                    adt.notifyItemRemoved(position);
                    Snackbar.make(route,"You deleted the chosen item. ",Snackbar.LENGTH_LONG)
                            .setAction("Undo",clk->{
                                routesFound.add(position,removedItem);
                                adt.notifyItemInserted(position);
                            })
                            .show();
                });
                builder.create().show();
                return true;
            });
        }
    }
    /** This function check if the customer enter
     * valid inputs (only number is allowed). If invalid input entered,
     * a toast will show on the screen.
     * @param number The String object entered and will be checked
     */
    boolean checkOnlyNumberEntered(String number){

        boolean onlyNumber = true;
        boolean empty = false;
        if(number.length()==0){
            empty=true;
        }
        for(int i=0;i<number.length();i++){
            if(!Character.isDigit(number.charAt(i))){
                onlyNumber=false;
            }
        }
        if(empty)
        {
            Toast.makeText(getApplicationContext(),"Nothing entered!",Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!onlyNumber)
        {
            Toast.makeText(getApplicationContext(),"Only Number is Allowed",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    /**
     * use SharedPreferences to store stop number user just entered.
     */
    protected void onPause() {
        super.onPause();
        EditText input=findViewById(R.id.TransporteditText);
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = prefs.edit();
        editor.putString("number", input.getText().toString());
        editor.apply();
    }
    /** this method is used for fragment layout to show detail of one item in the result list,
     * when user click the item.
     * @param route the item object chosen in the list
     */
    public void userClickedMessage(ListItem route) {
        AlertDialog dialog=new AlertDialog.Builder(OCTransport.this)
                .setTitle("Getting data")
                .setMessage("Downloading the data...")
                .setView(new ProgressBar(OCTransport.this))
                .show();
        Executor newThread= Executors.newSingleThreadExecutor();
        newThread.execute(()->{
            try{
                //make connection
                String stringURL="https://api.octranspo1.com/v2.0/GetNextTripsForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo="+ URLEncoder.encode(route.stopNum,"UTF-8")+"&routeNo="+ URLEncoder.encode(route.routeNumber,"UTF-8")+"&format=json";
                URL url = new URL(stringURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String text = (new BufferedReader(
                        new InputStreamReader(in, StandardCharsets.UTF_8)))
                        .lines()
                        .collect(Collectors.joining("\n"));
                //get data from the server
                JSONObject theDocument = new JSONObject(text);
                runOnUiThread(()->{
                        try {
                            JSONObject trip=theDocument.getJSONObject("GetNextTripsForStopResult").getJSONObject("Route").getJSONArray("RouteDirection").getJSONObject(0).getJSONObject("Trips").getJSONArray("Trip").getJSONObject(0);
                            String longtitude=trip.getString("Longitude");
                            String latitude=trip.getString("Latitude");
                            String tripStartTime=trip.getString("TripStartTime");
                            String adjustedScheduleTime=trip.getString("AdjustedScheduleTime");
                            //load the ListItem with data, then the data can be passed to detailfragment by the fragment's constructor.
                            ListItem detail=new ListItem(route.routeNumber,route.routeHeading,route.stopNum,longtitude,latitude,tripStartTime,adjustedScheduleTime);
                            enter.setVisibility(View.INVISIBLE);
                            RouteDetailFragment routeFragment=new RouteDetailFragment(detail);
                            getSupportFragmentManager().beginTransaction().add(R.id.fragment,routeFragment).commit();
                        } catch (JSONException e) {
                            // if there is no correct data from server, display nodata fragment.
                            enter.setVisibility(View.INVISIBLE);
                            NoDataFragment noData=new NoDataFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.fragment,noData).commit();
                        }
                    dialog.hide();
                });
            }
            catch (IOException | JSONException ioe){
                Log.e("Connection error:",ioe.getMessage());
            }
        });
    }
}
