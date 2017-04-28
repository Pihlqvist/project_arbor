package se.kth.projectarbor.project_arbor;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import se.kth.projectarbor.project_arbor.MainUIActivity;

public class NewTreeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tree);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final Button button = (Button) findViewById(R.id.new_tree);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(NewTreeActivity.this, MainUIActivity.class);
                myIntent.putExtra("New_tree_created", false);
                NewTreeActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}
