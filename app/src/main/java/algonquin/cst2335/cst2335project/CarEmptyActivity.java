package algonquin.cst2335.cst2335project;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

    /**
     *
     * lab section: CST2335 022
     * This class load a empty fragment in the Framelayout with id carframgmentRoom, then load a second fragment over top of the list
     *
    */
public class CarEmptyActivity extends AppCompatActivity {

    /**
     * This method upload a new fragment for ChargDetailFragment
     * also get values from intent that are stored in bundle in the CarMainActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charg_empty_layout);

        Bundle dtp = getIntent().getExtras();
        ChargingDetailFragment charFragment = new ChargingDetailFragment(); //load a fragment
        charFragment.setArguments(dtp);
        FragmentManager fMgr = getSupportFragmentManager();
                fMgr.beginTransaction()
                 .add(R.id.carfragmentRoom,charFragment)
                 .commit();    //this line actually loads the fragment into the specified fragment.

    }
}
