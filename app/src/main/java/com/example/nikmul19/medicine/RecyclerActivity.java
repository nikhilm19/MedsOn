package com.example.nikmul19.medicine;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference db;
    private FirebaseAuth firebaseAuth;
    private List<Medicine> MedicinesList = new ArrayList<>();


    private RecyclerView.LayoutManager layoutManager;
    private MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        recyclerView = findViewById(R.id.recycler1);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        recyclerView.setHasFixedSize(false);
        adapter = new MedicineAdapter(MedicinesList, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        ArrayList<String> name = getIntent().getStringArrayListExtra("name");
        ArrayList<Integer> price = getIntent().getIntegerArrayListExtra("price");
        /*TextView textView = (TextView) findViewById(R.id.textView5);
        textView.setText(name);*/
        Log.e("name", name.get(0));
        if (name.size() != 0 && price.size() != 0) {
            for (int i = 0; i < name.size(); i++) {
                Medicine medicine = new Medicine(name.get(i), String.valueOf(price.get(i)));
                MedicinesList.add(medicine);
            }

        } else {
            addMedicines();
        }


    }

    public void addMedicines() {


        //TODO get students as per selected criteria
        db = FirebaseDatabase.getInstance().getReference().child("medicines");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {

                    String name = childSnap.child("name").getValue(String.class);
                    int price = childSnap.child("price").getValue(Integer.class);

                    Medicine medicine1 = new Medicine(name, "Rs:" + String.valueOf(price));

                    Log.e("error", "hi" + price);
                    System.out.print(price);
                    MedicinesList.add(medicine1);
                    System.out.print("hi" + MedicinesList.size());


                    // Log.i("hichilds",name+price);
                }
                //medicine.putData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Log.e("error", "hey" + MedicinesList.size());


        adapter.notifyDataSetChanged();
        setProgressBarIndeterminateVisibility(false);


    }
}
