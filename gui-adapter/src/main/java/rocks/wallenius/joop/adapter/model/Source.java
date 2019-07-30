package rocks.wallenius.joop.adapter.model;

import java.io.File;

/**
 * Created by philipwallenius on 30/07/2019.
 */
public class Source {
    private String className;
    private File file;
    private String code;

    public Source(String className, String code) {
        this.className = className;
        this.code = code;
    }
}
