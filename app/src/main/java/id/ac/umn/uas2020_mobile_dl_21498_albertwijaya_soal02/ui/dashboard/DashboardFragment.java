package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.HomeActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.R;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.AboutActivity;
import id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02.Session;

public class DashboardFragment extends Fragment {
    private Switch swDarkMode;
    Session sharedpref;
    private Spinner spView, spLanguage;
    private RelativeLayout rlAbout;
    private TextView tvDarkMode, tvViewMode, tvLanguage, tvAbout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);


        spView = root.findViewById(R.id.spViewMode);
        spLanguage = root.findViewById(R.id.spLanguage);
        rlAbout = root.findViewById(R.id.rlAbout);
        tvDarkMode = root.findViewById(R.id.tvDarkMode);
        tvViewMode = root.findViewById(R.id.tvViewMode);
        tvAbout = root.findViewById(R.id.tvAbout);
        tvLanguage = root.findViewById(R.id.tvLanguage);
        swDarkMode = root.findViewById(R.id.swDarkMode);

        sharedpref = new Session(getContext());
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
}