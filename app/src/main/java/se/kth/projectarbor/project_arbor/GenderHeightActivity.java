package se.kth.projectarbor.project_arbor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import se.kth.projectarbor.project_arbor.tutorial.TutorialArbor;

public class GenderHeightActivity extends AppCompatActivity {

    private final static String TAG = "ARBOR_USER_INPUT";

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_height);
        sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE);

        //initializes the spinner
        String[] gender_choice = {"Female", "Male", "Non-binary"};
        final Spinner spinner = (Spinner) findViewById(R.id.genderSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner, R.id.genderChoice, gender_choice);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //initializes the two numberpickers
        final NumberPicker meterPick = (NumberPicker) findViewById(R.id.heightMeter);
        final NumberPicker centiMeterPick = (NumberPicker) findViewById(R.id.heightCM);
        meterPick.setMinValue(1);
        meterPick.setMaxValue(2);
        centiMeterPick.setMinValue(0);
        centiMeterPick.setMaxValue(99);

        //Gets the saved data from sharedpreferences and displays current gender and height by
        //setting the spinner and numberpickers
        if (sharedPreferences.contains("USER_GENDER")) {
            String current_gender = sharedPreferences.getString("USER_GENDER", "Female");
            Log.d(TAG, current_gender);
            switch (current_gender) {
                case "Female":
                    spinner.setSelection(0);
                    break;
                case "Male":
                    spinner.setSelection(1);
                    break;
                case "Non-binary":
                    spinner.setSelection(2);
                    break;
            }
        }

        if (sharedPreferences.contains("USER_HEIGHT")) {
            float current_height = sharedPreferences.getFloat("USER_HEIGHT", 1.5f);
            Log.d(TAG, current_height + "");
            int m = (int) Math.floor(current_height);
            meterPick.setValue(m);
            int cm = (int) Math.round((current_height - m) * 100.0);
            centiMeterPick.setValue(cm);
        }

        //initializes the save button
        if (sharedPreferences.getBoolean("FIRST_TIME_TUTORIAL", true)) {
            Button saveBtn = (Button) findViewById(R.id.saveHeightGenderBtn);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int meter = meterPick.getValue();
                    int centimeter = centiMeterPick.getValue();
                    float height = (float) (meter + centimeter / 100.0);
                    String gender = spinner.getSelectedItem().toString();
                    sharedPreferences.edit().putString("USER_GENDER", gender).apply();
                    sharedPreferences.edit().putFloat("USER_HEIGHT", height).apply();

                    startActivity(new Intent(GenderHeightActivity.this, TutorialArbor.class));
                }

            });
        } else {

            Button saveBtn = (Button) findViewById(R.id.saveHeightGenderBtn);
            saveBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int meter = meterPick.getValue();
                    int centimeter = centiMeterPick.getValue();
                    float height = (float) (meter + centimeter / 100.0);
                    String gender = spinner.getSelectedItem().toString();
                    sharedPreferences.edit().putString("USER_GENDER", gender).apply();
                    sharedPreferences.edit().putFloat("USER_HEIGHT", height).apply();

                    Log.d("arbor_genderHeight", "height " + gender);

                    Intent serviceIntent = new Intent(GenderHeightActivity.this, MainService.class);
                    serviceIntent.putExtra("MESSAGE_TYPE", MainService.MSG_USER_INPUT);
                    startService(serviceIntent);

                    onBackPressed();
                }


            });

        }
    }

}
