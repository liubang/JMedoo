package cn.iliubang.jmedoo;

import cn.iliubang.jmedoo.annotation.Id;
import cn.iliubang.jmedoo.entity.Query;
import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.util.StringUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 20:48 $
 * @see
 */
public class SqlBuilder {

    public static class SqlObjects implements Serializable {
        private String sql;
        private Object[] objects;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public Object[] getObjects() {
            return objects;
        }

        public void setObjects(Object[] objects) {
            this.objects = objects;
        }

        @Override
        public String toString() {
            return "SqlObjects{" +
                    "sql='" + sql + '\'' +
                    ", objects=" + Arrays.toString(objects) +
                    '}';
        }
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

    public SqlObjects buildSelect(String tableName, Map<String, Object> joinTable, List<Object> column, Query query) {
        StringBuilder sql = new StringBuilder("SELECT ");
        SqlObjects sqlObjects = new SqlObjects();
        sql.append(ParserFactory.getColumnParser().parse(null, column)).append("FROM \"").append(StringUtil.camel2Underline(tableName)).append("\" ");
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

    public SqlObjects buildCount(String tableName, Map<String, Object> joinTable, List<Object> column, Query query) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(");
        SqlObjects sqlObjects = new SqlObjects();
        sql.append(ParserFactory.getColumnParser().parse(null, column).trim()).append(") FROM \"").append(StringUtil.camel2Underline(tableName)).append("\" ");
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
                if (!field.isAnnotationPresent(Id.class) || (field.getAnnotation(Id.class).value() & Id.AUTO_INCREMENT) == 0) {
                    field.setAccessible(true);
                    Method method = tClass.getMethod("get" + StringUtil.ucfirst(field.getName()));
                    Object val = method.invoke(type);
                    sql.append("\"").append(StringUtil.camel2Underline(field.getName())).append("\", ");
                    list.add(val);
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
            throw new SqlParseException("Sql parsing error.");
        }

        SqlObjects sqlObjects = new SqlObjects();
        List<Object> list = new ArrayList<>();
        sqlObjects.setObjects(list.toArray());
        sqlObjects.setSql("INSERT INTO \"" + StringUtil.camel2Underline(tableName) + "\" " + beanToInsertSql(tClass, type, list));
        return sqlObjects;
    }

    private <T> String beanToUpdateSql(Class<T> tClass, T type, List<Object> list) {
        if (null == tClass || null == type) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        Query query = new Query();
        try {
            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Method method = tClass.getMethod("get" + StringUtil.ucfirst(field.getName()));
                Object val = method.invoke(type);
                field.setAccessible(false);
                // 主键不参与修改，而是作为约束条件
                if (!field.isAnnotationPresent(Id.class) || (field.getAnnotation(Id.class).value() & Id.PRIMARY) == 0) {
                    sql.append("\"").append(StringUtil.camel2Underline(field.getName())).append("\" = ?, ");
                    list.add(val);
                } else {
                    query.setWhere(new HashMap<String, Object>() {
                        {
                            put(StringUtil.camel2Underline(field.getName()), val);
                        }
                    });
                }
            }

            sql.deleteCharAt(sql.lastIndexOf(",")).append(ParserFactory.getWhereParser().parse(query.getWhere(), list));

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
        sqlObjects.setSql("UPDATE \"" + StringUtil.camel2Underline(tableName) + "\" SET " + beanToUpdateSql(tClass, type, list));

        sqlObjects.setObjects(list.toArray());
        return sqlObjects;
    }

    public SqlObjects buildUpdate(String tableName, Map<String, Object> map, Query query) {
        if (null == tableName || null == map || null == query || null == query.getWhere()) {
            throw new SqlParseException("Sql parsing error.");
        }

        SqlObjects sqlObjects = new SqlObjects();
        List<Object> list = new ArrayList<>();
        String sql = "UPDATE \"" + StringUtil.camel2Underline(tableName) + "\" SET " +
                mapToUpdateSql(map, list) + ParserFactory.getWhereParser().parse(query.getWhere(), list);
        sqlObjects.setSql(sql);
        sqlObjects.setObjects(list.toArray());

        return sqlObjects;
    }

}
