package rocks.wallenius.joop.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by philipwallenius on 15/02/16.
 */
public class JavaUtil {

    private static final Pattern JAVA_CLASS_NAME_REGEX = Pattern.compile("[A-Za-z_$]+[a-zA-Z0-9_$]*");
    private static final Set<String> JAVA_KEYWORDS = new HashSet<String>(Arrays.asList(
            "abstract",     "assert",        "boolean",      "break",           "byte",
            "case",         "catch",         "char",         "class",           "const",
            "continue",     "default",       "do",           "double",          "else",
            "enum",         "extends",       "false",        "final",           "finally",
            "float",        "for",           "goto",         "if",              "implements",
            "import",       "instanceof",    "int",          "interface",       "long",
            "native",       "new",           "null",         "package",         "private",
            "protected",    "public",        "return",       "short",           "static",
            "strictfp",     "super",         "switch",       "synchronized",    "this",
            "throw",        "throws",        "transient",    "true",            "try",
            "void",         "volatile",      "while"
    ));

    private JavaUtil(){}

    public static boolean isValidClassName(String text) {
        if(text.length() > 0) {
            for (String part : text.split("\\.")) {
                if (JAVA_KEYWORDS.contains(part.toLowerCase()) ||
                        !JAVA_CLASS_NAME_REGEX.matcher(part).matches()) {
                    return false;
                }
            }
        }
        return true;
    }

}
