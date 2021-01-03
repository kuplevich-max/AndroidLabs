package Activity;

import Adapters.DbAdapter;
import Adapters.ExeAdapter;
import Adapters.Exercise;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.androidlab2.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView list;

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
        super.onResume();
        DbAdapter adapter = new DbAdapter(this);
        adapter.open();

        List<Exercise> exercises = adapter.getExes();

        ExeAdapter arrayAdapter = new ExeAdapter(this, R.layout.exe_item, exercises);
        list.setAdapter(arrayAdapter);
        adapter.close();

    }
}