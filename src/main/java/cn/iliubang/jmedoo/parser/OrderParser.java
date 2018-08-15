package cn.iliubang.jmedoo.parser;

import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 20:50 $
 * @see
 */
public class OrderParser implements ParserInterface {
    @Override
    public String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) throws SqlParseException {
        if (null == objectMap || objectMap.isEmpty()) {
            return "";
        }

        StringBuilder sql = new StringBuilder();

        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof String && (val.equals("ASC") || val.equals("DESC"))) {
                sql.append("\"").append(StringUtil.camel2Underline(entry.getKey())).append("\" ").append(val).append(", ");
            } else if (val instanceof List && !((List) val).isEmpty()) {
                sql.append("FIELD (\"" + StringUtil.camel2Underline(entry.getKey()) + "\", ");
                List l = (List) val;
                int s = l.size();
                int i = 0;
                for (Object v : l) {
                    lists.add(v);
                    i++;
                    if (i == s)
                        sql.append("?), ");
                    else
                        sql.append("?, ");
                }
            }
        }

        if (sql.length() > 0) {
            sql.deleteCharAt(sql.lastIndexOf(",")).insert(0, "ORDER BY ");
        }

        return sql.toString();
    }
}
