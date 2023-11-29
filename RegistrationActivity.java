package com.example.moneyminder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity{

    private EditText mEmail;
    private EditText mPassword;
    private Button buttonSignIn;
    private TextView mSignIn;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registration);

            mAuth=FirebaseAuth.getInstance();
            mDialog= new ProgressDialog(this);

            signIn();

        }

        private void signIn() {
            mEmail = findViewById(R.id.email_signup);
            mPassword = findViewById(R.id.password_signup);
            buttonSignIn = findViewById(R.id.button_signup);
            mSignIn = findViewById(R.id.have_account);

            buttonSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        mEmail.setError("Email Required");
                    }
                    if (TextUtils.isEmpty(password)) {
                        mPassword.setError("Password Required");
                    }
                    if (password.length() < 6 ) {
                        mPassword.setError("Password should be at least 6 characters");
                    }

                    mDialog.setMessage("Processing");
                    mDialog.show();

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Sign up complete", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }else {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Sign up failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            mSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            });
        }
}