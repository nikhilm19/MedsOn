package com.example.nikmul19.medicine;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecylerFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference db;
    private FirebaseAuth firebaseAuth;
    private RecyclerView.LayoutManager layoutManager;
    private MedicineAdapter adapter;
    private FirebaseUser cUser;
    private String uId;
    //uId = cUser.getUid();

    private List<Medicine>MedicinesList= new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recyler, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        firebaseAuth = FirebaseAuth.getInstance();
        cUser = firebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("medicines");
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this.getActivity());
       // layoutManager.canScrollVertically();
       // layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //Context context;
        adapter = new MedicineAdapter(MedicinesList, getContext());
        uId = cUser.getUid();
        db.child("/users/Public/" + uId+"/cart");
        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        ShapeDrawable shapeDrawable= new ShapeDrawable(new RectShape());
        int dividerThickness=5;
        shapeDrawable.setIntrinsicHeight(dividerThickness);
        shapeDrawable.setAlpha(0);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.border1));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        try{
        ArrayList<String> name= getArguments().getStringArrayList("name");
        ArrayList<Integer> price= getArguments().getIntegerArrayList("price");
        ArrayList<String> mid= getArguments().getStringArrayList("mid");
        ArrayList<String> stores= getArguments().getStringArrayList("stores");
            ArrayList<String> storeIds= getArguments().getStringArrayList("store-ids");
            Log.i("store-ids",storeIds.toString());



            //Log.e("name",name.get(0));

                for (int i = 0; i < name.size(); i++) {
                    Medicine medicine = new Medicine(name.get(i), String.valueOf(price.get(i)),mid.get(i),stores.get(i),storeIds.get(i));
                    MedicinesList.add(medicine);
                    adapter.notifyDataSetChanged();
                    //db.setValue(mid.get(i))

                }


        }
        catch (Exception e)
        {

            addMedicines();
        }
        finally {

        }
        /*TextView textView = (TextView) findViewById(R.id.textView5);
        textView.setText(name);*/



        return view;
    }


    public void showAvailData(final ArrayList<String> names , final ArrayList<String> mid , final ArrayList<Integer> prices){

        for (int i = 0; i < names.size(); i++) {
            Medicine medicine = new Medicine(names.get(i), String.valueOf(prices.get(i)), mid.get(i));
            MedicinesList.add(medicine);
            adapter.notifyDataSetChanged();
        }
    }








    public void addMedicines(){

        Medicine medicine;
        db= FirebaseDatabase.getInstance().getReference().child("medicines");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnap:dataSnapshot.getChildren()){

                    String name= childSnap.child("name").getValue(String.class);
                    int price= childSnap.child("price").getValue(Integer.class);
                    String mid=childSnap.child("medicine_id").getValue(String.class);

                    Medicine medicine1 = new Medicine(name, String.valueOf(price),mid);

                   // Log.e("error","hi"+price);
                    //System.out.print(price);
                    MedicinesList.add(medicine1);
                    adapter.notifyDataSetChanged();
                   //System.out.print("hi"+MedicinesList.size());
                   // Log.e("error","Size"+MedicinesList.size());




                    // Log.i("hichilds",name+price);
                }
                //medicine.putData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
