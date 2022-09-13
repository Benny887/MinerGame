package model.game;

import view.GameType;

import java.io.IOException;

public interface IModelContract {
    void select(int x, int y);

    void flagging(int x, int y);

    void loadRecords() throws IOException;

    void saveRecord(String name);

    void newGame();

    void openAroundNums(int x, int y);

    void destroy();

    void changeSettings(GameType gameType);
}
