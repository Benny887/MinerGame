package controller;

import model.game.IModelContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.ButtonType;
import view.GameType;

import java.io.IOException;


public class GameController implements IController {

    private final IModelContract model;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameController.class);

    public GameController(IModelContract model) {
        this.model = model;
    }


    @Override
    public void setSetting(GameType gameType) {
        model.changeSettings(gameType);
    }
    @Override
    public void onFieldClick(int x, int y, ButtonType buttonType) {
        switch (buttonType) {
            case LEFT_BUTTON:
                model.select(x, y);
                break;
            case RIGHT_BUTTON:
                model.flagging(x, y);
                break;
            case MIDDLE_BUTTON:
                model.openAroundNums(x, y);
                break;
        }
    }

    @Override
    public void newGame() {
        model.newGame();
    }



    @Override
    public void setNameForRecord(String name) {
        model.saveRecord(name);
    }

    @Override
    public void loadRecords() {
        try {
            model.loadRecords();
        } catch (IOException e) {
            logger.error("Ошибка при чтении файла.");
        }
    }

    @Override
    public void close() {
        model.destroy();
    }
}
