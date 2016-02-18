package rocks.wallenius.joop.util;

import rocks.wallenius.joop.model.entity.CustomClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class ClassFileUtils {

    private ClassFileUtils() {}

    public static void saveClass(CustomClass clazz) throws IOException {
        Files.write(clazz.getPath(), clazz.getCode().getBytes());
    }

    public static String loadClass(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));
    }

    public static boolean exists(String className) {

        if(className.toLowerCase().endsWith(".java")) {
            className = className.substring(0, className.toLowerCase().lastIndexOf(".java"));
        }

        File file = new File(String.format("usergenerated/%s.java", className));
        return file.exists();
    }

}
