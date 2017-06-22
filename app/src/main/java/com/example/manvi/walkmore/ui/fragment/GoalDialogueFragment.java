package com.example.manvi.walkmore.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.data.WalkMorePreferences;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalDialogueFragment extends DialogFragment {

    @BindView(R.id.dialog_goal)
    EditText mDailyGoals;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View custom = inflater.inflate(R.layout.edit_daily_goal, null);

        ButterKnife.bind(this, custom);
        alert.setView(custom);
        alert.setTitle(getActivity().getString(R.string.daily_goal));

        alert.setPositiveButton(getActivity().getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                String srt = mDailyGoals.getEditableText().toString();
                if (!srt.equals("")) {
                    WalkMorePreferences.editDailyGoal(getActivity(), Integer.parseInt(srt));
                    Toast.makeText(getActivity(), getString(R.string.goal_updated), Toast.LENGTH_SHORT).show();
                    dismissAllowingStateLoss();
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
        if (alertDialog != null) {
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                window.setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
        return alertDialog;
    }
}
