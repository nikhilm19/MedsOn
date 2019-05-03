package com.example.nikmul19.medicine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.nikmul19.medicine.LoginSignUpContainer;
import com.example.nikmul19.medicine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Intent intent;
        Bundle bundle;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            bundle = new Bundle();
            Log.i("test","base verified");
            bundle.putString("user_id", firebaseUser.getDisplayName());
            intent = new Intent(this, DrawerActivity.class);
            intent.putExtras(bundle);
        } else {
            Log.i("test","not base verified");

            intent = new Intent(this, LoginSignUpContainer.class);

        }
        startActivity(intent);
        this.finish();

    }
}

