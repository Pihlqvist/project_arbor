package se.kth.projectarbor.project_arbor.tutorial;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.*;
import android.widget.Button;

import se.kth.projectarbor.project_arbor.MainService;
import se.kth.projectarbor.project_arbor.NewTreeActivity;
import se.kth.projectarbor.project_arbor.R;
import se.kth.projectarbor.project_arbor.Tree;
import se.kth.projectarbor.project_arbor.weather.Environment;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Lazar Cerovic and Johan Andersson on 2017-05-16.
 */


public class TutorialFour extends Fragment {

    private Button mPreviousButton;
    private Button mLetsGo;
    private SharedPreferences sharedPreferences;

    View view;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tutorial_4, container, false);
        sharedPreferences = getContext().getSharedPreferences("se.kth.projectarbor.project_arbor", Context.MODE_PRIVATE);

        mPreviousButton = (Button) view.findViewById(R.id.previous);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TutorialArbor)getActivity()).previous_fragment(view);
            }
        });

        if (sharedPreferences.getBoolean("FIRST_TIME_TUTORIAL", true)) {
            mLetsGo = (Button) view.findViewById(R.id.letsGo);
            mLetsGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences.edit().putBoolean("FIRST_TIME_TUTORIAL", false).apply();
                    getActivity().startActivity(new Intent(getContext(), NewTreeActivity.class));
                }
            });
        }

        else {

            mLetsGo = (Button) view.findViewById(R.id.letsGo);
            mLetsGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

        }

        return view;

    }
}
