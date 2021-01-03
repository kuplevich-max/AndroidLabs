package Activity;

import Adapters.DbAdapter;
import Adapters.Exercise;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidlab2.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class NewExerciseActivity extends AppCompatActivity {

    private EditText Title;
    private EditText Prepare;
    private EditText Work;
    private EditText Chill;
    private EditText Cycles;
    private EditText Sets;
    private EditText SetChill;
    private DbAdapter adapter;
    private int color;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exercise);
        adapter = new DbAdapter(this);
        Bundle extras = getIntent().getExtras();
        setupUI();
        if (extras != null)
        {
            id = extras.getInt("id");
        }
        if (id > 0)
        {
            adapter.open();
            Exercise exe = adapter.getExe(id);
            Title.setText(exe.title);
            Prepare.setText(String.valueOf(exe.prepare));
            Work.setText(String.valueOf(exe.work));
            Chill.setText(String.valueOf(exe.chill));
            Cycles.setText(String.valueOf(exe.cycles));
            Sets.setText(String.valueOf(exe.sets));
            SetChill.setText(String.valueOf(exe.setChill));
            color = exe.color;
            adapter.close();
        }

    }

    void setupUI(){
        Title = findViewById(R.id.newTitle);
        Prepare = findViewById(R.id.newPrepare);
        Work = findViewById(R.id.newWork);
        Chill = findViewById(R.id.newChill);
        Cycles = findViewById(R.id.newCycles);
        Sets = findViewById(R.id.newSets);
        SetChill = findViewById(R.id.newSetChill);
        Button btnColor = findViewById(R.id.btnColor);
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = Title.getText().toString();
                int prepare = Integer.parseInt(Prepare.getText().toString());
                int work = Integer.parseInt(Work.getText().toString());
                int chill = Integer.parseInt(Chill.getText().toString());
                int cycles = Integer.parseInt(Cycles.getText().toString());
                int sets = Integer.parseInt(Sets.getText().toString());
                int setChill = Integer.parseInt(SetChill.getText().toString());
                Exercise exe = new Exercise(id, color, title, prepare, work, chill, cycles, sets, setChill);
                adapter.open();
                if (id > 0)
                {
                    adapter.update(exe);
                } else {
                    adapter.insert(exe);
                }
                adapter.close();
                returnToMain();
            }
        });
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = NewExerciseActivity.this;

                ColorPickerDialogBuilder
                        .with(context)
                        .setTitle(R.string.color)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(getApplicationContext(),"onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT);
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                setColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .showColorEdit(true)
                        .setColorEditTextColor(ContextCompat.getColor(NewExerciseActivity.this, android.R.color.holo_blue_bright))
                        .build()
                        .show();
            }
        });
    }

    void setColor(int color){
        this.color = color;
    }
    void returnToMain(){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
    }


}