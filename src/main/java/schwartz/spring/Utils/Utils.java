package schwartz.spring.Utils;

import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern patterValidEmail = Pattern.compile("^/[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-z]{2,}$");
    private static final Pattern patterValidCPF = Pattern.compile("");
    public static boolean isEmpty(final Object object) {
        return switch (object) {
            case null -> true;
            case String v -> v.replaceAll("\\s", "").isEmpty();
            case StringJoiner v -> v.length() == 0;
            case byte[] v -> v.length == 0;
            case short[] v -> v.length == 0;
            case int[] v -> v.length == 0;
            case long[] v -> v.length == 0;
            case float[] v -> v.length == 0;
            case double[] v -> v.length == 0;
            case boolean[] v -> v.length == 0;
            case char[] v -> v.length == 0;
            case Object[] v -> v.length == 0;
            case Collection<?> v -> v.isEmpty();
            case Map<?, ?> v -> v.isEmpty();
            default -> false;
        };
    }

    public static boolean validarEMAIL(String email) {
        return patterValidEmail.matcher(email).matches();
    }

    public static boolean validarCPF(String cpf) {
        // TODO Valicação de CPF com regex
        return false;
    }
}

