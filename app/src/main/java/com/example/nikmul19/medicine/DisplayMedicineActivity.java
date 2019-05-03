package com.example.nikmul19.medicine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DisplayMedicineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_medicine);

        Bundle bundle=getIntent().getExtras();
        RecylerFragment fragment= new RecylerFragment();
        getSupportFragmentManager().beginTransaction().
                add(R.id.meds_container,fragment,null).commit();
        fragment.setArguments(bundle);
    }
}
