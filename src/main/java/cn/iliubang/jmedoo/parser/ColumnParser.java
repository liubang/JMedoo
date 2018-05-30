package cn.iliubang.jmedoo.parser;

import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 11:16 $
 * @see
 */
public class ColumnParser implements ParserInterface {

    @Override
    public String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) throws SqlParseException {
        if (null == lists || lists.isEmpty()) {
            return "SELECT * ";
        }
        StringBuilder sql = new StringBuilder("SELECT ");

        for (Object o : lists) {
            if (o instanceof String) {
                String sO = (String) o;
                int index = -1;
                if ((index = sO.indexOf('.')) > 0) {
                    String table = sO.substring(0, index);
                    String column = sO.substring(index + 1);
                    if (column.equals("*")) {
                        sql.append("\"").append(StringUtil.camel2Underline(table)).append("\".").append(column).append(", ");
                    } else {
                        sql.append("\"").append(StringUtil.camel2Underline(table)).append("\".\"").append(StringUtil.camel2Underline(column)).append("\", ");
                    }
                }
            }
        }

        if (sql.toString().trim().endsWith(",")) {
            sql.deleteCharAt(sql.lastIndexOf(","));
        }

        return sql.toString();
    }
}
