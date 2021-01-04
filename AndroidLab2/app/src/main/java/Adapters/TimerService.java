package Adapters;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.example.androidlab2.R;

import java.util.ArrayList;

public class TimerService extends Service
{

    TimerBinder binder = new TimerBinder();

    private CountDownTimer countDownTimer;

    private long timeLeft;
    private int pos;
    private int maxPos;
    private ArrayList<Integer> list;
    private SoundPool sp;
    private int soundIdBell;

    public void onCreate()
    {
        super.onCreate();
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundIdBell = sp.load(this, R.raw.sound, 1);
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        list = intent.getIntegerArrayListExtra("modes");
        maxPos = list.size();
        timeLeft = intent.getLongExtra("timeLeft", 0);
        pos = intent.getIntExtra("pos", 0);
        timer();
        return super.onStartCommand(intent, flags, startId);
    }


    public IBinder onBind(Intent arg0)
    {
        return binder;
    }

    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    public void onDestroy()
    {
        super.onDestroy();
        countDownTimer.cancel();
    }

    public void startTimer()
    {
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
        }
        timeLeft = list.get(pos) * 1000;
        timer();
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
        countDownTimer = new CountDownTimer(timeLeft, 1000)
        {
            @Override
            public void onTick(long l)
            {
                timeLeft = l;
            }

            @Override
            public void onFinish()
            {
                sp.play(soundIdBell, 1, 1, 0, 0, 1);
                next();
            }
        }.start();
    }

    public int getPos()
    {
        return pos;
    }

    public long getTimeLeft()
    {
        return timeLeft;
    }


    public class TimerBinder extends Binder
    {
        public TimerService getService()
        {
            return TimerService.this;
        }
    }
}
