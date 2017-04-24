package se.kth.projectarbor.project_arbor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class TreeGame extends Activity {

    private ToggleButton mWalk;
    private TextView weatherView;
    private TextView tempView;
    private TextView hpView;
    private TextView treeView;
    private TextView distanceView;

    private Float distance;
    private Tree tree;
    private Environment environment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_game);

        setupValues();

        mWalk = (ToggleButton) findViewById(R.id.toggleButton);
        mWalk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Intent intent = new Intent(TreeGame.this, MainService.class);
                    intent.putExtra("MESSAGE_TYPE", MainService.MSG_START);
                    startService(intent);
                } else {
                    // The toggle is disabled
                    Intent intent = new Intent(TreeGame.this, MainService.class);
                    intent.putExtra("MESSAGE_TYPE", MainService.MSG_STOP);
                    startService(intent);
                    setupValues();
                }
            }
        });


    }

//    public void onClick(View view){
//
//                Button startButton = (Button) findViewById(R.id.startButton);
//                if(startButton.getText().equals("Start Walking")){
//                    startButton.setText("Stop Walking");
//                }
//                else {
//                    startButton.setText("Start Walking");
//                }
//
//
//
//        TextView weather = (TextView) findViewById(R.id.tvWeather);
//        Environment E = new Environment(59.404890, 17.951595);
//        TextView hp = (TextView) findViewById(R.id.tvHP);
//        hp.setText("HP: ".concat(getHP()));
//        TextView temp = (TextView) findViewById(R.id.tvTemp);
//        double te = E.getTemp();
//        String t = Double.toString(te);
//        temp.setText("temp: ".concat(t));
//
//        weather.setText("Weather: " + E.getWeather().toString());
//
//
//    }

    public String getHP(){
        return "10";
    }

    private void setupValues() {
        weatherView = (TextView) findViewById(R.id.tvWeather);
        tempView = (TextView) findViewById(R.id.tvTemp);
        hpView = (TextView) findViewById(R.id.tvHP);
        treeView = (TextView) findViewById(R.id.tvTree);
        distanceView = (TextView) findViewById(R.id.tvDistance);

        List<Object> list = DataManager.readState(getApplicationContext(), MainService.filename);

        Log.d("ARBOR", "TreeGame, List.size() " + list.size());
        tree = (Tree) list.get(0);
        distance = (Float) list.get(1);
        environment = (Environment) list.get(2);

        weatherView.setText("Weather: " + environment.getWeather().toString());
        tempView.setText("Temp: " + environment.getTemp());
        hpView.setText("HP: " + tree.getHealth());
        treeView.setText("Tree, Phase: " + tree.getTreePhase());
        distanceView.setText("Distance: " + distance.toString());


    }



}
