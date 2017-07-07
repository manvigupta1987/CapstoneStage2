package com.example.manvi.walkmore.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.data.WalkMorePreferences;
import com.google.common.base.Preconditions;

/**
 * Created by manvi on 25/5/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DialogueUtill {

    public static void showDialogue(final Context context){
        Preconditions.checkNotNull(context, "context is null");
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(context.getString(R.string.daily_goal));
        //alert.setMessage("Enter your goal here");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton(context.getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                String srt = input.getEditableText().toString();
                if(!srt.equals("")) {
                    WalkMorePreferences.editDailyGoal(context, Integer.parseInt(srt));
                    Toast.makeText(context,context.getString(R.string.goal_updated), Toast.LENGTH_SHORT).show();
                }
            } // End of onClick(DialogInterface dialog, int whichButton)
        }); //End of alert.setPositiveButton
        alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
            alertDialog.show();
        }
    }




    public static void showNoInternetConnectivity(final Context context){
        Preconditions.checkNotNull(context, "context is null");
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(context.getString(R.string.no_Connectivity));
        alert.setMessage(context.getString(R.string.no_internet));
        alert.setPositiveButton(context.getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                dialog.cancel();
            } // End of onClick(DialogInterface dialog, int whichButton)
        }); //End of alert.setPositiveButton
        AlertDialog alertDialog = alert.create();
        if(alertDialog!=null) {
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alertDialog.show();
        }
    }

    public static void showHeightDialogue(final Context context){
        Preconditions.checkNotNull(context, "context is null");
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        //alert.setTitle(context.getString(R.string.enter_mandatory_info));
        alert.setMessage(context.getString(R.string.enter_mandatory_info));
        alert.setPositiveButton(context.getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                dialog.cancel();
            } // End of onClick(DialogInterface dialog, int whichButton)
        }); //End of alert.setPositiveButton
        AlertDialog alertDialog = alert.create();
        if(alertDialog!=null) {
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alertDialog.show();
        }
    }
}
