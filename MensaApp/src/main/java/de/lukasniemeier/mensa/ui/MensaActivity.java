package de.lukasniemeier.mensa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Mensa;
import de.lukasniemeier.mensa.ui.adapter.CardState;
import de.lukasniemeier.mensa.ui.adapter.MensaAdapter;
import de.lukasniemeier.mensa.utils.DefaultMensaManager;

public class MensaActivity extends BaseActivity {

    public static final String EXTRA_NO_DEFAULT_REDIRECT = "EXTRA_NO_DEFAULT_REDIRECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        redirectOnDefaultMensa();

        setContentView(R.layout.activity_mensa);
        boolean isFirstStart = savedInstanceState == null;

        setupGridView(isFirstStart);
    }

    private void setupGridView(boolean firstStart) {
        MensaAdapter adapter = new MensaAdapter(this);
        GridView mensaList = (GridView) findViewById(R.id.mensa_list_view);
        mensaList.setAdapter(adapter);
        adapter.addAll(Mensa.getMensas(), firstStart);
        mensaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                @SuppressWarnings("unchecked") CardState<Mensa> selectedMensa = (CardState<Mensa>) adapterView.getItemAtPosition(i);
                selectMensa(selectedMensa.getValue());
            }
        });
        mensaList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return true;
            }
        });
    }

    private boolean redirectOnDefaultMensa() {
        Intent startIntent = getIntent();
        if (startIntent.hasExtra(EXTRA_NO_DEFAULT_REDIRECT)) {
            return false;
        }
        startIntent.removeExtra(EXTRA_NO_DEFAULT_REDIRECT);
        DefaultMensaManager defaultManager = new DefaultMensaManager(this);
        if (defaultManager.hasDefault()) {
            Mensa defaultMensa = Mensa.getMensa(defaultManager.getDefaultMensaShortName());
            if (defaultMensa != null) {
                selectMensa(defaultMensa);
                return true;
            }
        }
        return false;
    }

    private void selectMensa(Mensa selectedMensa) {
        startActivity(MenuActivity.createIntent(this, selectedMensa));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
