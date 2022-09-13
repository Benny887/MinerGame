package controller;

import ru.cft.shift.task3.view.ButtonType;
import ru.cft.shift.task3.view.GameType;

public interface IController {
    void setSetting(GameType gameType);

    void newGame();

    void onFieldClick(int x, int y, ButtonType buttonType);

    void setNameForRecord(String name);

    void loadRecords();

    void close();

}
