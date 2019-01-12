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
 */
public class OrParser implements ParserInterface {
    @Override
    public String parse(Map<String, Object> objectMap, List<Object> lists, Object... objects) {
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
                    sql.append("(").append(ParserFactory.getAND_PARSER().parse(tAnd, lists)).append(") OR ");
                }
            } else if (entry.getKey().equals("OR") || entry.getKey().startsWith("OR#")) {
                Object oOr = entry.getValue();
                if (oOr instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> tOr = (Map<String, Object>) oOr;
                    sql.append("(").append(ParserFactory.getOR_PARSER().parse(tOr, lists)).append(") OR ");
                }
            } else {
                orMap.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Object> entry : orMap.entrySet()) {
            String key = entry.getKey();
            if (!KEY_CHECK_PATTERN.matcher(key).matches()) {
                throw new SqlParseException("Sql parsing error: bad column (" + key + ")");
            }
            Object val = entry.getValue();
            String realK;
            boolean isBetween = false;
            boolean isLike = false;
            boolean isNot = false;
            boolean isEndWith = key.endsWith("]");
            if (isEndWith) {
                int index = key.lastIndexOf("[");
                String op = key.substring(index);
                realK = key.substring(0, index);

                int indexCa = -1;
                if ((indexCa = realK.indexOf(".")) > 0) {
                    String table = StringUtil.camel2Underline(realK.substring(0, indexCa));
                    realK = StringUtil.camel2Underline(realK.substring(indexCa + 1));
                    realK = "\"" + table + "\".\"" + realK;
                } else {
                    realK = "\"" + StringUtil.camel2Underline(realK) + "\"";
                }
                sql.append(realK);

                if (op.equals("[>]")) {
                    sql.append(" > ? OR ");
                } else if (op.equals("[<]")) {
                    sql.append(" < ? OR ");
                } else if (op.equals("[<=]")) {
                    sql.append(" <= ? OR ");
                } else if (op.equals("[>=]")) {
                    sql.append(" >= ? OR ");
                } else if (op.equals("[!]")) {
                    isNot = true;
                } else if (op.equals("[!~]")) {
                    sql.append("\" NOT LIKE '%' ? '%' OR ");
                } else if (op.equals("[<>]")) {
                    sql.append(" BETWEEN ");
                    isBetween = true;
                } else if (op.equals("[~]")) {
                    sql.append(" LIKE ");
                    isLike = true;
                } else {
                    throw new SqlParseException("Sql parsing error: " + objectMap);
                }
            } else {
                int indexCa = -1;
                if ((indexCa = key.indexOf(".")) > 0) {
                    String table = StringUtil.camel2Underline(key.substring(0, indexCa));
                    realK = StringUtil.camel2Underline(key.substring(indexCa + 1));
                    realK = "\"" + table + "\".\"" + realK + "\"";
                } else {
                    realK = "\"" + StringUtil.camel2Underline(key) + "\"";
                }
                sql.append(realK);
            }

            if (isBetween && !(val instanceof List)) {
                throw new SqlParseException("Sql parsing error: " + objectMap);
            }

            if (val instanceof List) {

                if (isNot) {
                    sql.append(" NOT");
                }

                if (isLike) {
                    for (Object o : (List) val) {
                        sql.append("'%' ? '%', OR ").append(realK).append(" LIKE ");
                        lists.add(o);
                    }
                    sql.delete(sql.lastIndexOf(","), sql.length()).append(" OR ");
                } else {
                    if (isBetween) {
                        if (((List) val).size() > 2)
                            throw new SqlParseException("Sql parsing error: " + objectMap);

                        sql.append("(");
                    } else {
                        sql.append(" IN (");
                    }

                    for (Object o : (List) val) {
                        sql.append("?,");
                        lists.add(o);
                    }
                    sql.deleteCharAt(sql.lastIndexOf(",")).append(") OR ");
                }
            } else if (isLike) {
                sql.append("%?% OR ");
                lists.add(val);
            } else if (isNot) {
                sql.append(" != ? OR ");
                lists.add(val);
            } else if (!isEndWith) {
                sql.append(" = ? OR ");
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
