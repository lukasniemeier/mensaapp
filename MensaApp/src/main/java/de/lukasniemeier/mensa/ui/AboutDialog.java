package de.lukasniemeier.mensa.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import de.lukasniemeier.mensa.R;

/**
 * Created on 19.09.13.
 */
public class AboutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage(getActivity().getString(R.string.about_text))
                .setTitle(getActivity().getString(R.string.about_title))
                .setPositiveButton(getActivity().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
}
