package algonquin.cst2335.cst2335project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ChargingDetailFragment extends Fragment {
    CarMainActivity.ChargingListItem chosenLocation;
    int chosenPosition;
    public ChargingDetailFragment(CarMainActivity.ChargingListItem location, int position){
        chosenLocation = location;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsView = inflater.inflate(R.layout.charging_detail,container,false);


        ImageView imageView = detailsView.findViewById(R.id.imageView);
        TextView lacationView = detailsView.findViewById(R.id.loacationTitle);
        TextView latitudeView = detailsView.findViewById(R.id.latitudeView);
        TextView longitudeView = detailsView.findViewById(R.id.longitudeView);
        TextView telView = detailsView.findViewById(R.id.telView);

        lacationView.setText("Location Title: " + chosenLocation.locationTitle);
        latitudeView.setText("Latitude: " + chosenLocation.latitude);
        longitudeView.setText("Longitude: " + chosenLocation.longitude);
        telView.setText("Telphone Number: " +chosenLocation.telNumber);



        return detailsView;
    }
}
