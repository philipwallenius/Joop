package rocks.wallenius.joop.gui.util;

import rocks.wallenius.joop.gui.classdiagram.Constructor;
import rocks.wallenius.joop.gui.classdiagram.Field;
import rocks.wallenius.joop.gui.classdiagram.Method;
import rocks.wallenius.joop.gui.classdiagram.Parameter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipwallenius on 24/03/2018.
 */
public class ClassMemberMapperUtil {

    private ClassMemberMapperUtil() {}

    public static Field[] getFields(Class source) {
        List<Field> fields = new ArrayList<>();

        for(java.lang.reflect.Field field : source.getDeclaredFields()) {

            String modifiers = Modifier.toString(field.getModifiers());
            String type = field.getType().getSimpleName();
            fields.add(new Field(field.getName(), type, getAccessModifier(modifiers), isStatic(modifiers), isFinal(modifiers)));

        }

        return fields.toArray(new Field[fields.size()]);
    }

    public static Constructor[] getConstructors(Class source) {
        List<Constructor> constructors = new ArrayList<>();

        for(java.lang.reflect.Constructor constructor : source.getConstructors()) {
            String modifiers = Modifier.toString(constructor.getModifiers());
            constructors.add(new Constructor(constructor.getName(), getAccessModifier(modifiers), getParameters(constructor.getParameters())));
        }

        return constructors.toArray(new Constructor[constructors.size()]);
    }

    public static Method[] getMethods(Class source) {
        List<Method> methods = new ArrayList<>();

        for(java.lang.reflect.Method method : source.getDeclaredMethods()) {

            String modifiers = Modifier.toString(method.getModifiers());
            String returnType = method.getReturnType().getSimpleName();
            methods.add(new Method(method.getName(), returnType, getAccessModifier(modifiers), getParameters(method.getParameters()), isStatic(modifiers), isFinal(modifiers)));

        }

        return methods.toArray(new Method[methods.size()]);
    }

    private static String getAccessModifier(String modifiers) {
        if(modifiers.contains("public")) {
            return "public";
        } else if(modifiers.contains("private")) {
            return "private";
        } else if(modifiers.contains("protected")) {
            return "protected";
        } else {
            return "package";
        }
    }

    private static Parameter[] getParameters(java.lang.reflect.Parameter[] parameters) {
        List<Parameter> result = new ArrayList<>(parameters.length);

        for(java.lang.reflect.Parameter parameter : parameters) {
            result.add(new Parameter(parameter.getName(), parameter.getType().getSimpleName()));
        }

        return result.toArray(new Parameter[result.size()]);
    }

    private static boolean isFinal(String modifiers) {
        return modifiers.contains("final");
    }

    private static boolean isStatic(String modifiers) {
        return modifiers.contains("static");
    }

}
