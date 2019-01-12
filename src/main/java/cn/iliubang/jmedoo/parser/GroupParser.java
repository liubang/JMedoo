package cn.iliubang.jmedoo.parser;

import cn.iliubang.jmedoo.ParserFactory;

import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2019-01-12 21:30 $
 */
public class GroupParser implements ParserInterface {
    @Override
    public String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) {
        if (null == objectMap || objectMap.isEmpty()) {
            return "";
        }
        @SuppressWarnings("unchecked")
        List<String> column = (List<String>)objectMap.get("group");

        if (null == column || column.isEmpty()) {
            return "";
        }

        return "GROUP BY " + ParserFactory.COLUMN_PARSER.parse(null, null, column.toArray());
    }
}
