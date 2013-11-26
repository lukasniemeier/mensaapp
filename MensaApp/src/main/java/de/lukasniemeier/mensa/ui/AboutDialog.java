package de.lukasniemeier.mensa.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;

import de.lukasniemeier.mensa.R;

/**
 * Created on 19.09.13.
 */
public class AboutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final SpannableString s = new SpannableString(getActivity().getString(R.string.about_text));
        Linkify.addLinks(s, Linkify.ALL);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage(s)
                .setTitle(getActivity().getString(R.string.about_title))
                .setPositiveButton(getActivity().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
}
