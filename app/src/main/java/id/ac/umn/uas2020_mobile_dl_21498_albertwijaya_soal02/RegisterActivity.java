package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;

    private EditText etName, etEmail, etPassword1, etPassword2;
    private Button btnRegister;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    Session sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }else{
            setTheme(R.style.AppTheme);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressDialog = new ProgressDialog(RegisterActivity.this);

        sharedpref = new Session(this);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword1 = findViewById(R.id.etPassword1);
        etPassword2 = findViewById(R.id.etPassword2);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        if(sharedpref.loadNightModeState()){
        }else {
        }
    }

    private boolean isNameValid(String name){
        String regex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isPasswordValid(String password){
        String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void registerUser(){

        progressDialog.setMessage("Signing up");
        progressDialog.show();

        boolean valid = true;

        final String name = etName.getText().toString();
        if(name.isEmpty()){
            etName.setError("Name cannot be empty");
            valid = false;
        }else if (!isNameValid(name)){
            etName.setError("Name cannot be numbers");
            valid=false;
        }

        final String email = etEmail.getText().toString();
        if (email.isEmpty()){
            etEmail.setError("Email cannot be empty");
            valid=false;
        }else if (!isEmailValid(email)){
            etEmail.setError("Email is invalid");
            valid=false;
        }

        final String password1 = etPassword1.getText().toString();
        if (password1.isEmpty()){
            etPassword1.setError("Password cannot be empty");
            valid=false;
        }else if(!isPasswordValid(password1)) {
            etPassword1.setError("Password must have at least 8 characters, one uppercase letter, one lowercase letter, one number, and one special character");
            valid = false;
        }

        final String password2 = etPassword2.getText().toString();
        if (password2.isEmpty()){
            etPassword2.setError("Password cannot be empty");
            valid=false;
        } else if(!password2.equals(password1)){
            etPassword1.setError("Password doesn't match");
            etPassword2.setError("Password doesn't match");
            valid=false;
        }

        if(valid){
            final String password = password1;
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    // Sign in success, update UI with the signed-in user's information
                                                    User newUser = new User(name, "", "", "", "", "", 0, email, "", "");
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    String keyId = user.getUid();
                                                    mDatabase.child(keyId).setValue(newUser);
                                                    progressDialog.dismiss();
                                                    FirebaseAuth.getInstance().signOut();

                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    intent.putExtra("verifyEmail", "verifyEmail");
                                                    startActivity(intent);
                                                } else {
                                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Sign up failed", Snackbar.LENGTH_SHORT);
                                                    View sbView = snackbar.getView();
                                                    sbView.setBackgroundColor(Color.parseColor("#FDA89F"));
                                                    snackbar.show();

                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException existEmail){
                                    etEmail.setError("Email has already Exist");
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Email has already exist", Snackbar.LENGTH_SHORT);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(Color.parseColor("#FDA89F"));
                                    snackbar.show();
                                } catch (Exception e)
                                {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Sign up failed", Snackbar.LENGTH_SHORT);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(Color.parseColor("#FDA89F"));
                                    snackbar.show();
                                } finally {
                                    progressDialog.dismiss();
                                }
                            }
                        }
                    });
        }
        else{
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please check your credentials", Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.parseColor("#FDA89F"));
            snackbar.show();
            progressDialog.dismiss();
        }

    }
}
