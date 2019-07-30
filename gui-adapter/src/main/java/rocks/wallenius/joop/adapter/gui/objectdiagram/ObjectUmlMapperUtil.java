package rocks.wallenius.joop.adapter.gui.objectdiagram;

import rocks.wallenius.joop.adapter.gui.objectdiagram.Method;
import rocks.wallenius.joop.adapter.gui.classdiagram.Parameter;
import rocks.wallenius.joop.adapter.gui.objectdiagram.Property;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipwallenius on 31/03/2018.
 */
public class ObjectUmlMapperUtil {

    private ObjectUmlMapperUtil() {}

    public static Property[] getProperties(Object source) {

        List<Property> properties = new ArrayList<>();

        for (Field field : source.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(source);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            properties.add(new Property(name, value));
        }

        return properties.toArray(new Property[properties.size()]);
    }

    public static Method[] getMethods(Object source) {
        List<Method> methods = new ArrayList<>();

        for (java.lang.reflect.Method method : source.getClass().getDeclaredMethods()) {
            String returnType = method.getReturnType().getSimpleName();
            methods.add(new Method(method.getName(), returnType, getParameters(method.getParameters())));
        }

        return methods.toArray(new Method[methods.size()]);
    }

    private static Parameter[] getParameters(java.lang.reflect.Parameter[] parameters) {
        List<Parameter> result = new ArrayList<>(parameters.length);

        for(java.lang.reflect.Parameter parameter : parameters) {
            result.add(new Parameter(parameter.getName(), parameter.getType()));
        }

        return result.toArray(new Parameter[result.size()]);
    }

}
