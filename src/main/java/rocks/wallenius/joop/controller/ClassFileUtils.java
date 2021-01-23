package rocks.wallenius.joop.controller;

import rocks.wallenius.joop.model.Source;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class ClassFileUtils {

    private final static String SOURCES_DIR = "sources/";

    private ClassFileUtils() {}

    public static void saveClass(Source source) throws IOException {

        File file = source.getFile();

        Path path;
        if(file == null) {
            file = new File(String.format("%s%s.java", SOURCES_DIR, source.getClassName()));
            source.setFile(file);
        }

        path = source.getFile().toPath();

        Files.createDirectories(path.getParent());
        Files.write(path, source.getCode().getBytes("UTF-8"));
    }

    public static String loadClass(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));
    }

    public static boolean exists(String path) {

        if(path.toLowerCase().endsWith(".java")) {
            path = path.substring(0, path.toLowerCase().lastIndexOf(".java"));
        }

        path = path.replace(".", "/");

        File file = new File(String.format("%s%s.java", SOURCES_DIR, path));
        return file.exists();
    }

    public static String loadClassTemplate(String fullyQualifiedName) throws IOException, URISyntaxException {
        URL url = ClassFileUtils.class.getResource("/class_template.template");
        String content = new String(Files.readAllBytes(Paths.get(url.toURI())));
        String pckg = getPackageName(fullyQualifiedName);
        String className = getClassName(fullyQualifiedName);
        return content.replace("<<CLASSNAME>>", className).replace("<<PACKAGE>>", pckg.length() > 0 ? String.format("package %s;", pckg) : pckg);
    }

    public static String getFullyQualifiedName(String name, String classDefinition) {
        String pckg = "";

        if(name.toLowerCase().contains(".java")) {
            name = name.substring(0, name.toLowerCase().lastIndexOf("."));
        }

        // Find package
        Pattern p = Pattern.compile("^package ([_0-9a-zA-Z.]+);");
        Matcher m = p.matcher(classDefinition.toLowerCase());

        if(m.find()) {
            pckg = m.group(1);
        }

        return pckg.length() > 0 ? pckg + "." + name : name;
    }

    public static String getPackageName(String fullyQualifiedName) {
        String result = "";
        if(fullyQualifiedName.contains(".")) {
            result = fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf("."));
        }
        return result;
    }

    public static String getClassName(String fullyQualifiedName) {
        String result = fullyQualifiedName;
        if(fullyQualifiedName.contains(".")) {
            result = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".") + 1);
        }
        return result;
    }

}
