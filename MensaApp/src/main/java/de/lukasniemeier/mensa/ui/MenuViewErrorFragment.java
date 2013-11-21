package de.lukasniemeier.mensa.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.lukasniemeier.mensa.R;

/**
 * Created on 18.09.13.
 */
public class MenuViewErrorFragment extends MenuViewSpecialFragment {

    private static final String ARGS_MESSAGE = "errorMessageArgument";

    public static MenuViewErrorFragment create(String errorMessage) {
        MenuViewErrorFragment fragment = new MenuViewErrorFragment();
        fragment.setArguments(createArgumentBundle(errorMessage));
        return fragment;
    }

    private static Bundle createArgumentBundle(String errorMessage) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGS_MESSAGE, errorMessage);
        return arguments;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_error_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();

        Button button = (Button) getView().findViewById(R.id.fragment_menu_error_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRefreshRequested();
            }
        });

        TextView text = (TextView) getView().findViewById(R.id.fragment_menu_error_text);
        if (arguments.containsKey(ARGS_MESSAGE)) {
            text.setText(arguments.getString(ARGS_MESSAGE));
        } else {
            text.setText(getString(R.string.menu_error_description));
        }
    }
}
