package de.kai_morich.simple_bluetooth_le_terminal;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PairActivity extends AppCompatActivity {

    Button buttonPair;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);
        buttonPair=findViewById(R.id.btn_pair);
        buttonPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(PairActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        if (getIntent().getBooleanExtra("LOGOUT", false))
        {
            finish();
        }
    }

}
