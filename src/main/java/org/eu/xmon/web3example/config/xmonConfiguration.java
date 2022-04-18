package org.eu.xmon.web3example.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

public abstract class xmonConfiguration<T> {
    private final transient static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final transient static Logger logger = Logger.getLogger(xmonConfiguration.class.getName());

    private final transient String name;
    private final transient Class<T> configClass;
    private final transient Path configPath;
    private final transient Path filePath;

    public xmonConfiguration(String name, Path path ,Class<T> configClass) {
        this.name = name;
        this.configClass = configClass;
        this.configPath = path;
        this.filePath = Paths.get(path.toString() + "/" + name);
    }

    public T load() {
        logger.info("xmonConfiguration "+ name + " is loading...");

        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath);
            }

            if (!Files.exists(filePath)) {
                T config = configClass.newInstance();

                Files.createFile(filePath);
                Files.writeString(filePath, gson.toJson(config), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                logger.info("xmonConfiguration "+ name + " was created.");
                logger.warning("Configure your " + name + " first!");
                System.exit(-1);
            }

            if (filePath.toFile().length() == 0) {
                T config = configClass.newInstance();
                Files.writeString(filePath, gson.toJson(config), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }

            try(FileReader reader = new FileReader(filePath.toFile())) {
                T json = gson.fromJson(reader, configClass);

                logger.info("xmonConfiguration "+ name + " was loaded correctly.");
                return json;
            }
        } catch (IllegalAccessException | InstantiationException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void update(T config) {
        try {
            Files.delete(filePath);
            Files.writeString(filePath, gson.toJson(config), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
