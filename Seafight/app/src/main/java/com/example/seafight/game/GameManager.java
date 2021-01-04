package com.example.seafight.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.seafight.MainActivity;
import com.example.seafight.R;

public class GameManager {
    public enum UnitState {
        EMPTY, DESTROYED, MISSED, ALIVE
    }

    public enum GameState {
        Waiting, Ended, hostTurn, userTurn
    }

    public static UnitState[][] getEmptyField() {
        UnitState[][] units = new UnitState[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                units[i][j] = UnitState.EMPTY;
            }
        }
        return units;
    }

    public static class GameStatus {
        public final static Character winCondition = '~';
        public GameStatus() { }
        public UnitState[][] hostState;
        public UnitState[][] userState;
        public String hostId = null;
        public String userId = null;
        public GameState state = GameState.Waiting;
        public GameStatus(UnitState[][] hostState, UnitState[][] userState, String hostId, String userId, GameState state) {
            this.hostState = hostState; this.userState = userState;
            this.hostId = hostId; this.userId = userId;
            this.state = state;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static TableLayout createField(Context context, View view, int fieldId, GameManager.UnitState[][] states) {
        TableLayout cellsView = (TableLayout) view.findViewById(fieldId);

        // it may be ownField, userField, hostField

        int[] screenSize = getScreenSize(context);
        int cellSizeX = screenSize[0] / states.length - 20;
        int cellSizeY = screenSize[1] / (states.length * 2) - 20;
        for (GameManager.UnitState[] state : states) {
            // Row creating
            TableRow row = new TableRow(view.getContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT
            ));

            // Row filling
            for (int j = 0; j < state.length; j++) {
                Button cellView = new Button(view.getContext());
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable cellBg = context.getResources().getDrawable(R.drawable.unit_empty, view.getContext().getTheme());
                cellView.setLayoutParams(new TableRow.LayoutParams(cellSizeX, cellSizeY));
                cellView.setBackground(cellBg);
                row.addView(cellView);
            }
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            );
            cellsView.addView(row, params);
        }
        return cellsView;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setCellBackgroundState(Context context, View v, GameManager.UnitState state, boolean hide) {
        if (hide) {
            switch (state){
                case ALIVE:
                case EMPTY:
                    v.setBackground(context.getResources().getDrawable(
                            R.drawable.unit_hidden,     context.getTheme())); break;
                case MISSED: v.setBackground(context.getResources().getDrawable(
                        R.drawable.unit_missed,    context.getTheme())); break;
                case DESTROYED: v.setBackground(context.getResources().getDrawable(
                        R.drawable.unit_destroyed, context.getTheme())); break;
            }
        } else {
            switch (state){
                case ALIVE: v.setBackground(context.getResources().getDrawable(
                        R.drawable.unit_alive,     context.getTheme())); break;
                case MISSED: v.setBackground(context.getResources().getDrawable(
                        R.drawable.unit_missed,    context.getTheme())); break;
                case EMPTY: v.setBackground(context.getResources().getDrawable(
                        R.drawable.unit_empty,     context.getTheme())); break;
                case DESTROYED: v.setBackground(context.getResources().getDrawable(
                        R.drawable.unit_destroyed, context.getTheme())); break;
            }
        }
    }

    // context must be main activity
    public static int[] getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((MainActivity)(context)).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return new int[] {width, height};
    }

    public static String fieldToString(GameManager.UnitState[][] field) {
        StringBuilder jsonString = new StringBuilder("[");
        for (int i = 0; i < field.length; i++) {
            jsonString.append("[");
            for (int j = 0; j < field.length; j++) {
                jsonString.append(field[i][j].ordinal());
                if (j != field.length - 1) jsonString.append(",");
            }
            jsonString.append("]");
            if (i != field.length - 1) jsonString.append(",");
        }
        jsonString.append("]");
        return jsonString.toString();
    }

    public static void fillField(Context context, TableLayout field, GameManager.UnitState[][] values, boolean hide) {
        int size = values.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                GameManager.setCellBackgroundState(context,
                        ((TableRow)field.getChildAt(i)).getChildAt(j), values[i][j], hide);
            }
        }
    }

    public static class AccountInfo {
        public String accountImage;
        public String accountEmail;
        public String accountName;
    }
}
