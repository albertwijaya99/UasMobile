package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.load.engine.Resource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;

    private EditText etEmail, etPassword;
    private TextView tvRegister;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private User user;
    private Switch swDarkMode;
    Session sharedpref;
    private Spinner spLang;
    private Boolean initSpinner = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }else{
            setTheme(R.style.AppTheme);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }

        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);

        sharedpref = new Session(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        spLang = findViewById(R.id.spLanguage);
        spLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(initSpinner){
                    initSpinner = false;
                }else{

                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    if(position==0){
                        sharedpref.setLanguage(false);
                    }else{
                        sharedpref.setLanguage(true);
                    }
                    res.updateConfiguration(conf,dm);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }
        });

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        Intent intent = getIntent();
        String verifyEmail = intent.getStringExtra("verifyEmail");
        if(verifyEmail != null){
            if(verifyEmail.contains("verifyEmail")){
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please verify your email address", Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(Color.parseColor("#FDA89F"));
                snackbar.show();
            }
        }
        swDarkMode = findViewById(R.id.swDarkMode);
        if (sharedpref.loadNightModeState()) {
            swDarkMode.setChecked(true);
        }
        swDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedpref.setNightModeState(true);
                    recreate();
                }
                else {
                    sharedpref.setNightModeState(false);
                    recreate();
                }
            }
        });

        tvRegister = findViewById(R.id.tvRegister);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void loginUser(){
        boolean valid=true;

        String email = etEmail.getText().toString();
        if (email.isEmpty()){
            etEmail.setError("Email cannot be empty!");
            valid=false;
        }else if (!isEmailValid(email)){
            etEmail.setError("Email is invalid");
            valid=false;
        }

        String password = etPassword.getText().toString();
        if (password.isEmpty()){
            etPassword.setError("Password cannot be empty");
            valid=false;
        }

        if(valid){

            progressDialog.setMessage("Signing in");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                                    if(mUser != null) {
                                        // Get a reference to our posts
                                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference ref = database.getReference("users");
                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                } else{
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please verify your email address", Snackbar.LENGTH_SHORT);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(Color.parseColor("#FDA89F"));
                                    snackbar.show();
                                }
                            }
                            else {
                                if(isOnline()){
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Wrong email or password", Snackbar.LENGTH_SHORT);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(Color.parseColor("#FDA89F"));
                                    snackbar.show();
                                }
                                else{
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Check your internet connection", Snackbar.LENGTH_SHORT);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(Color.parseColor("#FDA89F"));
                                    snackbar.show();
                                }
                            }
                            progressDialog.dismiss();

                        }
                    });
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
