package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class GenderHeightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_height);

        String[] gender_choice = {"Female", "Male", "Non-binary"};
        Spinner spinner = (Spinner) findViewById(R.id.genderSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner, R.id.genderChoice, gender_choice);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        NumberPicker meterPick = (NumberPicker) findViewById(R.id.heightMeter);
        NumberPicker centiMeterPick = (NumberPicker) findViewById(R.id.heightCM);

        meterPick.setMinValue(1);
        meterPick.setMaxValue(2);
        centiMeterPick.setMinValue(0);
        centiMeterPick.setMaxValue(99);

        Button saveBtn = (Button) findViewById(R.id.saveHeightGenderBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }


        });

    }

}
