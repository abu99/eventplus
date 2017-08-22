package com.eventplus.eventplus.ui;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Oops Error").setMessage("Error Occured").setPositiveButton("OK", null);
        AlertDialog dialog = alertDialog.create();

        return dialog;
    }
}
