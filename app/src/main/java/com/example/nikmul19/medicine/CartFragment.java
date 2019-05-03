package com.example.nikmul19.medicine;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private static DatabaseReference db;
    private FirebaseAuth firebaseAuth;
    private RecyclerView.LayoutManager layoutManager;
    private CartAdapter adapter;
    private FirebaseUser cUser;
    static int total;
    private String uId;
    private static TextView amount;
    private List<Medicine> MedicinesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_cart_recycler, container, false);
        amount = view.findViewById(R.id.total_amount);
        recyclerView = view.findViewById(R.id.cart_recycler);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        cUser = firebaseAuth.getCurrentUser();
        uId = cUser.getUid();
        db = FirebaseDatabase.getInstance().getReference().child("users").child("Public").child(uId).child("cart");
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this.getActivity());
        // layoutManager.canScrollVertically();
        // layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new CartAdapter(MedicinesList);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        int dividerThickness = 5;
        shapeDrawable.setIntrinsicHeight(dividerThickness);
        shapeDrawable.setAlpha(0);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.border1));
        add_to_cart.refresh();
        db.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total=0;
                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {

                    String name = childSnap.child("name").getValue(String.class);
                    String price = childSnap.child("price").getValue(String.class);
                    String mid = childSnap.child("id").getValue(String.class);
                    String quantity_val = childSnap.child("quantity").getValue(String.class);

                    String storeName= childSnap.child("store_name").getValue(String.class);
                    String storeId =childSnap.child("store_id").getValue(String.class);
                    Log.e("nname", name);
                    add_to_cart.set(name, Integer.parseInt(price), mid,storeName,storeId);
                    total += Integer.parseInt(quantity_val) * Integer.parseInt(price);

                    //Log.e("ssize2", String.valueOf(add_to_cart.medicineArrayList.size()));

                    //medicineList.add()


                }
                for (int i = 0; i < add_to_cart.medicineArrayList.size(); i++) {
                    Log.e("addadd", add_to_cart.get(i).getName());
                    MedicinesList.add(add_to_cart.get(i));

                }
                adapter.notifyDataSetChanged();

                amount.setText("Rs. " + String.valueOf(total));
                //medicine.putData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //Log.e("ssize1", String.valueOf(n));

        /*TextView textView = (TextView) findViewById(R.id.textView5);
        textView.setText(name);*/

        //TODO get students as per selected criteria

        return view;
    }


    public static void setTextPrice() {
//        int p1=amount.getText();
        total=0;
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total=0;
                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {

                    //String name= childSnap.child("name").getValue(String.class);
                    String price = childSnap.child("price").getValue(String.class);
                    //String mid=childSnap.child("id").getValue(String.class);
                    String quantity_val = childSnap.child("quantity").getValue(String.class);
                    //Log.e("nname",name);
                    //add_to_cart.set(name,Integer.parseInt(price),mid);
                    total += Integer.parseInt(quantity_val) * Integer.parseInt(price);

                    //Log.e("ssize2", String.valueOf(add_to_cart.medicineArrayList.size()));

                    //medicineList.add()


                }

                amount.setText("Rs. " + String.valueOf(total));
                //medicine.putData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //amount.setText(price);
    }
}