package com.example.simplechat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");

        Fragment fragment = new settingfragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (savedInstanceState == null)
        {
            fragmentTransaction.add(R.id.settings_prefs, fragment, "settings_prefs");
            fragmentTransaction.commit();
        }
        else {
            fragment = getFragmentManager().findFragmentByTag("settings_prefs");
        }
    }

    public static class settingfragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.setting_screen);
        }
    }

}

