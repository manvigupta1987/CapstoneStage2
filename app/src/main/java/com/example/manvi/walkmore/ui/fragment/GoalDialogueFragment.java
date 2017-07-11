package com.example.manvi.walkmore.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.data.WalkMorePreferences;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalDialogueFragment extends DialogFragment {

    @BindView(R.id.dialog_goal)
    EditText mDailyGoals;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View custom = inflater.inflate(R.layout.edit_daily_goal, null);

        ButterKnife.bind(this, custom);
        alert.setView(custom);
        alert.setTitle(getActivity().getString(R.string.daily_goal));
        alert.setPositiveButton(android.R.string.ok, null);
        alert.setNegativeButton(android.R.string.cancel, null);
        alert.setCancelable(false);

        alert.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
            }
        }); //End of alert.setNegativeButton

        final AlertDialog alertDialog = alert.create();
        if (alertDialog != null) {
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                window.setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
        if(alertDialog!=null) {
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button position = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    position.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String srt = mDailyGoals.getEditableText().toString();
                            if (!srt.equals("") && srt.length() != 0) {
                                int dailyGoal = Integer.parseInt(srt);
                                if (dailyGoal > 0) {
                                    //Preconditions.checkArgument(dailyGoal > 0, "Illegal Argument passed: value %s.", dailyGoal);
                                    WalkMorePreferences.editDailyGoal(getActivity(), dailyGoal);
                                    Toast.makeText(getActivity(), getString(R.string.goal_updated), Toast.LENGTH_SHORT).show();
                                    dismissAllowingStateLoss();
                                } else {
                                    mDailyGoals.setError(getString(R.string.enter_goal_error));
                                }
                            }else {
                                mDailyGoals.setError(getString(R.string.enter_goal_error));
                            }

                        }
                    });
                }
            });
        }
        return alertDialog;
    }
}
