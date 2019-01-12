package cn.iliubang.jmedoo.parser;

import cn.iliubang.jmedoo.ParserFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2019-01-12 21:51 $
 */
public class HavingParser implements ParserInterface {

    @Override
    public String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) {
        if (null == objectMap || objectMap.isEmpty()) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        Map<String, Object> whereMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            if (entry.getKey().equals("AND") || entry.getKey().startsWith("AND#")) {
                Object oAnd = entry.getValue();
                if (oAnd instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> tAnd = (Map<String, Object>) entry.getValue();
                    sql.append("(").append(ParserFactory.AND_PARSER.parse(tAnd, lists)).append(") AND ");
                }
            } else if (entry.getKey().equals("OR") || entry.getKey().startsWith("OR#")) {
                Object oOr = entry.getValue();
                if (oOr instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> tOr = (Map<String, Object>) oOr;
                    sql.append("(").append(ParserFactory.OR_PARSER.parse(tOr, lists)).append(") AND ");
                }
            } else {
                whereMap.put(entry.getKey(), entry.getValue());
            }
        }

        if (!whereMap.isEmpty()) {
            sql.append(ParserFactory.AND_PARSER.parse(whereMap, lists));
        }

        if (sql.toString().trim().endsWith("AND")) {
            sql.delete(sql.lastIndexOf("AND"), sql.length());
        }

        if (sql.length() > 0) {
            sql.insert(0, "HAVING ").append(" ");
        }

        return sql.toString();
    }
}
