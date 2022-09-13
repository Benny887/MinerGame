package model.setting;

import java.io.IOException;
import java.util.Map;

public interface IParametersRepository {
    void saveRecord(String gameLevel, String name, int value);

    Map<String, String> getRecords() throws IOException;

    Map<String, String> getSettings() throws IOException;
}
