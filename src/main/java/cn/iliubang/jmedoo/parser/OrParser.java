package cn.iliubang.jmedoo.parser;

import cn.iliubang.jmedoo.ParserFactory;
import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 20:50 $
 * @see
 */
public class OrParser implements ParserInterface {
    @Override
    public String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) throws SqlParseException {
        if (null == objectMap || objectMap.isEmpty()) {

            return "";
        }

        StringBuilder sql = new StringBuilder();
        Map<String, Object> orMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            if (entry.getKey().equals("AND") || entry.getKey().startsWith("AND#")) {
                Object oAnd = entry.getValue();
                if (oAnd instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> tAnd = (Map<String, Object>) entry.getValue();
                    sql.append("(").append(ParserFactory.getAndParser().parse(tAnd, lists)).append(") OR ");
                }
            } else if (entry.getKey().equals("OR") || entry.getKey().startsWith("OR#")) {
                Object oOr = entry.getValue();
                if (oOr instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> tOr = (Map<String, Object>) oOr;
                    sql.append("(").append(ParserFactory.getOrParser().parse(tOr, lists)).append(") OR ");
                }
            } else {
                orMap.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Object> entry : orMap.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            boolean isBetween = false;
            boolean isEndWith = key.endsWith("]");
            if (isEndWith) {
                int index = key.lastIndexOf("[");
                String op = key.substring(index);
                String realK = key.substring(0, index);

                int indexCa = -1;
                if ((indexCa = realK.indexOf(".")) > 0) {
                    String table = StringUtil.camel2Underline(realK.substring(0, indexCa));
                    realK = StringUtil.camel2Underline(realK.substring(indexCa + 1));
                    sql.append("\"").append(table).append("\".\"").append(realK);
                } else {
                    sql.append("\"").append(StringUtil.camel2Underline(realK));
                }

                if (op.equals("[>]")) {
                    sql.append("\" > ? OR ");
                } else if (op.equals("[<]")) {
                    sql.append("\" < ? OR ");
                } else if (op.equals("[<=]")) {
                    sql.append("\" <= ? OR ");
                } else if (op.equals("[>=]")) {
                    sql.append("\" >= ? OR ");
                } else if (op.equals("[!]")) {
                    sql.append("\" != ? OR ");
                } else if (op.equals("[<>]")) {
                    sql.append("\" BETWEEN ");
                    isBetween = true;
                } else {
                    throw new SqlParseException("Sql parsing error.");
                }
            } else {
                int indexCa = -1;
                if ((indexCa = key.indexOf(".")) > 0) {
                    String table = StringUtil.camel2Underline(key.substring(0, indexCa));
                    String realK = StringUtil.camel2Underline(key.substring(indexCa + 1));
                    sql.append("\"").append(table).append("\".\"").append(realK);
                } else {
                    sql.append("\"").append(StringUtil.camel2Underline(key));
                }
            }

            if (isBetween && !(val instanceof List)) {
                throw new SqlParseException("Sql parsing error.");
            }

            if (val instanceof List) {
                if (isBetween) {
                    if (((List) val).size() > 2)
                        throw new SqlParseException("Sql parsing error.");
                    sql.append("(");
                } else {
                    sql.append("\" IN (");
                }

                for (Object o : (List) val) {
                    sql.append("?,");
                    lists.add(o);
                }
                sql.deleteCharAt(sql.lastIndexOf(",")).append(") OR ");
            } else if (!isEndWith) {
                sql.append("\" = ? OR ");
                lists.add(val);
            } else {
                lists.add(val);
            }
        }

        if (sql.lastIndexOf(" OR ") >= 0) {
            return sql.delete(sql.lastIndexOf(" OR "), sql.length()).toString();
        } else {
            return sql.toString();
        }
    }
}
