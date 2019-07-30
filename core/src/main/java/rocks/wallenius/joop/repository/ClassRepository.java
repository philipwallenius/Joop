package rocks.wallenius.joop.repository;

import rocks.wallenius.joop.domain.JoopClass;

import java.net.URI;

/**
 * Created by philipwallenius on 28/07/2019.
 */
public interface ClassRepository {
    JoopClass getClassByFullyQualifiedName(URI uri);
}
