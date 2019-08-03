package rocks.wallenius.joop.model;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by philipwallenius on 30/07/2019.
 */
public class Source {
    private String className;
    private File file;
    private String code;

    public Source(String className, String code, File file) {
        this(className, code);

        if (file == null) {
            throw new IllegalArgumentException("Parameter file cannot be null");
        }

        this.file = file;
    }

    public Source(String className, String code) {

        if (className == null) {
            throw new IllegalArgumentException("Parameter code cannot be null");
        }
        if (code == null) {
            throw new IllegalArgumentException("Parameter className cannot be null");
        }

        this.className = className;
        this.code = code;

    }


    public String getFullyQualifiedClassName() {
        String pckg = "";

        if(className.toLowerCase().contains(".java")) {
            className = className.substring(0, className.toLowerCase().lastIndexOf("."));
        }

        // Find package
        Pattern p = Pattern.compile("^package ([_0-9a-zA-Z.]+);");
        Matcher m = p.matcher(code.toLowerCase());

        if(m.find()) {
            pckg = m.group(1);
        }

        return pckg.length() > 0 ? pckg + "." + className : className;
    }

    public String getClassName() {
        return className;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public File getFile() { return file; }

    public void setFile(File file) {
        this.file = file;
    }
}
