package rocks.wallenius.joop.adapter.repository;

import rocks.wallenius.joop.domain.JoopClass;
import rocks.wallenius.joop.repository.ClassRepository;

import java.net.URI;

/**
 * Created by philipwallenius on 28/07/2019.
 */
public class FileClassRepositoryImpl implements ClassRepository {

    @Override
    public JoopClass getClassByFullyQualifiedName(URI uri) {
        return null;
    }
}
