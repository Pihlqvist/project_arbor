package se.kth.projectarbor.project_arbor;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.*;
import android.widget.Button;

/**
 * Created by Lazarko on 2017-04-27.
 */

public class Tab1 extends Fragment {

    private Button mNewTree;
    private Button mResume;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab1, container, false);
        // This will create a new tree and start the game logic, it will then
        // take the user to the main view of the tree
        mNewTree = (Button) view.findViewById(R.id.start_button);
        mNewTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.createUser(getContext().getApplicationContext(), MainService.filename);

                Intent intent = new Intent(getActivity(), MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_NEED);
                PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (MainService.ALARM_HOUR * 1000), pendingIntent);

                startActivity(new Intent(getActivity(), TreeGame.class));
            }
        });

        // This will continue a game state and take the user to the main view of the tree
        mResume = (Button) view.findViewById(R.id.resume_button);
        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TreeGame.class));
            }
        });

        return view;

    }


}
