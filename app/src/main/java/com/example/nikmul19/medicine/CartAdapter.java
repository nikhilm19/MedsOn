package com.example.nikmul19.medicine;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;
import android.view.ViewGroup;

import com.example.nikmul19.medicine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

public class CartAdapter  extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{
    private List <Medicine> medicineList;
    int quantity_count=1;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;

    private FirebaseUser cUser;
    private String uId;
    public CartAdapter(final List<Medicine> medicineList1){
        this.medicineList=medicineList1;
        //Log.e("cons","hi"+medicineList.size());
        firebaseAuth = FirebaseAuth.getInstance();
        cUser = firebaseAuth.getCurrentUser();
        uId = cUser.getUid();
        db= FirebaseDatabase.getInstance().getReference().child("users").child("Public").child(uId).child("cart");

        //Retrieve from firebase cart data


        //Log.e("name",medicineList.get(0).getName());


    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name,price,total_quantity,quantity,store;
        private Button decrease,increase,delete;
        public int quantity_count=0;


        public MyViewHolder(View view){
            super(view);
            name=view.findViewById(R.id.medicine_name);
            price=view.findViewById(R.id.medicine_price);
            total_quantity=view.findViewById(R.id.Total_Quantity);
            decrease = view.findViewById(R.id.decrease);
            increase=view.findViewById(R.id.increase);
            delete=view.findViewById(R.id.delete);
            quantity=view.findViewById(R.id.quantity);
            store= view.findViewById(R.id.avlbl_store);



            /*delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    medicineList.remove(getAdapterPosition());
                }
            });*/







        }


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cart, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final  Medicine medicine= medicineList.get(position);
        holder.name.setText(medicine.getName());
        holder.price.setText("Rs:"+medicine.getPrice());
        holder.store.setText("From: "+medicine.getStoreName());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quantity_count=Integer.parseInt(dataSnapshot.child(medicine.getId()).child("quantity").getValue(String.class));
                holder.quantity.setText(String.valueOf(quantity_count));
                holder.total_quantity.setText(String.valueOf(Integer.parseInt(medicine.getPrice())*quantity_count));
                //medicine.putData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        //holder.quantity.setText(String.valueOf(quantity_count));
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        quantity_count=Integer.parseInt(dataSnapshot.child(medicine.getId()).child("quantity").getValue(String.class));
                        quantity_count+=1;
                        holder.quantity.setText(String.valueOf(quantity_count));
                        db.child(medicine.getId()).child("quantity").setValue(String.valueOf(quantity_count));
                        holder.total_quantity.setText(String.valueOf(Integer.parseInt(medicine.getPrice())*quantity_count));
                        CartFragment.setTextPrice();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        quantity_count=Integer.parseInt(dataSnapshot.child(medicine.getId()).child("quantity").getValue(String.class));
                        quantity_count-=1;
                        if(quantity_count<0)
                            quantity_count=0;
                        holder.quantity.setText(String.valueOf(quantity_count));
                        db.child(medicine.getId()).child("quantity").setValue(String.valueOf(quantity_count));
                        holder.total_quantity.setText(String.valueOf(Integer.parseInt(medicine.getPrice())*quantity_count));
                        CartFragment.setTextPrice();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


               // holder.quantity.setText(String.valueOf(quantity_count));
                //holder.total_quantity.setText(String.valueOf(Integer.parseInt(medicine.getPrice())*quantity_count));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_to_cart.remove(position);
                db.child(medicine.getId()).removeValue();
                medicineList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,add_to_cart.medicineArrayList.size());
                CartFragment.setTextPrice();
                //Log.e("pos",String.valueOf(position));
            }
        });




//        holder.qty.setText("1");


    }
    @Override
    public int getItemCount(){


        Log.e("error","item-count:"+medicineList.size());
        return medicineList.size();
    }

}
