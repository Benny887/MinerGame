package model.setting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(FileUtils.class);

    public static Map<String, String> loadFilesData(String file) {
        Map<String, String> result = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String dataFromFile = reader.readLine();
            while (dataFromFile != null) {
                String[] values = dataFromFile.split("=");
                result.put(values[0], values[1]);
                dataFromFile = reader.readLine();
            }
        } catch (IOException e) {
            logger.error("Ошибка при чтении файла.");
        }
        return result;
    }

    public static void saveDataToFiles(Map<String, String> map, String file) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            map.forEach((k, v) -> {
                try {
                    fileWriter.write(k + "=" + v + "\n");
                } catch (IOException e) {
                    logger.error("Ошибка при записи в файл.");
                }
            });
        }
    }
}
