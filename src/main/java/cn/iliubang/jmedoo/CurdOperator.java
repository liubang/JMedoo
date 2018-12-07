package cn.iliubang.jmedoo;

import cn.iliubang.jmedoo.annotation.Table;
import cn.iliubang.jmedoo.entity.Query;
import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.sharding.ShardingStrategyInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/24 14:41 $
 */
public abstract class CurdOperator<T> {
    protected JdbcTemplate jdbcTemplateMaster;
    protected JdbcTemplate jdbcTemplateSlave;

    @SuppressWarnings("unchecked")
    private Class<T> entryClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(this.getClass(), CurdOperator.class);

    public CurdOperator(JdbcTemplate jdbcTemplateMaster, JdbcTemplate jdbcTemplateSlave) {
        this.jdbcTemplateMaster = jdbcTemplateMaster;
        this.jdbcTemplateSlave = jdbcTemplateSlave;
    }

    private static final Logger logger = LoggerFactory.getLogger(CurdOperator.class);

    public static String getTableName(Class<?> clazz) {
        return getTableName(clazz, null);
    }

    public static String getTableName(Class<?> clazz, Map<String, String> map) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new RuntimeException("Table annotation is not defined.");
        }

        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.value();

        String[] keys = table.keys();
        Class<? extends ShardingStrategyInterface> strageClazz = table.shardingStrategy();

        if (!strageClazz.isInterface()) {
            List<String> list = new ArrayList<>();
            if (keys.length > 0) {
                if (null == map || map.isEmpty()) {
                    throw new RuntimeException("Map should contain all keys of " + Arrays.toString(keys) + ", but null or empty given.");
                }
                for (String key : keys) {
                    if (map.containsKey(key)) {
                        list.add(map.get(key));
                    } else {
                        throw new RuntimeException("Map must contain all keys of " + Arrays.toString(keys));
                    }
                }
            }
            String[] strings = new String[list.size()];
            list.toArray(strings);
            try {
                return tableName + table.separator() + strageClazz.newInstance().sharding(strings);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return tableName;
        }
    }

    public long add(T type, Map<String, String> tMap) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildInsert(getTableName(entryClass, tMap), entryClass, type);

        logger.info(sqlObjects.toString());

        int res = jdbcTemplateMaster.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sqlObjects.getSql(), Statement.RETURN_GENERATED_KEYS);
            int index = 0;
            for (Object o : sqlObjects.getObjects()) {
                index++;
                preparedStatement.setObject(index, o);
            }
            return preparedStatement;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        } else {
            return res;
        }
    }

    public long add(T type) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildInsert(getTableName(entryClass), entryClass, type);

        logger.info(sqlObjects.toString());

        int res = jdbcTemplateMaster.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sqlObjects.getSql(), Statement.RETURN_GENERATED_KEYS);
            int index = 0;
            for (Object o : sqlObjects.getObjects()) {
                index++;
                preparedStatement.setObject(index, o);
            }
            return preparedStatement;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        } else {
            return res;
        }
    }

    public List select(Query query, Map<String, String> tMap) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect(getTableName(entryClass, tMap), query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateSlave.query(sqlObjects.getSql(), sqlObjects.getObjects(), new BeanPropertyRowMapper<T>());
    }

    public List select(Query query) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect(getTableName(entryClass), query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateSlave.query(sqlObjects.getSql(), sqlObjects.getObjects(), new BeanPropertyRowMapper<T>());
    }

    public T get(Query query, Map<String, String> tMap) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect(getTableName(entryClass, tMap), query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), new BeanPropertyRowMapper<>());
    }

    public T get(Query query) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect(getTableName(entryClass), query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), new BeanPropertyRowMapper<>());
    }

    public Long count(Query query, Map<String, String> tMap) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildCount(getTableName(entryClass, tMap), query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), Long.class);
    }

    public Long count(Query query) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildCount(getTableName(entryClass), query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), Long.class);
    }

    public int update(T type) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildUpdate(getTableName(entryClass), entryClass, type);
        logger.info(sqlObjects.toString());
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int update(T type, Map<String, String> tMap) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildUpdate(getTableName(entryClass, tMap), entryClass, type);
        logger.info(sqlObjects.toString());
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int update(String sql, Object[] objects) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder.SqlObjects();
        sqlObjects.setSql(sql);
        sqlObjects.setObjects(objects);
        logger.info(sqlObjects.toString());
        return jdbcTemplateMaster.update(sql, objects);
    }

    public int update(String table, Map<String, Object> updateData, Query query) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildUpdate(table, updateData, query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int delete(String table, Query query) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildDelete(table, query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int delete(Map<String, String> tMap, Query query) {
        return delete(getTableName(entryClass, tMap), query);
    }
}
