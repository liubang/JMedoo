package cn.iliubang.jmedoo.parser;

import cn.iliubang.jmedoo.exception.SqlParseException;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 20:49 $
 * @see
 */
public interface ParserInterface {
    String testKey = "^[a-zA-Z0-9_]+(\\[(\\>|\\<|\\<\\=|\\>\\=|~|!~|\\<\\>|!)\\])?$";
    String testColumn = "^[a-zA-Z0-9_\\.\\*]+(\\([a-zA-Z0-9_]+\\))?$";
    String testJoin = "^\\[(\\>|\\<|\\>\\<|\\<\\>)\\][a-zA-Z0-9_]+$";
    String testOrder = "^[a-zA-Z0-9_]+$";

    Pattern keyTestPattern = Pattern.compile(testKey);
    Pattern columnTestPattern = Pattern.compile(testColumn);
    Pattern joinTestPattern = Pattern.compile(testJoin);
    Pattern orderTestPattern = Pattern.compile(testOrder);

    String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) throws SqlParseException;
}