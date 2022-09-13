package app;


import controller.GameController;
import controller.IController;
import model.game.GameLogic;
import model.setting.GameParameters;
import model.setting.IParametersRepository;
import model.timer.SimpleTimer;
import view.WindowManager;

public class Application {
    public static void main(String[] args) {
        SimpleTimer simpleTimer = new SimpleTimer();
        IParametersRepository parametersRepository = new GameParameters();
        GameLogic model = new GameLogic(parametersRepository, simpleTimer);
        IController controller = new GameController(model);

        WindowManager windowManager = new WindowManager(controller);
        windowManager.makeWindows();

        model.setTimerListener(windowManager);
        model.setFieldListener(windowManager);
        model.setGameListener(windowManager);
        model.setGameParametersListener(windowManager);

        controller.newGame();
    }
}
