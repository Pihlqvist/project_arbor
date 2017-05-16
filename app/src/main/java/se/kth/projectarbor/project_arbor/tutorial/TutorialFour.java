package se.kth.projectarbor.project_arbor.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.*;
import android.widget.Button;

import se.kth.projectarbor.project_arbor.MainService;
import se.kth.projectarbor.project_arbor.MainUIActivity;
import se.kth.projectarbor.project_arbor.R;

/**
 * Created by Lazar Cerovic and Johan Andersson on 2017-05-16.
 */


public class TutorialFour extends Fragment {
    private Button mPreviousButton;
    private Button mLetsGo;
    View view;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tutorial_4, container, false);

        mPreviousButton = (Button) view.findViewById(R.id.previous);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TutorialArbor)getActivity()).previous_fragment(view);
            }
        });

        mLetsGo = (Button) view.findViewById(R.id.letsGo);
        mLetsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainService.class);
                intent.putExtra("MESSAGE_TYPE", MainService.MSG_RESUME_GAME);
                getActivity().startService(intent);

            }
        });

        return view;
    }
}
