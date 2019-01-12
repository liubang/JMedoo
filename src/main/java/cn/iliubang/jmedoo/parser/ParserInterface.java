package cn.iliubang.jmedoo.parser;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 20:49 $
 */
public interface ParserInterface {
    String CHECK_KEY = "^[a-zA-Z0-9_\\.]+(\\[(\\>|\\<|\\<\\=|\\>\\=|~|!~|\\<\\>|!)\\])?$";
    String CHECK_COLUMN = "^[a-zA-Z0-9_\\.\\*]+(\\([a-zA-Z0-9_]+\\))?$";
    String CHECK_JOIN = "^\\[(\\>|\\<|\\>\\<|\\<\\>)\\][a-zA-Z0-9_]+$";
    String CHECK_ORDER = "^[a-zA-Z0-9_]+$";

    Pattern KEY_CHECK_PATTERN = Pattern.compile(CHECK_KEY);
    Pattern COLUMN_CHECK_PATTERN = Pattern.compile(CHECK_COLUMN);
    Pattern JOIN_CHECK_PATTERN = Pattern.compile(CHECK_JOIN);
    Pattern ORDER_CHECK_PATTERN = Pattern.compile(CHECK_ORDER);

    String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects);
}