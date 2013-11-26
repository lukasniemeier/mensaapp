package de.lukasniemeier.mensa.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import de.lukasniemeier.mensa.R;

/**
 * Created on 18.09.13.
 */
public class MenuViewEmptyFragment extends MenuViewSpecialFragment {

    public static MenuViewEmptyFragment create() {
        return new MenuViewEmptyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Calendar now = Calendar.getInstance();

        View view = inflater.inflate(R.layout.fragment_menu_empty_view, container, false);
        TextView textView = (TextView) view.findViewById(R.id.fragment_menu_empty_text);
        if (now.get(Calendar.HOUR_OF_DAY) <= 12) {
            textView.setText(R.string.menu_empty_description_too_early);
        } else {
            textView.setText(R.string.menu_empty_description_too_late);
        }
        return view;
    }
}
