package com.eventplus.eventplus.ui;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.eventplus.eventplus.R;

public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(R.string.error_dialog_title).setMessage(R.string.error_dialog_message).setPositiveButton(R.string.ok_button_title, null);
        AlertDialog dialog = alertDialog.create();

        return dialog;
    }
}
