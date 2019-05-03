package com.example.nikmul19.medicine;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikmul19.medicine.DrawerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    public LoginFragment() {
        // Required empty public constructor
    }

    public View view;

    private FirebaseAuth cAuth;
    private FirebaseUser cUser;
    private DatabaseReference db;
    public EditText email, password, enroll;
    private TextView signUp, errorText;
    private Button signInButton,verifyButton;
    private String uId;
    ProgressBar loginProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        findViews();
        /*if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG);
            toast.show();
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 200);

        }*/
        return view;
    }

    public void showProgress() {

        loginProgress.setVisibility(View.VISIBLE);
        loginProgress.setIndeterminate(true);
        Log.e("progress", "shown");
    }

    public void hideProgress() {
        loginProgress.setVisibility(View.GONE);
        loginProgress.setIndeterminate(false);
        Log.e("progress", "hidden");
    }

    public void findViews() {
        db = FirebaseDatabase.getInstance().getReference();
        errorText = view.findViewById(R.id.errorText);
        signInButton = view.findViewById(R.id.sign_in_button);
        // emailVerifyButton=view.findViewById(R.id.verify_button);
        cAuth = FirebaseAuth.getInstance();
        email = ((EditText) view.findViewById(R.id.email_id_1));
        password = ((EditText) view.findViewById(R.id.password));
        //createButton=view.findViewById(R.id.create_account);
        signUp = view.findViewById(R.id.sign_up);
        signUp.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        loginProgress = view.findViewById(R.id.login_progress);
        verifyButton=view.findViewById(R.id.verify_email_btn);
        verifyButton.setOnClickListener(this);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast toast = Toast.makeText(getActivity(), "Permission granted. Thanks", Toast.LENGTH_LONG);
                toast.show();


            }
        }


    }



    public void signIn(final String email, final String password) {





        cUser = cAuth.getCurrentUser();
        boolean error = false;
        errorText.setVisibility(View.INVISIBLE);
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Enter email address!", Toast.LENGTH_SHORT).show();
            error = true;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getActivity(), "Enter valid email address!", Toast.LENGTH_SHORT).show();
            error = true;
        }

       else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Enter password!", Toast.LENGTH_SHORT).show();
            error = true;
        }


       else if (password.length() < 6) {
            Toast.makeText(getActivity(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            error = true;
        }
        Log.e("error", email + " " + password);
        if(error)hideProgress();
        else{


            if (cUser != null && !cUser.isEmailVerified()) {
                Toast toast = Toast.makeText(getActivity(), "PLEASE VERIFY EMAIL", Toast.LENGTH_LONG);
                toast.show();
                cUser.reload();
                hideProgress();
            } else {
                cAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final String error;
                        if (task.isSuccessful()) {
                            if (cAuth.getCurrentUser().isEmailVerified()){

                            errorText.setVisibility(View.GONE);
                            hideProgress();
                            MainIntent(email);
                            System.out.print("succesful");}
                            else{
                                cAuth.getCurrentUser().reload();
                                Log.i("test","not verified");
                                errorText.setVisibility(View.VISIBLE);
                                errorText.setText("Please verify email");
                                hideProgress();
                            }
                        } else {

                            hideProgress();

                            try {
                                errorText.setVisibility(View.VISIBLE);
                                throw task.getException();

                            } catch (FirebaseAuthWeakPasswordException e) {

                                errorText.setText("Weak Password");
                                LoginFragment.this.password.requestFocus();
                                Log.e("errror", e.getMessage());

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                LoginFragment.this.password.requestFocus();
                                errorText.setText("Invalid Credentials");
                                Log.e("errror", e.getMessage());

                            } catch (FirebaseAuthUserCollisionException e) {
                                LoginFragment.this.password.requestFocus();
                                errorText.setText("User Already Exists");
                                Log.e("errror", e.getMessage());
                            } catch (Exception e) {
                                errorText.setText(e.getMessage());
                                Log.e("errror", e.getMessage());
                            }
                            Log.e("eror", "onComplete: Failed=" + task.getException().getMessage());
                            System.out.print("UNsuccesful");


                        }

                    }
                });
            }
        }


    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {


            case R.id.sign_in_button:


                //Log.i("test",email+" "+password);
                signInButton.setEnabled(false);
                showProgress();


                signIn(email.getText().toString(), password.getText().toString());
                signInButton.setEnabled(true);

                break;


            case R.id.sign_up:
                System.out.println("clicked");
                Log.d("test", "clicked");
                UserSignUpFragment fragment = new UserSignUpFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.Constraint_Layout, fragment, null).addToBackStack(null).commit();
                break;
            case R.id.verify_email_btn:


        }

    }

    public void MainIntent(String email) {
        Log.i("test","called mainintent");
        Bundle bundle = new Bundle();
        Intent intent = new Intent(getActivity(), DrawerActivity.class);
        bundle.putString("user_id", email);
        intent.putExtras(bundle);
        hideProgress();
        getActivity().finish();
        startActivity(intent);
    }

    public void AdminIntent(String email) {

        //todo admin intent
        Bundle bundle = new Bundle();
        Intent intent = new Intent(getActivity(), DrawerActivity.class);
        bundle.putString("user_id", email);
        intent.putExtras(bundle);
        getActivity().finish();
        startActivity(intent);

    }
}



