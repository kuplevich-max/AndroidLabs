package Activity;

import Adapters.DbAdapter;
import Adapters.Exercise;
import Adapters.TimerAdapter;
import Adapters.TimerService;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidlab2.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.LongAdder;

public class TimerActivity extends AppCompatActivity {

    private TextView titleTimer;
    private ListView listView;
    private DbAdapter adapter;
    private Button btn_Pause;
    private Button btn_back;
    private Button btn_next;
    private TextView ItemName;
    private boolean bound = false;
    private boolean needService;
    private ServiceConnection serviceConnection;
    private Intent intent;
    TimerService timerService;
    private CountDownTimer countDownTimer;
    private Exercise curExe = new Exercise();
    private boolean paused;
    private long timeLeft = 0;
    private long endTime;
    private int pos = 0;
    private int maxPos;
    private ArrayList<Pair<String,Integer>> list;
    private SoundPool soundPool;
    private int soundId;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setupUI();
        adapter = new DbAdapter(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            curExe.id = extras.getInt("id");
        }
        if (curExe.id > 0)
        {
            adapter.open();
            Exercise exe = adapter.getExe(curExe.id);
            curExe.prepare = exe.prepare;
            curExe.work = exe.work;
            curExe.chill = exe.chill;
            curExe.cycles = exe.cycles;
            curExe.sets = exe.sets;
            curExe.setChill = exe.setChill;
            adapter.close();
        }

        list = new ArrayList<Pair<String,Integer>>();
        list.add(new Pair<String,Integer>(getString(R.string.prepare), curExe.prepare));
        for(LongAdder i = new LongAdder(); i.sum() < curExe.sets; i.increment())
        {
            for(int j = 0; j < curExe.cycles; j++)
            {
                list.add(new Pair<String,Integer>(getString(R.string.work), curExe.work));
                list.add(new Pair<String,Integer>(getString(R.string.pause), curExe.chill));
            }
            list.add(new Pair<String,Integer>(getString(R.string.setPause), curExe.setChill));
        }
        maxPos = 1 + curExe.sets * (curExe.cycles * 2 + 1);

        TimerAdapter timerAdapter = new TimerAdapter(this,
                R.layout.timer_item, list);
        listView.setAdapter(timerAdapter);

        timeLeft = list.get(pos).second * 1000;

        needService = true;
        intent = new Intent(this, TimerService.class);
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int i = 0; i < maxPos; i++)
        {
            temp.add(list.get(i).second);
        }
        intent.putExtra("modes", temp);
        serviceConnection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder)
            {
                timerService = ((TimerService.TimerBinder) binder).getService();
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                bound = false;
            }
        };
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (bound)
        {
            pos = timerService.getPos();
            timeLeft = timerService.getTimeLeft();
            unbindService(serviceConnection);
            stopService(intent);
            bound = false;
        }

        if (paused)
        {
            btn_Pause.setText(R.string.go);
        }
        else
        {
            btn_Pause.setText(R.string.pause);
        }
        titleTimer.setText(String.valueOf((int)(timeLeft/1000)));
        ItemName.setText(list.get(pos).first);
        if (!paused)
        {
            timer();
        }
        listView.setSelection(pos);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (needService && (!bound) && (!paused))
        {
            intent.putExtra("pos", pos);
            intent.putExtra("timeLeft", timeLeft);
            startService(intent);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            bound = true;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (bound)
        {
            unbindService(serviceConnection);
            stopService(intent);
            bound = false;
        }
    }

    public void startTimer()
    {
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
        }
        ItemName.setText(list.get(pos).first);
        timeLeft = list.get(pos).second * 1000;
        titleTimer.setText(String.valueOf((int)(timeLeft/1000)));
        if (!paused) {
            timer();
        }
        listView.setSelection(pos);
    }

    public void next()
    {

        if (pos == maxPos - 1)
        {
            pos++;
            if (countDownTimer != null)
            {
                countDownTimer.cancel();
            }
            titleTimer.setText(R.string.end);
            ItemName.setText(R.string.end);
        }
        else if (pos == maxPos)
        {

        }
        else {
            pos++;
            startTimer();
        }
    }

    public void timer()
    {
        endTime = timeLeft + System.currentTimeMillis();
        countDownTimer = new CountDownTimer(timeLeft, 1000)
        {
            @Override
            public void onTick(long l)
            {
                timeLeft = l;
                titleTimer.setText(String.valueOf((int)(l/1000)));
            }

            @Override
            public void onFinish()
            {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                next();
            }
        }.start();
    }

    @Override
    public void onBackPressed()
    {
        countDownTimer.cancel();
        needService = false;
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPause", paused);
        outState.putInt("pos", pos);
        outState.putLong("timeLeft", timeLeft);
        outState.putLong("endTime", endTime);
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        paused = savedInstanceState.getBoolean("isPause");
        pos = savedInstanceState.getInt("pos");
        timeLeft = savedInstanceState.getLong("timeLeft");
        endTime = savedInstanceState.getLong("endTime");
        if (!paused)
        {
            timeLeft = endTime - System.currentTimeMillis();
        }
    }
    void setupUI(){
        titleTimer = findViewById(R.id.Timer);
        listView = findViewById(R.id.Modes);
        ItemName = findViewById(R.id.ItemName);
        btn_Pause = findViewById(R.id.btnPause);
        btn_back = findViewById(R.id.btn_back);
        btn_next = findViewById(R.id.btn_next);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(this, R.raw.sound, 1);

        paused = false;
        btn_Pause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (paused)
                {
                    if (pos < maxPos)
                    {
                        btn_Pause.setText(R.string.pause);
                        paused = false;
                        timer();
                    }
                }
                else {
                    btn_Pause.setText(R.string.go);
                    paused = true;
                    countDownTimer.cancel();
                }

            }
        });
        btn_next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                next();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (pos > 0)
                {
                    pos--;
                    countDownTimer.cancel();
                    startTimer();
                }
            }
        });
    }
}