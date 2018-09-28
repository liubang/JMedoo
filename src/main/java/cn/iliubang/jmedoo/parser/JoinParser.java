package cn.iliubang.jmedoo.parser;

import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 10:08 $
 * @see
 */
public class JoinParser implements ParserInterface {

    // [>] == LEFT JOIN
    // [<] == RIGHT JOIN
    // [<>] == FULL JOIN
    // [><] == INNER JOIN
    @Override
    public String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) throws SqlParseException {
        if (null == objectMap || objectMap.isEmpty()) {
            return "";
        }

        StringBuilder sql = new StringBuilder();

        String primaryTable = null;

        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            String key = entry.getKey();
            if (!joinTestPattern.matcher(key).matches()) {
                throw new SqlParseException("Sql parsing error: bad join table (" + key + ")");
            }
            Object val = entry.getValue();
            if (key.startsWith("[")) {
                String op = key.substring(0, key.lastIndexOf("]") + 1);
                String realTable = StringUtil.camel2Underline(key.substring(key.lastIndexOf("]") + 1));

                if (op.equals("[>]")) {
                    sql.append("LEFT JOIN ");
                } else if (op.equals("[<]")) {
                    sql.append("RIGHT JOIN ");
                } else if (op.equals("[><]")) {
                    sql.append("INNER JOIN ");
                } else if (op.equals("[<>]")) {
                    sql.append("FULL JOIN ");
                } else {
                    throw new SqlParseException("Sql parsing error: " + objectMap);
                }
                sql.append("\"").append(realTable).append("\" ");
                if (val instanceof String) {
                    sql.append("USING (\"").append(StringUtil.camel2Underline((String) val)).append("\") ");
                } else if (val instanceof Map) {
                    if (null == objects || objects.length == 0) {
                        throw new SqlParseException("Sql parsing error: " + objectMap);
                    }
                    if (null == primaryTable) {
                        primaryTable = StringUtil.camel2Underline((String) objects[0]);
                    }
                    @SuppressWarnings("unchecked")
                    Map<String, String> mapVal = (Map<String, String>) val;
                    sql.append("ON ");
                    for (Map.Entry<String, String> entry1 : mapVal.entrySet()) {
                        String k1 = entry1.getKey();
                        String k2 = entry1.getValue();
                        sql.append("\"").append(primaryTable).append("\".\"").append(StringUtil.camel2Underline(k1))
                                .append("\" = \"").append(realTable).append("\".\"")
                                .append(StringUtil.camel2Underline(k2)).append("\" AND ");
                    }

                    if (sql.toString().trim().endsWith("AND")) {
                        sql.delete(sql.lastIndexOf("AND"), sql.length());
                    }
                } else {
                    throw new SqlParseException("Sql parsing error: " + objectMap);
                }
                primaryTable = realTable;
            }
        }

        return sql.toString();
    }
}
