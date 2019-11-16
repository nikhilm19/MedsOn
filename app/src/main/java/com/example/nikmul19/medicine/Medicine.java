package com.example.nikmul19.medicine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Medicine {
    List<String> names;
    HashMap<String,Object> constituents,schedule;
    String form,manufacturer,medicine_id,name,packageForm;
    int price,units;
    String price1,mid;

    String storeName,storeId;
    Medicine(){names=new ArrayList<>();}
    public void addMedicine(String medicine){
        names.add(medicine);
    }
    public String getName(){
        return name;
    }
    public String getPrice(){
        return price1;
    }
    public String getId(){
        return mid;
    }
    public String getStoreName(){
        return storeName;
    }
    public String getStoreId(){
        return storeId;
    }

    Medicine(String name,String price){
        this.name=name;
        this.price1=price;
    }

    Medicine(String name,String price,String mid,String storeName,String storeId){
        this.name=name;
        this.price1=price;
        this.mid=mid;
       this.storeName=storeName;
       this.storeId=storeId;
    }
    Medicine(String name,String price,String mid){
        this.name=name;
        this.price1=price;
        this.mid=mid;

    }


    public void putData(){

            System.out.println(name);
    }
}
