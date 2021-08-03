package algonquin.cst2335.cst2335project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;


import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

//1. how to load this activity when click button?
public class OCTransport extends AppCompatActivity {
    RecyclerView routeListView;
    ArrayList<ListItem> routesFound=new ArrayList<>();
    MyTransportAdapter adt=new MyTransportAdapter();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transportlayout);
        routeListView=findViewById(R.id.TransportRecycler);
        routeListView.setAdapter(adt);
        routeListView.setLayoutManager(new LinearLayoutManager(this));
        Button enter=findViewById(R.id.buttonEnter);
        Button clear=findViewById(R.id.buttonClear);
        EditText input=findViewById(R.id.TransporteditText);
        enter.setOnClickListener(click->{
            String userInput=input.getText().toString();
            checkOnlyNumberEntered(userInput);

            //get the data from server and then display to the recycler view
            // a loop may be used to load data
            ListItem thisItem=new ListItem(1,"");
            routesFound.add(thisItem);
            adt.notifyItemInserted(routesFound.size()-1);
        });
        //get the user previous input from onpause() function.
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String number = prefs.getString("number", "");
        input.setText(number);


    }
    private class ListItem{
        int routeNumber;
        String routeHeading;

        public ListItem(int routeNumber, String routeHeading) {
            this.routeNumber = routeNumber;
            this.routeHeading = routeHeading;
        }

        public int getRouteNumber() {
            return routeNumber;
        }

        public String getRouteHeading() {
            return routeHeading;
        }

        public void setRouteNumber(int routeNumber) {
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
            //future use maybe for deleting?
            holder.setPosition(position);
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
            //delete list item maybe used?

//            itemView.setOnClickListener(click->{
//                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
//                builder.setMessage("Do you want to delete the message: "+messageText.getText());
//                builder.setTitle("Question:");
//                builder.setNegativeButton("No",(dialog,cl)->{
//                });
//                builder.setPositiveButton("Yes",(dialog,cl)->{
//                    position=getAdapterPosition();
//                    ChatMessage removedMessage=messages.get(position);
//                    messages.remove(position);
//                    adt.notifyItemRemoved(position);
//                    Snackbar.make(messageText,"you deleted message #"+position,Snackbar.LENGTH_LONG)
//                            .setAction("Undo",clk->{
//                                messages.add(position,removedMessage);
//                                adt.notifyItemInserted(position);
//                            })
//                            .show();
//                });
//                builder.create().show();
//            });
            route=itemView.findViewById(R.id.routeItem);

        }
        public void setPosition(int p){position=p;}
    }
    /** This function check if the customer enter
     * valid inputs (only number is allowed).
     *
     * @param number The String object entered and will be checked

     */
    void checkOnlyNumberEntered(String number){

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
        }
        else if(!onlyNumber)
        {
            Toast.makeText(getApplicationContext(),"Only Number is Allowed",Toast.LENGTH_LONG).show();
        }
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

}
