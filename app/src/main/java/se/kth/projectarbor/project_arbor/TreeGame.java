package se.kth.projectarbor.project_arbor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TreeGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_game);
    }

    public void onClick(View view){
        Button startButton = (Button) findViewById(R.id.startButton);
        if(startButton.getText().equals("Start Walking")){
            startButton.setText("Stop Walking");
        }
        else {
            startButton.setText("Start Walking");
        }

        TextView weather = (TextView) findViewById(R.id.tvWeather);
        weather.setText("Weather: ".concat(getWeather()));
        TextView hp = (TextView) findViewById(R.id.tvHP);
        hp.setText("HP: ".concat(getHP()));
    }

    public String getWeather(){
        return "CLOUDY";
    }

    public String getHP(){
        return "10";
    }
}
