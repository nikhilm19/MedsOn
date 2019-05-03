package com.example.nikmul19.medicine;

import android.util.Log;

import java.util.ArrayList;

public class add_to_cart {
    static ArrayList<Medicine> medicineArrayList = new ArrayList<>();

    public static void set(String name,int price,String mid)
    {

        Medicine med = new Medicine(name,String.valueOf(price),mid);
        medicineArrayList.add(med);
        Log.e("cartsize",String.valueOf(medicineArrayList.size()));

    }
    public static void set(String name,int price,String mid,String storeName, String storeId)
    {

        Medicine med = new Medicine(name,String.valueOf(price),mid,storeName,storeId);
        medicineArrayList.add(med);
        Log.e("cartsize",String.valueOf(medicineArrayList.size()));

    }
    public static Medicine get(int index1)
    {
        Medicine med=medicineArrayList.get(index1);
        return med;
    }
    public static void remove(int index1)
    {
        Medicine med=medicineArrayList.remove(index1);


    }
    public static void refresh()
    {
        int n=medicineArrayList.size();
        for(int i=n-1;i>=0;i--)
        {
            medicineArrayList.remove(i);
        }
    }
    public static void remove(String key)
    {
        for(int i=0;i<medicineArrayList.size();i++)
        {
            Medicine medicine=medicineArrayList.get(i);
            if(medicine.getId().equals(key))
            {
                remove(i);
            }
        }
    }
}
