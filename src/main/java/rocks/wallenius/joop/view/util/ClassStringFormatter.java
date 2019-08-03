package rocks.wallenius.joop.view.util;

import rocks.wallenius.joop.view.classdiagram.Parameter;

/**
 * Created by philipwallenius on 25/03/2018.
 */
public class ClassStringFormatter {

    private ClassStringFormatter() {}

    public static String formatParameters(Parameter[] parameters) {
        StringBuilder sb = new StringBuilder();

        int index = 0;

        for(Parameter parameter : parameters) {
            sb.append(parameter.getType().getSimpleName());
            sb.append(" " + parameter.getName());
            index++;
            if(index <= (parameters.length - 1)) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

}
