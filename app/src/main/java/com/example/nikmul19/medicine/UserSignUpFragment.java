package com.example.nikmul19.medicine;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikmul19.medicine.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserSignUpFragment extends Fragment implements View.OnClickListener {


    public UserSignUpFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth cAuth;
    private FirebaseUser cUser;
    private DatabaseReference db;
    public EditText email, password, age, name, phoneNo;

    private TextView status, isAdmin, errorText;
    private ProgressBar signUpProgress;
    private Button createButton, signInButton;
    private String uId, sYear;
    private View view;
    private RadioGroup year;
    private CheckBox shift;

    public void showProgress() {

        signUpProgress.setVisibility(View.VISIBLE);
        signUpProgress.setIndeterminate(true);
        Log.e("progress", "shown");
    }

    public void hideProgress() {
        signUpProgress.setVisibility(View.GONE);
        signUpProgress.setIndeterminate(false);
        Log.e("progress", "hidden");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_sign_up, container, false);
        findViews();
        /*if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG);
            toast.show();
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 200);

        }*/
        return view;
    }

    public void findViews() {

        db = FirebaseDatabase.getInstance().getReference();
        isAdmin = view.findViewById(R.id.if_admin_account);
        isAdmin.setOnClickListener(this);
        cAuth = FirebaseAuth.getInstance();
        email = ((EditText) view.findViewById(R.id.email_id_1));
        age = view.findViewById(R.id.age);
        name = view.findViewById(R.id.user_name);
        password = ((EditText) view.findViewById(R.id.password_1));
        createButton = view.findViewById(R.id.create_account);
        signInButton = view.findViewById(R.id.Sign_in);
        signInButton.setOnClickListener(this);
        phoneNo = view.findViewById(R.id.phone_no);
        signUpProgress = view.findViewById(R.id.sign_up_progress);
        errorText=view.findViewById(R.id.sign_up_error);

        signUpProgress.setVisibility(View.INVISIBLE);
        createButton.setOnClickListener(this);


    }


    public void createAccount() {
        final String email=this.email.getText().toString();
        final int age= Integer.parseInt(this.age.getText().toString());
        final String name = this.name.getText().toString();
        final String phone = this.phoneNo.getText().toString();
        final String password=this.password.getText().toString();
        errorText.setVisibility(View.INVISIBLE);
        boolean error=false;

        Log.i("test-vals",email+ " 1 "+ name);

        if (TextUtils.isEmpty(email)) {

            Log.i("test","emopty-email");
            Toast.makeText(getActivity(), "Enter email address!", Toast.LENGTH_SHORT).show();
            error=true;
        }

        if(TextUtils.isEmpty(this.age.getText().toString())){
            Toast.makeText(getActivity(), "Enter age!", Toast.LENGTH_SHORT).show();
            error=true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getActivity(), "Enter valid email address!", Toast.LENGTH_SHORT).show();
            error=true;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Enter password!", Toast.LENGTH_SHORT).show();
            error=true;
        }


        if (password.length() < 6) {
            Toast.makeText(getActivity(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            error=true;
        }
        if (!Patterns.PHONE.matcher(phone).matches()){
            Toast.makeText(getActivity(), "Enter valid Phone number!", Toast.LENGTH_SHORT).show();
            error=true;
        }
        if (error)hideProgress();

        if(!error) {


            cAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    View view1 = view.findViewById(R.id.Constraint_layout);
                    if (task.isSuccessful()) {
                        System.out.print("created");
                        Snackbar snackbar = Snackbar.make(view, "Successful Sign in", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        cUser = cAuth.getCurrentUser();
                        UserProfileChangeRequest profileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        cUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i("test", "Set display name successfully");
                                }
                            }
                        });
                        createButton.setVisibility(View.GONE);
                        signInButton.setVisibility(View.VISIBLE);
                        uId = cUser.getUid();

                        final UserSignUpData user = new UserSignUpData(email, name, phone, age);
                        writeNewUser(uId,user);
                        verifyEmail();
                    } else {

                        try {
                            errorText.setVisibility(View.VISIBLE);
                            throw task.getException();

                        } catch (FirebaseAuthWeakPasswordException e) {

                            errorText.setText("Weak Password");
                            Log.e("errror", e.getMessage());

                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            errorText.setText("Invalid Credentials");
                            Log.e("errror", e.getMessage());

                        } catch (FirebaseAuthUserCollisionException e) {
                            errorText.setText("User Already Exists");
                            Log.e("errror", e.getMessage());
                        } catch (Exception e) {
                            Log.e("errror", e.getMessage());
                        }
                        Log.e("eror", "onComplete: Failed=" + task.getException().getMessage());
                        System.out.print("UNsuccesful");
                    }


                }

            });
        }

    }

    public void writeNewUser(String UserId, UserSignUpData user) {

        System.out.print("created new user");
        db.child("/users/Public/" + UserId).setValue(user);
    }

    public void signIn(final String email, final String password) {

        final int age = Integer.parseInt(this.age.getText().toString());
        final String name = this.name.getText().toString();
        final String phone = this.phoneNo.getText().toString();
        cUser = cAuth.getCurrentUser();

        if (cUser != null && !cUser.isEmailVerified()) {
            Toast toast = Toast.makeText(getActivity(), "cant sign in. please verify email" + cUser.getEmail()
                    , Toast.LENGTH_LONG);
            toast.show();
            cUser.reload();
        } else {
            cAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                View view1 = view.findViewById(R.id.Constraint_layout);

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        signInButton.setEnabled(false);

                        //status.setText("Signed in");
                        Log.i("test", "signed in");

                        MainIntent(email);

                    } else {
                        //status.setText("Signed out");
                        Log.i("test", "not signed in");
                    }
                }
            });
            //hideProgressBar();
        }
    }

    public void verifyEmail() {

        cUser.sendEmailVerification().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override

            public void onComplete(@NonNull Task<Void> task) {
                View view1 = view.findViewById(R.id.Constraint_layout);
                if (task.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(view1, "email sent", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    hideProgress();
                } else {
                    Snackbar snackbar = Snackbar.make(view1, "No mail sent", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    hideProgress();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.create_account:
                showProgress();
                Log.i("test", email.getText().toString() + "1" + password.getText().toString()+"1");

                if(!age.getText().toString().equals("")) {
                    createAccount();
                }
                else{
                    hideProgress();
                    Toast.makeText(getActivity(),"Please enter details",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.Sign_in:
                signIn(email.getText().toString(), password.getText().toString());

                break;
        }

    }


    public void MainIntent(String email) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(getActivity(), DrawerActivity.class);
        bundle.putString("user_id", email);
        intent.putExtras(bundle);
        getActivity().finish();

        startActivity(intent);

    }

}


class UserSignUpData {

    String email_id, name, phone;
    int age;

    UserSignUpData(String email_id, String name, String phone, int age) {
        this.email_id = email_id;
        this.name = name;
        this.age = age;
        this.phone = phone;

    }


}


