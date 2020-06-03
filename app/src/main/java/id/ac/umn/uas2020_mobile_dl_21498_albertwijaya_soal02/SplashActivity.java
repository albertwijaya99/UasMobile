package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    private User user;
    Session sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedpref = new Session(this);
        if(sharedpref.loadLanguage()){
            setLocale("id");
        }else {
            setLocale("en");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mUser != null) {
            // Get a reference to our posts
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            },2000);

        } else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }
            },2000);
        }
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
