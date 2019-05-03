package com.example.nikmul19.medicine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;

public class MedicineAdapter  extends RecyclerView.Adapter<MedicineAdapter.MyViewHolder>{
    private List <Medicine> medicineList;
    private FirebaseUser cUser;
    private String uId;
    private String quantity_val;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;
    Context context;
    int flag=0;
    public MedicineAdapter(List<Medicine> medicineList, Context context){
        this.medicineList=medicineList;
        this.context=context;
        Log.e("cons","hi"+medicineList.size());

        firebaseAuth = FirebaseAuth.getInstance();
        cUser = firebaseAuth.getCurrentUser();
        uId = cUser.getUid();
        db= FirebaseDatabase.getInstance().getReference().child("users").child("Public").child(uId).child("cart");
        //Log.e("bye",String.valueOf(uId));

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name,price,storeName;
        private EditText qty;
        private Button btn;


        public MyViewHolder(View view){
            super(view);
            name=view.findViewById(R.id.medicine_name);
            price=view.findViewById(R.id.medicine_price);
            qty=view.findViewById(R.id.quantity);
            btn = view.findViewById(R.id.add_to_cart);
            storeName=view.findViewById(R.id.store_name);
            //quantity_val=qty.getText().toString();




        }


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_list_row, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final  Medicine medicine= medicineList.get(position);
        holder.name.setText(medicine.getName());

        holder.qty.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                flag=1;
                quantity_val=s.toString();
                s=null;
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


       // Log.e("error1",medicine.getName()+medicine.getPrice());
        holder.price.setText("Rs:"+medicine.getPrice());
        holder.storeName.setText(medicine.getStoreName());



        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add_to_cart.set(medicine.getName(),Integer.valueOf(medicine.getPrice()),medicine.getId(),medicine.getStoreName(),medicine.getStoreId());
                //Log.e("mid",medicine.getId());
                //Log.e("qq",quantity_val);
                //System.out.println(quantity_val);
                if(flag==0)
                    quantity_val="1";
                db.child(medicine.getId()).child("quantity").setValue(quantity_val);
                db.child(medicine.getId()).child("name").setValue(medicine.getName());
                db.child(medicine.getId()).child("price").setValue(medicine.getPrice());
                db.child(medicine.getId()).child("id").setValue(medicine.getId());
                db.child(medicine.getId()).child("store_name").setValue(medicine.getStoreName());
                db.child(medicine.getId()).child("store_id").setValue(medicine.getStoreId());

                flag=0;

                Toast.makeText(context,medicine.getName()+" is in cart",Toast.LENGTH_SHORT).show();
                //db.setValue(medicine.mid);

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
