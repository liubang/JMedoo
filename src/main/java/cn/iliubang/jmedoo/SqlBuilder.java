package cn.iliubang.jmedoo;

import cn.iliubang.jmedoo.annotation.Id;
import cn.iliubang.jmedoo.entity.Query;
import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.util.StringUtil;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;

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

    public enum QueryFunc {
        // COUNT()
        COUNT,
        // SUM()
        SUM,
        // MAX()
        MAX,
        // MIN
        MIN,
        // AVG
        AVG
    }

    private String buildQuery(@NonNull Query query, @NonNull SqlObjects sqlObjects) {
        StringBuilder sql = new StringBuilder();
        ArrayList<Object> lists = new ArrayList<>();
        sql.append(ParserFactory.WHERE_PARSER.parse(query.getWhere(), lists))
                .append(ParserFactory.GROUP_PARSER.parse(Collections.singletonMap("group", query.getGroup()), lists))
                .append(ParserFactory.HAVING_PARSER.parse(query.getHaving(), lists))
                .append(ParserFactory.ORDER_PARSER.parse(query.getOrder(), lists))
                .append(ParserFactory.LIMIT_PARSER.parse(Collections.singletonMap("limit", query.getLimit()), lists));
        sqlObjects.setObjects(lists.toArray());
        return sql.toString();
    }

    public SqlObjects buildSelect(String tableName, Query query) {
        StringBuilder sql = new StringBuilder();
        SqlObjects sqlObjects = new SqlObjects();
        sql.append("SELECT * FROM \"").append(StringUtil.camel2Underline(tableName)).append("\" ");
        if (null != query) {
            sql.append(buildQuery(query, sqlObjects));
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
        sql.append(ParserFactory.COLUMN_PARSER.parse(null, null, column.toArray())).append("FROM \"")
                .append(StringUtil.camel2Underline(tableName)).append("\" ");
        if (null != joinTable) {
            sql.append(ParserFactory.JOIN_PARSER.parse(joinTable, null, tableName));
        }
        if (null != query) {
            sql.append(buildQuery(query, sqlObjects));
        }
        sqlObjects.setSql(sql.toString().trim());

        return sqlObjects;
    }

    public SqlObjects buildFuncQuery(@NonNull QueryFunc queryFunc,
                                     @NonNull String tableName,
                                     Map<String, Object> joinTable,
                                     Pair<String, String> column,
                                     Query query) {
        StringBuilder sql = new StringBuilder();
        SqlObjects sqlObjects = new SqlObjects();
        if (null == column || (column.getLeft() == null)) {
            if (queryFunc == QueryFunc.COUNT) {
                sql.append("SELECT COUNT(*) FROM \"").append(StringUtil.camel2Underline(tableName)).append("\" ");
            } else {
                throw new SqlParseException(queryFunc.name() + " operation must specify the target column.");
            }
        } else {
            if (column.getRight() == null) {
                sql.append("SELECT " + queryFunc.name() + "(\"" + StringUtil.camel2Underline(column.getLeft()) + "\") FROM \"")
                        .append(StringUtil.camel2Underline(tableName)).append("\" ");
            } else {
                sql.append("SELECT " + queryFunc.name() + "(\"" + StringUtil.camel2Underline(column.getLeft()) + "\") AS \"" + column.getRight() + "\" FROM \"")
                        .append(StringUtil.camel2Underline(tableName)).append("\" ");
            }
        }

        if (null != joinTable) {
            sql.append(ParserFactory.JOIN_PARSER.parse(joinTable, null, tableName));
        }

        if (null != query) {
            sql.append(buildQuery(query, sqlObjects));
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
                if (field.isSynthetic()) {
                    continue;
                }
                // 排除自增ID
                if (!field.isAnnotationPresent(Id.class)
                        || (field.getAnnotation(Id.class).value() & Id.AUTO_INCREMENT) == 0) {
                    field.setAccessible(true);
                    Method method = tClass.getMethod("get" + StringUtil.ucfirst(field.getName()));
                    Object val = method.invoke(type);
                    // all field not null
                    if (null != val) {
                        sql.append("\"").append(StringUtil.camel2Underline(field.getName())).append("\", ");
                        list.add(val);
                    }
                    field.setAccessible(false);
                }
            }

            sql.deleteCharAt(sql.lastIndexOf(","));
            if (!list.isEmpty()) {
                sql.insert(0, "(").append(") VALUES (");
                for (int i = 0; i < list.size(); ++i) {
                    sql.append("?,");
                }
                sql.deleteCharAt(sql.lastIndexOf(",")).append(") ");
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
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
        LinkedHashMap<String, Object> where = new LinkedHashMap<>();
        Query query = Query.builder().where(where).build();
        boolean hasId = false;
        try {
            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isSynthetic()) {
                    continue;
                }
                field.setAccessible(true);
                Method method = tClass.getMethod("get" + StringUtil.ucfirst(field.getName()));
                Object val = method.invoke(type);
                field.setAccessible(false);
                // 主键不参与修改，而是作为约束条件
                if (!field.isAnnotationPresent(Id.class)
                        || (field.getAnnotation(Id.class).value() & Id.PRIMARY) == 0) {
                    if (null != val) {
                        // 所有字段都是NOT NULL 约束
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
                    .append(ParserFactory.WHERE_PARSER.parse(query.getWhere(), list));

        } catch (SqlParseException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return sql.toString();
    }

    public <T> SqlObjects buildInsertForUpdate(String tableName, Class<T> tClass, T type) {
        if (null == tableName || tableName.isEmpty() || null == tClass || null == type) {
            throw new SqlParseException("Sql parsing error: tableName: " + tableName + ", tClass: "
                    + tClass + ", type: " + type);
        }

        SqlObjects sqlObjects = new SqlObjects();
        List<Object> list = new ArrayList<>();
        sqlObjects.setSql("INSERT INTO \"" + StringUtil.camel2Underline(tableName) + "\" "
                + beanToInsertSql(tClass, type, list) + " ON DUPLICATE KEY UPDATE "
                + beanToInsertForUpdateSql(tClass, type, list));

        sqlObjects.setObjects(list.toArray());
        return sqlObjects;
    }

    private <T> String beanToInsertForUpdateSql(Class<T> tClass, T type, List<Object> list) {
        if (null == tClass || null == type) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        boolean hasId = false;
        try {
            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isSynthetic()) {
                    continue;
                }
                field.setAccessible(true);
                Method method = tClass.getMethod("get" + StringUtil.ucfirst(field.getName()));
                Object val = method.invoke(type);
                field.setAccessible(false);
                // 主键和UNIQUE不参与修改
                if (!field.isAnnotationPresent(Id.class)
                        || ((field.getAnnotation(Id.class).value() & Id.PRIMARY) == 0
                        && (field.getAnnotation(Id.class).value() & Id.UNIQUE) == 0)) {
                    // all fields not null
                    if (null != val) {
                        sql.append("\"").append(StringUtil.camel2Underline(field.getName())).append("\" = ?, ");
                        list.add(val);
                    }
                } else {
                    hasId = true;
                }
            }

            if (!hasId) {
                throw new SqlParseException("Sql parsing error: 'ON DUPLICATE KEY UPDATE' operation must have primary key or unique key.");
            }

            sql.deleteCharAt(sql.lastIndexOf(","));
        } catch (SqlParseException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
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
                mapToUpdateSql(map, list) + ParserFactory.WHERE_PARSER.parse(query.getWhere(), list);
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
                + ParserFactory.WHERE_PARSER.parse(query.getWhere(), list);
        sqlObjects.setSql(sql);
        sqlObjects.setObjects(list.toArray());

        return sqlObjects;
    }
}
