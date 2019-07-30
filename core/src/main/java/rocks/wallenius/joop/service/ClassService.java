package rocks.wallenius.joop.service;

import rocks.wallenius.joop.domain.JoopClass;
import rocks.wallenius.joop.exception.CompilationException;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Created by philipwallenius on 28/07/2019.
 */
public interface ClassService {
    JoopClass compile(File[] files) throws CompilationException, IOException;

}
