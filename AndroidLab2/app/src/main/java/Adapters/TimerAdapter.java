package Adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.androidlab2.R;

import java.util.ArrayList;

public class TimerAdapter extends ArrayAdapter<Pair<String, Integer>>
{
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Pair<String, Integer>> items;
    public TimerAdapter(Context context, int resource, ArrayList<Pair<String, Integer>> items)
    {
        super(context, resource, items);
        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view=inflater.inflate(this.layout, parent, false);
        TextView tvItem = (TextView) view.findViewById(R.id.timerItem);
        Pair<String, Integer> item = items.get(position);
        tvItem.setText(item.first + ": " + item.second);

        return view;
    }
}
