package model.game;

import java.util.Map;

public interface IModelListener {

    interface Timer {
        void tick(int sec);
    }

    interface GameAction {
        void newGame(int sizeX, int sizeY, int mine);

        void endGame(GameResult result);

        void record();
    }

    interface FieldAction {
        void openCell(int x, int y, int value);

        void changeFlag(int x, int y, boolean state, int flags);
    }

    interface GameParameters {
        void loadRecords(Map<String, String> records);
    }

}
