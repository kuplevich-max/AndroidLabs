package Fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.example.androidlab2.R;

import java.util.Locale;

import Activity.MainActivity;
import Activity.SettingsActivity;
import Adapters.DbAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences preferences ;
    SharedPreferences.Editor edit;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        edit =  preferences.edit();
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
                    edit.putString("font", "big");
                    edit.apply();
                   // Toast.makeText(getContext(),"big",Toast.LENGTH_SHORT).show();
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                }
                else{
                    Configuration configuration = getResources().getConfiguration();
                    configuration.fontScale = 0.5F;
                    edit.putString("font", "tiny");
                    edit.apply();
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
                    edit.putString("theme", "day");
                    edit.apply();
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    edit.putString("theme", "night");
                    edit.apply();
                }
                getActivity().recreate();
                return true;
            }

        });

        assert lang != null;
        lang.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Configuration configuration = getResources().getConfiguration();
                if(newValue.toString().equals("ru")) {
                    configuration.setLocale(new Locale("ru"));
                    edit.putString("locale", "ru");
                    edit.apply();
                }
                else{
                    configuration.setLocale(new Locale("en"));
                    edit.putString("locale", "en");
                    edit.apply();
                }
                MainActivity.opa = true;
                getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
                getActivity().recreate();
                return true;
            }

        });

    }
}