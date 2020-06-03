package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setNightModeState(Boolean state) {
        editor.putBoolean("NightMode", state);
        editor.commit();
    }
    public void setLanguage(Boolean state){
        editor.putBoolean("lang", state);
        editor.commit();
    }
    public void setView(int view){
        editor.putInt("view",view);
        editor.commit();
    }

    public Boolean loadNightModeState() {
        Boolean state = prefs.getBoolean("NightMode", false);
        return state;
    }

    public Boolean loadLanguage(){
        Boolean state = prefs.getBoolean("lang", false);
        return state;
    }

    public int loadView(){
        int state = prefs.getInt("view", 0);
        return state;
    }
}
