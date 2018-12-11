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
 */
public interface ParserInterface {
    String checkKey = "^[a-zA-Z0-9_\\.]+(\\[(\\>|\\<|\\<\\=|\\>\\=|~|!~|\\<\\>|!)\\])?$";
    String checkColumn = "^[a-zA-Z0-9_\\.\\*]+(\\([a-zA-Z0-9_]+\\))?$";
    String checkJoin = "^\\[(\\>|\\<|\\>\\<|\\<\\>)\\][a-zA-Z0-9_]+$";
    String checkOrder = "^[a-zA-Z0-9_]+$";

    Pattern keyCheckPattern = Pattern.compile(checkKey);
    Pattern columnCheckPattern = Pattern.compile(checkColumn);
    Pattern joinCheckPattern = Pattern.compile(checkJoin);
    Pattern orderCheckPattern = Pattern.compile(checkOrder);

    String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) throws SqlParseException;
}