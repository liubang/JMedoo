package cn.iliubang.jmedoo.util;

import lombok.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 17:12 $
 */
public class StringUtil {
    private static final Pattern CAPITAL_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern WORD_PATTERN = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE);

    public static String camel2Underline(String param) {
        if (param == null || "".equals(param)) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = CAPITAL_PATTERN.matcher(param);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, "_"
                    + mc.group().toLowerCase());
            i++;
        }

        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    /**
     * uppercase first letter of string
     *
     * @param str
     * @return String
     */
    public static String ucfirst(@NonNull String str) {
        StringBuffer stringBuffer = new StringBuffer();
        Matcher m = WORD_PATTERN.matcher(str);

        while (m.find()) {
            m.appendReplacement(stringBuffer, m.group(1).toUpperCase() + m.group(2).toLowerCase());
        }

        return m.appendTail(stringBuffer).toString();
    }
}
