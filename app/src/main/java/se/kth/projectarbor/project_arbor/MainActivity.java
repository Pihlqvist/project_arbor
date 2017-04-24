package se.kth.projectarbor.project_arbor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        double LONGITUDE = 17.951595 ;
        double LATITUDE = 59.404890;

        Environment environment = new Environment(LATITUDE, LONGITUDE);
        double temp = environment.getTemp();
        Log.d("ARBRO", "everything went fine Temp: " + temp);
    }

}

// this is o