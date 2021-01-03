package com.example.seafight.game;

public class FieldValidator {
    private final GameManager.UnitState[][] field;
    private final boolean[][] checkedCells;
    int size;

    public FieldValidator(GameManager.UnitState[][] field) {
        this.field = field;
        size = field.length;
        checkedCells = new boolean[size][size];
    }

    public boolean Check(){
        return CheckShipNum();
    }

    public boolean CheckShipCellsInAngles(int i, int j) {
        return i > 0 && j > 0 && field[i-1][j-1] == GameManager.UnitState.ALIVE
                || i > 0 && j < size - 1 && field[i-1][j + 1] == GameManager.UnitState.ALIVE
                || i < size - 1 && j < size-1 && field[i+1][j+1] == GameManager.UnitState.ALIVE
                || i < size - 1 && j > 0 && field[i+1][j-1] == GameManager.UnitState.ALIVE;
    }

    public boolean isDefeat() {
        int total = 0;
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                total+=field[i][j] == GameManager.UnitState.DESTROYED ? 1 : 0;
        return total == 20;
    }

    private boolean CheckShipNum() {
        int ship1 = 0, ship2 = 0, ship3 = 0, ship4 = 0;
        int currentShip = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                if (field[i][j] == GameManager.UnitState.ALIVE) {
                    if (i > 0 && j > 0 && field[i-1][j-1] == GameManager.UnitState.ALIVE || i > 0 && j < size - 1 && field[i-1][j + 1] == GameManager.UnitState.ALIVE
                            || i < size - 1 && j < size-1 && field[i+1][j+1] == GameManager.UnitState.ALIVE || i < size - 1 && j > 0 && field[i+1][j-1] == GameManager.UnitState.ALIVE) {
                        return false;
                    }
                    if (checkedCells[i][j]){ continue; }
                    if (j > 0 && field[i][j-1] == GameManager.UnitState.ALIVE || (j < size - 1) && field[i][j+1] == GameManager.UnitState.ALIVE) {
                        int x = j, y = i;

                        for (x = j; x < size && field[y][x] == GameManager.UnitState.ALIVE ; x++){
                            checkedCells[y][x] = true;
                            currentShip += 1;
                        }
                    } else {
                        int x = j, y = i;
                        for (y = i; y < size && field[y][x] == GameManager.UnitState.ALIVE ; y++){
                            checkedCells[y][x] = true;
                            currentShip += 1;
                        }
                    }

                    if (currentShip == 1) {
                        ship1++;
                    } else if (currentShip == 2) {
                        ship2++;
                    } else if (currentShip == 3) {
                        ship3++;
                    } else if (currentShip == 4) {
                        ship4++;
                    } else {
                        return false;
                    }

                    currentShip = 0;
                }
            }
        }
        return ship1 == 4 && ship2 == 3 && ship3 == 2 && ship4 == 1;
    }
}