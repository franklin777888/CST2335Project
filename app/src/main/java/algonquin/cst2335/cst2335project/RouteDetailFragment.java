package algonquin.cst2335.cst2335project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * This class is used to get and show the detail of one selected item on the fragment.
 */
public class RouteDetailFragment extends Fragment {
    OCTransport.ListItem chosenRoute;

    public RouteDetailFragment(OCTransport.ListItem route){
        chosenRoute=route;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsView=inflater.inflate(R.layout.route_details,container,false);
        TextView destination=detailsView.findViewById(R.id.des);
        TextView latitude =detailsView.findViewById(R.id.lat);
        TextView longitude=detailsView.findViewById(R.id.longtitude);
        TextView startTime=detailsView.findViewById(R.id.startTime);
        TextView adjustedScheduleTime =detailsView.findViewById(R.id.adjustTime);
        destination.setText("Destination is: "+chosenRoute.routeHeading);
        latitude.setText("The nearest bus latitude is:  "+chosenRoute.latitude);
        longitude.setText("The nearest bus longitude is: "+chosenRoute.longitude);
        startTime.setText("The nearest bus start time is: "+chosenRoute.startTime);
        adjustedScheduleTime.setText("The nearest bus adjusted schedule time is: "+chosenRoute.adjustedScheduleTime);
        Button closeButton=detailsView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(closeClicked->{
            //get the parent activity in order to set the button in parent activity.
            ((OCTransport)getActivity()).enter.setVisibility(View.VISIBLE);
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
        return detailsView;
    }
}
