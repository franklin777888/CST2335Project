package algonquin.cst2335.cst2335project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button octButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        octButton=findViewById(R.id.OCTranspo);
        octButton.setOnClickListener((click)->{
            Intent navOct=new Intent(MainActivity.this,Navigation.class);
            startActivity(navOct);
        });
   }
}