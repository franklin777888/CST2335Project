package algonquin.cst2335.cst2335project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * This class is used to display no data is available from the server.
 */
public class NoDataFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View detailsView=inflater.inflate(R.layout.no_data_fragment,container,false);


        TextView hint=detailsView.findViewById(R.id.noData);
        hint.setText("Sorry, there is no data currently available.");

        Button closeButton=detailsView.findViewById(R.id.closeButton2);
        closeButton.setOnClickListener(closeClicked->{
            //get the parent activity in order to set the button in parent activity.
            ((OCTransport)getActivity()).enter.setVisibility(View.VISIBLE);
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
        return detailsView;
    }

}
