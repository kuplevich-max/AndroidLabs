package Fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.example.androidlab2.R;

import java.util.Locale;

import Activity.SettingsActivity;
import Adapters.DbAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Preference clear = findPreference("clear");
        Preference font = findPreference("font");
        Preference theme = findPreference("theme");
        Preference lang = findPreference("lang");
        assert clear != null;
        clear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
               // Toast.makeText(getContext(),"delete",Toast.LENGTH_SHORT).show();
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                prefs.edit().clear().apply();
                DbAdapter adapter = new DbAdapter(getContext());
                adapter.open();
                adapter.clear();
                adapter.close();
                return true;
            }
        });
        assert font != null;
        font.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(newValue.toString().equals("big")) {
                    Configuration configuration = getResources().getConfiguration();
                    configuration.fontScale = 1F;
                   // Toast.makeText(getContext(),"big",Toast.LENGTH_SHORT).show();
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                }
                else{
                    Configuration configuration = getResources().getConfiguration();
                    configuration.fontScale = 0.5F;
                  //  Toast.makeText(getContext(),"else",Toast.LENGTH_SHORT).show();
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                }
                getActivity().recreate();
                return true;
            }

        });

        assert theme != null;
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(newValue.toString().equals("day")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                Toast.makeText(getContext(),newValue.toString(),Toast.LENGTH_SHORT).show();
                getActivity().recreate();
                return true;
            }

        });

        assert lang != null;
        lang.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Resources resources = getResources();
                Configuration configuration = resources.getConfiguration();
                if(newValue.toString().equals("ru")) {
                    configuration.setLocale(new Locale("ru"));
                }
                else{
                    configuration.setLocale(new Locale("eng"));
                }
                Toast.makeText(getContext(),newValue.toString(),Toast.LENGTH_SHORT).show();
                getActivity().recreate();
                return true;
            }

        });

    }
}