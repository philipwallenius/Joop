package rocks.wallenius.joop.util;

import rocks.wallenius.joop.configuration.ConfigurationService;
import rocks.wallenius.joop.model.entity.CustomClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class ClassFileUtils {

    private final static String CONF_SOURCES_DIR = "sources.directory";

    private static ConfigurationService config;

    static {
        config = ConfigurationService.getInstance();
    }

    private ClassFileUtils() {}

    public static void saveClass(CustomClass clazz) throws IOException {
        Files.createDirectories(clazz.getPath().getParent());
        Files.write(clazz.getPath(), clazz.getCode().getBytes("UTF-8"));
    }

    public static String loadClass(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));
    }

    public static boolean exists(String path) {

        if(path.toLowerCase().endsWith(".java")) {
            path = path.substring(0, path.toLowerCase().lastIndexOf(".java"));
        }

        path = path.replace(".", "/");

        File file = new File(String.format("%s%s.java", config.getString(CONF_SOURCES_DIR), path));
        return file.exists();
    }

}
