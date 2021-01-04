package Activity;

import Adapters.DbAdapter;
import Adapters.ExeAdapter;
import Adapters.Exercise;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidlab2.R;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView list;
    public static boolean opa = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView)findViewById(R.id.listActivities);
        setupUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_settings)
        {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setupUI(){
        Button btnAdd = (Button) findViewById(R.id.addActivityButton);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewExerciseActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume()
    {
        setSettings();
        super.onResume();
        DbAdapter adapter = new DbAdapter(this);
        adapter.open();

        List<Exercise> exercises = adapter.getExes();

        ExeAdapter arrayAdapter = new ExeAdapter(this, R.layout.exe_item, exercises);
        list.setAdapter(arrayAdapter);
        adapter.close();


    }
    void setSettings(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultValueFont = "big";
        String defaultValueTheme = "day";
        String defaultValueLocale = "ru";
        String theme = sharedPref.getString("theme", defaultValueTheme);
        String font = sharedPref.getString("font",defaultValueFont);
        String locale = sharedPref.getString("locale",defaultValueLocale);
        if(theme.equals("day")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(font.equals("big")){
            Configuration configuration = getResources().getConfiguration();
            configuration.fontScale = 1F;
        }
        else{
            Configuration configuration = getResources().getConfiguration();
            configuration.fontScale = 0.5F;
        }
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if(locale.equals("ru")){
            configuration.setLocale(new Locale("ru"));
        }
        else{
            configuration.setLocale(new Locale("en"));
        }
        resources.updateConfiguration(configuration, dm);
        if(opa)
        recreate();
        opa = false;
    }

}