package de.kai_morich.simple_bluetooth_le_terminal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.kai_morich.simple_bluetooth_le_terminal.BLE.DevicesFragment;
import de.kai_morich.simple_bluetooth_le_terminal.BLE.PairActivity;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this::onBackStackChanged);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();
//            Intent intent=new Intent(MainActivity.this,LogInActivity.class);
//            startActivity(intent);
        }else
            onBackStackChanged();
        if (getIntent().getBooleanExtra("LOGOUT", false))
        {
            Intent intent = new Intent(getApplicationContext(), PairActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("LOGOUT", true);
            startActivity(intent);         }
    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount()>0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
