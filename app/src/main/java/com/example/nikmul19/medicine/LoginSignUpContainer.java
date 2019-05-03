package com.example.nikmul19.medicine;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.nikmul19.medicine.LoginFragment;
import com.example.nikmul19.medicine.R;

public class LoginSignUpContainer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up_container);
        FragmentManager fragmentManager= getSupportFragmentManager();
        LoginFragment fragment= new LoginFragment();
        fragmentManager.beginTransaction().add(R.id.Constraint_Layout,fragment,null).addToBackStack(null).commit();
    }
}
