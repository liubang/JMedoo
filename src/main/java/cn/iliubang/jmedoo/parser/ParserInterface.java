package cn.iliubang.jmedoo.parser;

import cn.iliubang.jmedoo.exception.SqlParseException;

import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 20:49 $
 * @see
 */
public interface ParserInterface {
    String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) throws SqlParseException;
}
