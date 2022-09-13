package model.game;

public class Cell {

    private int value;
    private CellState state;
    public int X, Y;

    public Cell(int x, int y) {
        value = 0;
        state = CellState.CLOSE;
        X = x;
        Y = y;
    }

    public void changeFlag() {
        if (isFlag()) state = CellState.CLOSE;
        else state = CellState.FLAG;
    }

    public boolean isFlag() {
        return state == CellState.FLAG;
    }

    public CellState getState() {
        return state;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isClose() {
        return state == CellState.CLOSE;
    }

    public void setState(CellState state) {
        this.state = state;
    }
}
