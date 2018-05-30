package cn.iliubang.jmedoo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 17:12 $
 * @see
 */
public class StringUtil {
    public static String camel2Underline(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
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
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String ucfirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
