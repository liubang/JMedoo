package cn.iliubang.jmedoo;

import cn.iliubang.jmedoo.annotation.Id;
import cn.iliubang.jmedoo.entity.Query;
import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.util.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 20:48 $
 */
public class SqlBuilder {

    @Data
    public static class SqlObjects implements Serializable {
        private String sql;
        private Object[] objects;
    }

    public SqlObjects buildSelect(String tableName, Query query) throws SqlParseException {
        StringBuilder sql = new StringBuilder();
        SqlObjects sqlObjects = new SqlObjects();
        sql.append("SELECT * FROM \"").append(StringUtil.camel2Underline(tableName)).append("\" ");
        if (null != query) {
            ArrayList<Object> lists = new ArrayList<>();
            sql.append(ParserFactory.getWhereParser().parse(query.getWhere(), lists))
                    .append(ParserFactory.getOrderParser().parse(query.getOrder(), lists))
                    .append(ParserFactory.getLimitParser().parse(new HashMap<String, Object>(1) {
                        {
                            put("limit", query.getLimit());
                        }
                    }, lists));
            sqlObjects.setObjects(lists.toArray());
        }

        sqlObjects.setSql(sql.toString().trim());

        return sqlObjects;
    }

    public SqlObjects buildSelect(String tableName,
                                  Map<String, Object> joinTable,
                                  List<Object> column,
                                  Query query) {
        StringBuilder sql = new StringBuilder("SELECT ");
        SqlObjects sqlObjects = new SqlObjects();
        sql.append(ParserFactory.getColumnParser().parse(null, column)).append("FROM \"")
                .append(StringUtil.camel2Underline(tableName)).append("\" ");
        if (null != joinTable) {
            sql.append(ParserFactory.getJoinParser().parse(joinTable, null, tableName));
        }
        if (null != query) {
            ArrayList<Object> lists = new ArrayList<>();
            sql.append(ParserFactory.getWhereParser().parse(query.getWhere(), lists))
                    .append(ParserFactory.getOrderParser().parse(query.getOrder(), lists))
                    .append(ParserFactory.getLimitParser().parse(new HashMap<String, Object>(1) {
                        {
                            put("limit", query.getLimit());
                        }
                    }, lists));
            sqlObjects.setObjects(lists.toArray());
        }
        sqlObjects.setSql(sql.toString().trim());

        return sqlObjects;
    }

    public SqlObjects buildCount(String tableName, Query query) throws SqlParseException {
        StringBuilder sql = new StringBuilder();
        SqlObjects sqlObjects = new SqlObjects();
        sql.append("SELECT COUNT(*) FROM \"").append(StringUtil.camel2Underline(tableName)).append("\" ");
        if (null != query) {
            ArrayList<Object> lists = new ArrayList<>();
            sql.append(ParserFactory.getWhereParser().parse(query.getWhere(), lists))
                    .append(ParserFactory.getOrderParser().parse(query.getOrder(), lists))
                    .append(ParserFactory.getLimitParser().parse(new HashMap<String, Object>(1) {
                        {
                            put("limit", query.getLimit());
                        }
                    }, lists));
            sqlObjects.setObjects(lists.toArray());
        }

        sqlObjects.setSql(sql.toString().trim());

        return sqlObjects;
    }

    public SqlObjects buildCount(String tableName,
                                 Map<String, Object> joinTable,
                                 List<Object> column,
                                 Query query) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(");
        SqlObjects sqlObjects = new SqlObjects();
        sql.append(ParserFactory.getColumnParser().parse(null, column).trim()).append(") FROM \"")
                .append(StringUtil.camel2Underline(tableName)).append("\" ");
        if (null != joinTable) {
            sql.append(ParserFactory.getJoinParser().parse(joinTable, null, tableName));
        }
        if (null != query) {
            ArrayList<Object> lists = new ArrayList<>();
            sql.append(ParserFactory.getWhereParser().parse(query.getWhere(), lists))
                    .append(ParserFactory.getOrderParser().parse(query.getOrder(), lists))
                    .append(ParserFactory.getLimitParser().parse(new HashMap<String, Object>(1) {
                        {
                            put("limit", query.getLimit());
                        }
                    }, lists));
            sqlObjects.setObjects(lists.toArray());
        }
        sqlObjects.setSql(sql.toString().trim());

        return sqlObjects;
    }

    private <T> String beanToInsertSql(Class<T> tClass, T type, List<Object> list) {
        if (null == tClass || null == type) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        try {
            Field[] fields = tClass.getDeclaredFields();

            for (Field field : fields) {
                // 排除自增ID
                if (!field.isAnnotationPresent(Id.class)
                        || (field.getAnnotation(Id.class).value() & Id.AUTO_INCREMENT) == 0) {
                    field.setAccessible(true);
                    Method method = tClass.getMethod("get" + StringUtil.ucfirst(field.getName()));
                    Object val = method.invoke(type);
                    if (null != val) { // 所有字段都是NOT NULL 约束
                        sql.append("\"").append(StringUtil.camel2Underline(field.getName())).append("\", ");
                        list.add(val);
                    }
                    field.setAccessible(false);
                }
            }

            sql.deleteCharAt(sql.lastIndexOf(","));
            if (list.size() > 0) {
                sql.insert(0, "(").append(") VALUES (");
                for (int i = 0; i < list.size(); ++i) {
                    sql.append("?,");
                }
                sql.deleteCharAt(sql.lastIndexOf(",")).append(") ");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return sql.toString();
    }

    public <T> SqlObjects buildInsert(String tableName, Class<T> tClass, T type) {
        if (null == tableName || tableName.isEmpty() || null == tClass || null == type) {
            throw new SqlParseException("Sql parsing error: tableName: "
                    + tableName + ", tClass: " + tClass + ", type: " + type);
        }

        SqlObjects sqlObjects = new SqlObjects();
        List<Object> list = new ArrayList<>();
        sqlObjects.setSql("INSERT INTO \"" + StringUtil.camel2Underline(tableName) + "\" "
                + beanToInsertSql(tClass, type, list));
        sqlObjects.setObjects(list.toArray());
        return sqlObjects;
    }

    private <T> String beanToUpdateSql(Class<T> tClass, T type, List<Object> list) {
        if (null == tClass || null == type) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        Query query = new Query();
        LinkedHashMap<String, Object> where = new LinkedHashMap<>();
        query.setWhere(where);
        boolean hasId = false;
        try {
            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Method method = tClass.getMethod("get" + StringUtil.ucfirst(field.getName()));
                Object val = method.invoke(type);
                field.setAccessible(false);
                // 主键不参与修改，而是作为约束条件
                if (!field.isAnnotationPresent(Id.class)
                        || (field.getAnnotation(Id.class).value() & Id.PRIMARY) == 0) {
                    if (null != val) { // 所有字段都是NOT NULL 约束
                        sql.append("\"").append(StringUtil.camel2Underline(field.getName())).append("\" = ?, ");
                        list.add(val);
                    }
                } else {
                    hasId = true;
                    where.put(StringUtil.camel2Underline(field.getName()), val);
                }
            }

            if (!hasId) {
                throw new SqlParseException("Sql parsing error: update operation must set where condition.");
            }
            sql.deleteCharAt(sql.lastIndexOf(","))
                    .append(ParserFactory.getWhereParser().parse(query.getWhere(), list));

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return sql.toString();
    }

    private String mapToUpdateSql(Map<String, Object> map, List<Object> list) {
        StringBuilder sql = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sql.append("\"").append(StringUtil.camel2Underline(entry.getKey())).append("\" = ? ,");
            list.add(entry.getValue());
        }

        return sql.deleteCharAt(sql.lastIndexOf(",")).toString();
    }

    public <T> SqlObjects buildUpdate(String tableName, Class<T> tClass, T type) {
        SqlObjects sqlObjects = new SqlObjects();
        List<Object> list = new ArrayList<>();
        sqlObjects.setSql("UPDATE \"" + StringUtil.camel2Underline(tableName) + "\" SET "
                + beanToUpdateSql(tClass, type, list));

        sqlObjects.setObjects(list.toArray());
        return sqlObjects;
    }

    public SqlObjects buildUpdate(String tableName, Map<String, Object> map, Query query) {
        if (null == tableName || null == map || null == query) {
            throw new SqlParseException("Sql parsing error: tableName: " + tableName + ", map: "
                    + map + ", query: " + query);
        }

        if (null == query.getWhere() || query.getWhere().isEmpty()) {
            throw new SqlParseException("Update operation must set where condition.");
        }

        SqlObjects sqlObjects = new SqlObjects();
        List<Object> list = new ArrayList<>();
        String sql = "UPDATE \"" + StringUtil.camel2Underline(tableName) + "\" SET " +
                mapToUpdateSql(map, list) + ParserFactory.getWhereParser().parse(query.getWhere(), list);
        sqlObjects.setSql(sql);
        sqlObjects.setObjects(list.toArray());

        return sqlObjects;
    }

    public SqlObjects buildDelete(String tableName, Query query) {
        if (null == tableName || null == query) {
            throw new SqlParseException("Sql parsing error: delete operation must set where condition.");
        }

        if (null == query.getWhere() || query.getWhere().isEmpty()) {
            throw new SqlParseException("Delete operation must set where condition.");
        }

        SqlObjects sqlObjects = new SqlObjects();
        List<Object> list = new ArrayList<>();
        String sql = "DELETE FROM \"" + StringUtil.camel2Underline(tableName) + "\" "
                + ParserFactory.getWhereParser().parse(query.getWhere(), list);
        sqlObjects.setSql(sql);
        sqlObjects.setObjects(list.toArray());

        return sqlObjects;
    }
}
