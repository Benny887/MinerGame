package model.game;

import model.setting.IParametersRepository;
import model.timer.SimpleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.GameType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameLogic implements IModelContract {

    private final int[][] pattern = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
    };

    private IModelListener.Timer timerListener;
    private IModelListener.FieldAction fieldListener;
    private IModelListener.GameAction gameListener;
    private IModelListener.GameParameters gameParametersListener;

    private final IParametersRepository gameParameters;

    private Cell[][] gameField;

    private final SimpleTimer timer;
    private List<Cell> minesList = new ArrayList<>();

    private int sizeX, sizeY;
    private int mine, flags;
    private int openCellsCount;
    private int gameSteps;
    private String gameLevel;
    private int gameTime = 0;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameLogic.class);

    public GameLogic(IParametersRepository parametersRepository, SimpleTimer timer) {
        this.timer = timer;
        gameParameters = parametersRepository;
        loadSettings();
        initGameField();
    }

    public void setTimerListener(IModelListener.Timer timerListener) {
        this.timerListener = timerListener;
    }

    public void setFieldListener(IModelListener.FieldAction fieldListener) {
        this.fieldListener = fieldListener;
    }

    public void setGameListener(IModelListener.GameAction gameListener) {
        this.gameListener = gameListener;
    }

    public void setGameParametersListener(IModelListener.GameParameters gameParametersListener) {
        this.gameParametersListener = gameParametersListener;
    }

    @Override
    public void select(int x, int y) {
        if (gameSteps == 0) {
            initGameField();
            createMines(x, y);
            initTimer();
            logger.info("First click");
        }
        if (gameField[x][y].isClose() && !gameField[x][y].isFlag()) {
            int value = gameField[x][y].getValue();
            if (value != -1) openCell(x, y);
            else end(Result.LOSE);
        }
        if (minesList.size() > 0) {
            nextStep();
        }
        if ((sizeX * sizeY - openCellsCount) == mine) end(Result.WIN);
    }

    @Override
    public void flagging(int x, int y) {
        if (gameField[x][y].getState() != CellState.OPEN) {
            if (!gameField[x][y].isFlag()) {
                if (flags > 0) {
                    flags--;
                    gameField[x][y].changeFlag();
                }
            } else {
                if (flags <= mine) {
                    flags++;
                    gameField[x][y].changeFlag();
                }
            }
            if (fieldListener != null) fieldListener.changeFlag(x, y, gameField[x][y].isFlag(), flags);
        }
    }

    @Override
    public void openAroundNums(int x, int y) {
        Cell cell = gameField[x][y];
        int flagsOnMine = 0;
        int countOfFlags = 0;
        int flagsWithoutMines = 0;
        if (cell.getState() == CellState.OPEN) {
            if (cell.getValue() > 0) {
                for (int i = 0; i < pattern.length; i++) {
                    for (int j = 0; j < pattern[0].length; j++) {
                        int targetX = x + i - 1;
                        int targetY = y + j - 1;
                        if (targetX == -1 || targetY == -1 || targetX >= sizeX || targetY >= sizeY) continue;
                        if (i == 1 && j == 1) continue;
                        if (gameField[targetX][targetY].isFlag() && gameField[targetX][targetY].getValue() == -1)
                            flagsOnMine++;
                        if (gameField[targetX][targetY].isFlag())
                            countOfFlags++;
                        if (gameField[targetX][targetY].isFlag() && gameField[targetX][targetY].getValue() != -1)
                            flagsWithoutMines++;
                    }
                }
                if (flagsOnMine == cell.getValue()) {
                    openCellsAround(x, y);
                } else if (countOfFlags == cell.getValue() && flagsWithoutMines > 0)
                    end(Result.LOSE);
                if ((isAllCellsOpen() && isAllMineWithFlags()) || ((sizeX * sizeY - openCellsCount) == mine))
                    end(Result.WIN);
            }
        }
    }

    @Override
    public void changeSettings(GameType gameType) {
        reset();
        switch (gameType) {
            case NOVICE:
                changeSettings(9, 9, 10, gameType.name());
                break;
            case MEDIUM:
                changeSettings(16, 16, 40, gameType.name());
                break;
            case EXPERT:
                changeSettings(30, 16, 99, gameType.name());
                break;
        }
    }

    private void changeSettings(int sizeX, int sizeY, int mine, String gameLevel) {
        reset();
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.mine = flags = mine;
        this.gameLevel = gameLevel;
        newGame();
    }

    @Override
    public void loadRecords() {
        try {
            gameParametersListener.loadRecords(gameParameters.getRecords());
            logger.info("Records loaded...");
        } catch (IOException e) {
            logger.error("Ошибка при считывании рекордов игры.");
        }
    }

    @Override
    public void saveRecord(String name) {
        gameParameters.saveRecord(gameLevel, name, gameTime);
        logger.info("Save new record " + name + ": " + gameTime);
    }

    private void loadSettings() {
        logger.info("Setting loaded...");
        try {
            Map<String, String> settings = gameParameters.getSettings();
            sizeX = Integer.parseInt(settings.get("sizeX"));
            sizeY = Integer.parseInt(settings.get("sizeY"));
            flags = mine = Integer.parseInt(settings.get("mine"));
            gameLevel = settings.get("level");
        } catch (IOException e) {
            logger.error("Ошибка при считывании настроек игры. Установлены настройки по умолчанию.");
            sizeX = 9;
            sizeY = 9;
            flags = mine = 10;
            gameLevel = "NOVICE";
        }
    }

    @Override
    public void newGame() {
        if (gameListener != null) {
            gameListener.newGame(sizeX, sizeY, mine);
        }
        openCellsCount = 0;
        gameSteps = 0;
        gameTime = 0;
        flags = mine;
        stopTimer();
        clearMineList();
        logger.info("New Game");
    }

    private void reset() {
        openCellsCount = 0;
        gameSteps = 0;
        gameTime = 0;
        flags = mine;
        stopTimer();
        clearField();
        clearMineList();
    }

    public void end(Result result) {
        logger.info("Game end");
        stopTimer();
        openAllMine();
        GameResult gameResult = new GameResult(result);
        if (result == Result.WIN)
            if (isRecord()) gameListener.record();
        if (gameListener != null) gameListener.endGame(gameResult);
        reset();
    }

    private void nextStep() {
        if (isAllCellsOpen() || isAllMineWithFlags()) {
            end(Result.WIN);
            return;
        }
        gameSteps++;
    }

    @Override
    public void destroy() {
        timer.end();
        reset();
        clearMineList();
        clearField();
    }

    private void openAllMine() {
        for (Cell c : minesList) {
            openCell(c.X, c.Y);
        }
    }

    private void openCell(int x, int y) {
        Cell cell = gameField[x][y];
        cell.setState(CellState.OPEN);
        openCellsCount++;
        fieldListener.openCell(x, y, cell.getValue());
        if (cell.getValue() == 0) openCellsAround(x, y);
        logger.info("Open cell: " + x + " " + y);
    }

    private void openCellsAround(int x, int y) {
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[0].length; j++) {
                int targetX = x + i - 1;
                int targetY = y + j - 1;
                if (targetX == -1 || targetY == -1 || targetX >= sizeX || targetY >= sizeY) continue;
                if (i == 1 && j == 1) continue;
                if (gameField[targetX][targetY].getState() == CellState.CLOSE)
                    openCell(targetX, targetY);
            }
        }
    }

    private void initGameField() {
        gameField = new Cell[sizeX][sizeY];
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[0].length; j++) {
                gameField[i][j] = new Cell(i, j);
            }
        }
    }

    private void createMines(int noneMineX, int noneMineY) {
        int count = 0;
        Random random = new Random();
        minesList = new ArrayList<>();
        while (count != mine) {
            int i = random.nextInt(sizeX);
            int j = random.nextInt(sizeY);
            if (i == noneMineX && j == noneMineY) continue;
            if (gameField[i][j].getValue() == -1) continue;
            setMine(i, j);
            createNums(i, j);
            count++;
        }
        System.out.println();
    }

    private void setMine(int x, int y) {
        gameField[x][y].setValue(-1);
        minesList.add(gameField[x][y]);
    }

    private void createNums(int mineX, int mineY) {
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[0].length; j++) {
                int numX = mineX + i - 1;
                int numY = mineY + j - 1;
                if (numX == -1 || numY == -1 || numX >= sizeX || numY >= sizeY) continue;
                if (i == 1 && j == 1) continue;
                int value = gameField[numX][numY].getValue();
                if (value != -1) {
                    gameField[numX][numY].setValue(value + 1);
                }
            }
        }
    }

    private boolean isRecord() {
        int record = 0;
        try {
            record = Integer.parseInt(gameParameters.getRecords().get(gameLevel).split(":::")[1]);
        } catch (IOException e) {
            logger.error("Ошибка при чтении файла.");
        }
        return gameTime < record;
    }

    private boolean isAllMineWithFlags() {
        if (minesList.size() == 0) return false;
        for (Cell c : minesList) {
            if (!c.isFlag())
                return false;
        }
        return true;
    }

    private boolean isAllCellsOpen() {
        return openCellsCount == sizeX * sizeY - mine;
    }

    private void initTimer() {
        timer.go((sec) -> {
            gameTime++;
            timerListener.tick(sec);
        });

    }

    private void stopTimer() {
        timer.end();
    }

    private void clearMineList() {
        if (minesList.size() > 0) minesList.clear();
    }

    private void clearField() {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                gameField[i][j] = null;
            }
        }
    }
}
