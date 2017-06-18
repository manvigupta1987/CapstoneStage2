package com.example.manvi.walkmore.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.data.WalkMorePreferences;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
  * create an instance of this fragment.
 */
public class GoalDialogueFragment extends DialogFragment {
    public GoalDialogueFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getActivity().getString(R.string.daily_goal));
        //alert.setMessage("Enter your goal here");
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton(getActivity().getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                String srt = input.getEditableText().toString();
                if(!srt.equals("")) {
                    WalkMorePreferences.editDailyGoal(getActivity(), Integer.parseInt(srt));
                    Toast.makeText(getActivity(),getString(R.string.goal_updated), Toast.LENGTH_SHORT).show();
                }
            } // End of onClick(DialogInterface dialog, int whichButton)
        }); //End of alert.setPositiveButton
        alert.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
            }
        }); //End of alert.setNegativeButton
        AlertDialog alertDialog = alert.create();
        if(alertDialog!=null) {
            if( alertDialog.getWindow()!=null) {
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                alertDialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
        return alertDialog;
    }
}
