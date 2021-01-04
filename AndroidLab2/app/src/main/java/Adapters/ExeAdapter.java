package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.androidlab2.R;

import java.util.List;

import Activity.MainActivity;
import Activity.NewExerciseActivity;
import Activity.TimerActivity;

public class ExeAdapter extends ArrayAdapter<Exercise> {
    private final LayoutInflater inflater;
    private final int layout;
    private final List<Exercise> exeList;
    private final Context mCtx;
    private final DbAdapter adapter;

    public ExeAdapter(Context context, int resource, List<Exercise> exes)
    {
        super(context, resource, exes);
        this.exeList = exes;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.mCtx = context;
        adapter = new DbAdapter(context);
    }
    public View getView(int position, View convertView, ViewGroup parent)
    {

        final ViewHolder viewHolder;
        if(convertView==null)
        {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Exercise exe = exeList.get(position);
        viewHolder.itemTitle.setText(exe.title);
        viewHolder.item.setBackgroundColor(exe.color);
        viewHolder.item.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mCtx, TimerActivity.class);
                intent.putExtra("id", exe.id);
                intent.putExtra("click", 25);
                mCtx.startActivity(intent);
            }
        });

        try
        {
            viewHolder.itemOptions.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (v.getId() == R.id.itemOptions) {
                        PopupMenu popup = new PopupMenu(mCtx, v);
                        popup.getMenuInflater().inflate(R.menu.item_menu,
                                popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menu_edit:
                                        Intent intent = new Intent(mCtx, NewExerciseActivity.class);
                                        intent.putExtra("id", exe.id);
                                        intent.putExtra("click", 25);
                                        mCtx.startActivity(intent);
                                        break;
                                    case R.id.menu_delete:
                                        adapter.open();
                                        adapter.delete(exe.id);
                                        adapter.close();
                                        mCtx.startActivity(new Intent(mCtx, MainActivity.class));
                                        break;
                                    default:
                                        break;
                                }
                                return true;
                            }
                        });
                    }
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder
    {
        final TextView itemTitle;
        final ImageView itemOptions;
        final LinearLayout item;
        ViewHolder(View view)
        {
            itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            item = (LinearLayout) view.findViewById(R.id.item);
            itemOptions = (ImageView) view.findViewById(R.id.itemOptions);
        }
    }
}
