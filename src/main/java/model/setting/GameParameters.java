package model.setting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GameParameters implements IParametersRepository {

    private final String separator = File.separator;
    private final File RECORDS_FILE = new File(System.getProperty("user.dir") + separator +
            "task3" + separator + "src" + separator + "main" + separator + "resources" + separator + "gameRecords.txt");

    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameParameters.class);

    public void saveRecord(String gameLevel, String name, int value) {
        try {
            Map<String, String> map = FileUtils.loadFilesData(RECORDS_FILE.getPath());
            map.put(gameLevel, name + ":::" + value);
            FileUtils.saveDataToFiles(map, RECORDS_FILE.getPath());
        } catch (IOException e) {
            logger.error("Ошибка при записи в файл.Рекорд не сохранен.");
        }
    }

    public Map<String, String> getRecords() {
        return FileUtils.loadFilesData(RECORDS_FILE.getPath());
    }

    public Map<String, String> getSettings() {
        return FileUtils.loadFilesData(getClass().getClassLoader().getResource("gameDefaultSettings.txt").getFile());
    }
}
