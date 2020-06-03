package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.ui.dashboard;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.HomeActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.MainActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.R;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.AboutActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.Session;

public class DashboardFragment extends Fragment {
    private Switch swDarkMode;
    Session sharedpref;
    private Spinner spView, spLang;
    private RelativeLayout rlAbout;
    private TextView tvDarkMode, tvViewMode, tvLanguage, tvAbout;
    private Boolean initSpinner = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);


        spView = root.findViewById(R.id.spViewMode);
        spLang = root.findViewById(R.id.spLanguage);
        rlAbout = root.findViewById(R.id.rlAbout);
        tvDarkMode = root.findViewById(R.id.tvDarkMode);
        tvViewMode = root.findViewById(R.id.tvViewMode);
        tvAbout = root.findViewById(R.id.tvAbout);
        tvLanguage = root.findViewById(R.id.tvLanguage);
        swDarkMode = root.findViewById(R.id.swDarkMode);

        sharedpref = new Session(getContext());

        if(sharedpref.loadLanguage()){
            spLang.setSelection(1);
        }else {
            spLang.setSelection(0);
        }
        spLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(initSpinner){
                    initSpinner = false;
                }else{
                    if(position == 0){
                        sharedpref.setLanguage(false);
                        setLocale("en");
                    }else{
                        sharedpref.setLanguage(true);
                        setLocale("id");
                    }
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }
        });
        if(sharedpref.loadNightModeState()) {
            swDarkMode.setChecked(true);
            getContext().setTheme(R.style.DarkTheme);
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            setTheme(Color.WHITE);
        }else{
            getContext().setTheme(R.style.AppTheme);
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            setTheme(Color.GRAY);
        }
        swDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedpref.setNightModeState(true);
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    sharedpref.setNightModeState(false);
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }
            }
        });

        rlAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void setTheme(int color){
        tvDarkMode.setTextColor(color);
        tvViewMode.setTextColor(color);
        tvLanguage.setTextColor(color);
        tvAbout.setTextColor(color);
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