package cn.iliubang.jmedoo.parser;

import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 11:16 $
 */
public class ColumnParser implements ParserInterface {

    @Override
    public String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) throws SqlParseException {
        if (null == lists || lists.isEmpty()) {
            return "* ";
        }
        StringBuilder sql = new StringBuilder();

        for (Object o : lists) {
            if (o instanceof String) {
                String sO = (String) o;
                if (!columnTestPattern.matcher(sO).matches()) {
                    throw new SqlParseException("Sql parsing error: bad column (" + sO + ")");
                }
                int index = -1;
                if ((index = sO.indexOf('.')) > 0) {
                    String table = sO.substring(0, index);
                    String column = sO.substring(index + 1);
                    sql.append("\"").append(StringUtil.camel2Underline(table));
                    if (column.equals("*")) {
                        sql.append("\".").append(column).append(", ");
                    } else {
                        sql.append("\".\"");
                        if (column.indexOf("(") > 0 && column.endsWith(")")) {
                            String alias = column.substring(column.indexOf("(") + 1, column.length() - 1);
                            String ccolumn = column.substring(0, column.indexOf("("));

                            sql.append(StringUtil.camel2Underline(ccolumn)).append("\" AS \"").append(alias).append("\", ");

                        } else {
                            sql.append(StringUtil.camel2Underline(column)).append("\", ");
                        }
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
