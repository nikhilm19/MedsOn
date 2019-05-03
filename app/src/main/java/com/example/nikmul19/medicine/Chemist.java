package com.example.nikmul19.medicine;

import java.util.HashMap;

public class Chemist {
    String formatted_address,id,name,place_id;

    HashMap<String,Object> geometry,plus_Code;

    public HashMap<String, Object> getGeometry() {
        return geometry;
    }

    public HashMap<String, Object> getPlus_Code() {
        return plus_Code;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
