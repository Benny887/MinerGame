package view;

import controller.IController;
import model.game.GameResult;
import model.game.IModelListener;

import java.util.Map;

public class WindowManager implements IModelListener.Timer, IModelListener.FieldAction, IModelListener.GameAction, IModelListener.GameParameters {

    private final IController controller;
    private MainWindow mainWindow;
    private LoseWindow loseWindow;
    private WinWindow winWindow;
    private SettingsWindow settingsWindow;
    private HighScoresWindow highScoresWindow;
    private RecordsWindow recordsWindow;

    public WindowManager(IController gameController) {
        controller = gameController;
    }

    public void setHighScoresWindow(HighScoresWindow highScoresWindow) {
        this.highScoresWindow = highScoresWindow;
    }

    public void setSettingsWindow(SettingsWindow settingsWindow) {
        this.settingsWindow = settingsWindow;
        this.settingsWindow.setGameTypeListener(new GameTypeListener() {
            @Override
            public void onGameTypeChanged(GameType gameType) {
                controller.setSetting(gameType);
            }
        });
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.mainWindow.setNewGameMenuAction(e -> controller.newGame());
        this.mainWindow.setSettingsMenuAction(e -> settingsWindow.setVisible(true));
        this.mainWindow.setHighScoresMenuAction(e -> {
            controller.loadRecords();
            highScoresWindow.setVisible(true);
        });
        this.mainWindow.setExitMenuAction(e -> this.mainWindow.dispose());
        this.mainWindow.setCellListener(controller::onFieldClick);
        this.mainWindow.setVisible(true);
    }

    public void setLoseWindow(LoseWindow loseWindow) {
        this.loseWindow = loseWindow;
        this.loseWindow.setNewGameListener(e -> controller.newGame());
        this.loseWindow.setExitListener(e -> {
            this.loseWindow.setVisible(false);
            mainWindow.dispose();
            controller.close();
        });
    }

    public void setWinWindow(WinWindow winWindow) {
        this.winWindow = winWindow;
        this.winWindow.setNewGameListener(e -> controller.newGame());
        this.winWindow.setExitListener(e -> {
            this.winWindow.setVisible(false);
            mainWindow.dispose();
            controller.close();
        });
    }

    public void setRecordsWindow(RecordsWindow recordsWindow) {
        recordsWindow.setNameListener(controller::setNameForRecord);
    }

    public void makeWindows() {
        mainWindow = new MainWindow();
        recordsWindow = new RecordsWindow(mainWindow);
        loseWindow = new LoseWindow(mainWindow);
        highScoresWindow = new HighScoresWindow(mainWindow);
        winWindow = new WinWindow(mainWindow);
        settingsWindow = new SettingsWindow(mainWindow);
        setWindows();
    }

    private void setWindows() {
        setMainWindow(mainWindow);
        setRecordsWindow(recordsWindow);
        setLoseWindow(loseWindow);
        setHighScoresWindow(highScoresWindow);
        setWinWindow(winWindow);
        setSettingsWindow(settingsWindow);
    }

    @Override
    public void tick(int sec) {
        mainWindow.setTimerValue(sec);
    }

    @Override
    public void openCell(int x, int y, int value) {
        switch (value) {
            case -1:
                mainWindow.setCellImage(x, y, GameImage.BOMB);
                break;
            case 0:
                mainWindow.setCellImage(x, y, GameImage.EMPTY);
                break;
            case 1:
                mainWindow.setCellImage(x, y, GameImage.NUM_1);
                break;
            case 2:
                mainWindow.setCellImage(x, y, GameImage.NUM_2);
                break;
            case 3:
                mainWindow.setCellImage(x, y, GameImage.NUM_3);
                break;
            case 4:
                mainWindow.setCellImage(x, y, GameImage.NUM_4);
                break;
            case 5:
                mainWindow.setCellImage(x, y, GameImage.NUM_5);
                break;
            case 6:
                mainWindow.setCellImage(x, y, GameImage.NUM_6);
                break;
            case 7:
                mainWindow.setCellImage(x, y, GameImage.NUM_7);
                break;
            case 8:
                mainWindow.setCellImage(x, y, GameImage.NUM_8);
                break;
        }
    }

    @Override
    public void changeFlag(int x, int y, boolean state, int flags) {
        if (state) mainWindow.setCellImage(x, y, GameImage.MARKED);
        else mainWindow.setCellImage(x, y, GameImage.CLOSED);
        mainWindow.setBombsCount(flags);
    }

    @Override
    public void newGame(int sizeX, int sizeY, int mine) {
        mainWindow.createGameField(sizeY, sizeX);
        mainWindow.setBombsCount(mine);
    }

    @Override
    public void endGame(GameResult result) {
        switch (result.getResult()) {
            case WIN: {
                winWindow.setLocationRelativeTo(mainWindow);
                winWindow.setVisible(true);
                break;
            }
            case LOSE: {
                loseWindow.setLocationRelativeTo(mainWindow);
                loseWindow.setVisible(true);
                break;
            }
        }
    }

    @Override
    public void record() {
        recordsWindow.setLocationRelativeTo(mainWindow);
        recordsWindow.setVisible(true);
    }

    @Override
    public void loadRecords(Map<String, String> records) {
        records.forEach((k, v) -> {
            String[] values = v.split(":::");
            String _name = values[0];
            int value = Integer.parseInt(values[1]);
            String time = value == 99999 ? "No results" : String.valueOf(value);
            if (k.equals("EXPERT")) highScoresWindow.setExpertRecord(_name, time);
            if (k.equals("MEDIUM")) highScoresWindow.setMediumRecord(_name, time);
            if (k.equals("NOVICE")) highScoresWindow.setNoviceRecord(_name, time);
        });
    }
}
