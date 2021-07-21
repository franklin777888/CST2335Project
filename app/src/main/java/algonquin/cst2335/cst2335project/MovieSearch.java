package algonquin.cst2335.cst2335project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MovieSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_search);

        TextView myText = findViewById(R.id.textView);
        EditText myEdit = findViewById(R.id.movieTextField);
        Button myButton = findViewById(R.id.searchButton);
        RecyclerView movieList = findViewById(R.id.myrecycler);
        Context context = getApplicationContext();


        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String movieName = prefs.getString("movieName", "");
        myEdit.setText(movieName);

        myButton.setOnClickListener(    ( vw ) -> {
            String editString = myEdit.getText().toString();
            myText.setText("Your Movie Search is: " + editString);
            Toast.makeText(context, "Future use: search pass/fail",
                    Toast.LENGTH_SHORT).show();
        }   );
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = prefs.edit();
        EditText myEdit = findViewById(R.id.movieTextField);
        editor.putString("movieName", myEdit.getText().toString());
        editor.apply();
    }


}
