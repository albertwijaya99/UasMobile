package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    Session sharedpref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }else{
            setTheme(R.style.AppTheme);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}
