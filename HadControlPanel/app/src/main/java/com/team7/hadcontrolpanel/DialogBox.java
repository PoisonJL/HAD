package com.team7.hadcontrolpanel;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;


/**
 *public class DialogBox
 */
@SuppressLint("ValidFragment")
public class DialogBox extends AppCompatDialogFragment {

    private String title, message;

    public DialogBox (String title, String message) {
        this.title = title;
        this.message = message;
    }


    /**
     *what happens when the dialog is created.
     */
    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceSate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    // whenever data at this location is updated.
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}