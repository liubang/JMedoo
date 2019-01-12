package cn.iliubang.jmedoo;

import cn.iliubang.jmedoo.annotation.Table;
import cn.iliubang.jmedoo.entity.Query;
import cn.iliubang.jmedoo.sharding.ShardingStrategyInterface;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

/**
 * @author <a href="mailto:it.liubang.gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/24 14:41 $
 */
public abstract class AbstractCurdOperator<T> {
    protected JdbcTemplate jdbcTemplateMaster;
    protected JdbcTemplate jdbcTemplateSlave;
    protected final Class<T> entryClass;

    @SuppressWarnings("unchecked")
    public AbstractCurdOperator(JdbcTemplate jdbcTemplateMaster, JdbcTemplate jdbcTemplateSlave) {
        this.jdbcTemplateMaster = jdbcTemplateMaster;
        this.jdbcTemplateSlave = jdbcTemplateSlave;
        entryClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), AbstractCurdOperator.class);
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractCurdOperator.class);

    public static String getTableName(Class<?> clazz) {
        return getTableName(clazz, null);
    }

    public static String getTableName(Class<?> clazz, Map<String, Object> map) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalStateException("Table annotation is not defined.");
        }

        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.value();

        String[] keys = table.keys();
        Class<? extends ShardingStrategyInterface> strageClazz = table.shardingStrategy();

        if (!strageClazz.isInterface()) {
            Map<String, Object> filtedKeyMap = new HashMap<>();
            if (keys.length > 0) {
                if (null == map || map.isEmpty()) {
                    throw new IllegalArgumentException("Map should contain all keys of " + Arrays.toString(keys)
                            + ", but null or empty given.");
                }
                for (String key : keys) {
                    if (map.containsKey(key)) {
                        filtedKeyMap.put(key, map.get(key));
                    } else {
                        throw new IllegalArgumentException("Map must contain all keys of " + Arrays.toString(keys));
                    }
                }
            }
            try {
                return tableName + table.separator() + strageClazz.newInstance().sharding(filtedKeyMap);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        } else {
            return tableName;
        }
    }

    public long add(T type, Map<String, Object> shardingKeys) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildInsert(getTableName(entryClass, shardingKeys),
                entryClass, type);

        logger.info("{}", sqlObjects);

        int res = jdbcTemplateMaster.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sqlObjects.getSql(),
                    Statement.RETURN_GENERATED_KEYS);
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
        return add(type, null);
    }

    public long insertForUpdate(T type, Map<String, Object> shardingKeys) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildInsertForUpdate(getTableName(entryClass, shardingKeys),
                entryClass, type);

        logger.info("{}", sqlObjects);

        int res = jdbcTemplateMaster.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sqlObjects.getSql(),
                    Statement.RETURN_GENERATED_KEYS);
            int index = 0;
            for (Object o : sqlObjects.getObjects()) {
                index++;
                preparedStatement.setObject(index, o);
            }
            return preparedStatement;
        }, keyHolder);

        if (res == 1) {
            if (keyHolder.getKey() != null) {
                return keyHolder.getKey().longValue();
            }
        }

        return res;
    }

    public long insertForUpdate(T type) {
        return insertForUpdate(type, null);
    }


    public Optional<List<T>> select(Query query, Map<String, Object> shardingKeys) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect(getTableName(entryClass, shardingKeys), query);
        logger.info("{}", sqlObjects);
        List<T> tList = jdbcTemplateSlave.query(sqlObjects.getSql(), sqlObjects.getObjects(),
                new BeanPropertyRowMapper<>(entryClass));
        if (null == tList || tList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(tList);
        }
    }

    public Optional<List<T>> select(Query query) {
        return select(query, null);
    }

    public Optional<T> get(Query query, Map<String, Object> shardingKeys) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect(getTableName(entryClass, shardingKeys), query);
        logger.info("{}", sqlObjects);
        try {
            T t = jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(),
                    new BeanPropertyRowMapper<>(entryClass));
            return Optional.of(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public Optional<T> get(Query query) {
        return get(query, null);
    }

    public long count(Map<String, Object> joinTable, String column, Query query, Map<String, Object> shardingKeys) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildFuncQuery(SqlBuilder.QueryFunc.COUNT,
                getTableName(entryClass, shardingKeys), joinTable, column, query);
        logger.info("{}", sqlObjects);
        Long c = jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), Long.class);
        if (null == c) {
            return 0;
        } else {
            return c;
        }
    }

    public long count(Query query) {
        return count(null, null, query, null);
    }

    public long count(Query query, Map<String, Object> shardingKeys) {
        return count(null, null, query, shardingKeys);
    }

    public long count(String column, Query query, Map<String, Object> shardingKeys) {
        return count(null, column, query, shardingKeys);
    }

    public <S> S max(Map<String, Object> joinTable,
                     @NonNull String column, Query query, Map<String, Object> shardingKeys, Class<S> sClass) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildFuncQuery(SqlBuilder.QueryFunc.MAX,
                getTableName(entryClass, shardingKeys), joinTable, column, query);
        logger.info("{}", sqlObjects);
        return jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), sClass);
    }

    public <S> S max(String column, Query query, Map<String, Object> shardingKeys, Class<S> sClass) {
        return max(null, column, query, shardingKeys, sClass);
    }

    public <S> S max(String column, Map<String, Object> shardingKeys, Class<S> sClass) {
        return max(null, column, null, shardingKeys, sClass);
    }

    public <S> S max(String column, Class<S> sClass) {
        return max(null, column, null, null, sClass);
    }

    public <S> S min(Map<String, Object> joinTable,
                     @NonNull String column, Query query, Map<String, Object> shardingKeys, Class<S> sClass) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildFuncQuery(SqlBuilder.QueryFunc.MIN,
                getTableName(entryClass, shardingKeys), joinTable, column, query);
        logger.info("{}", sqlObjects);
        return jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), sClass);
    }

    public <S> S min(String column, Query query, Map<String, Object> shardingKeys, Class<S> sClass) {
        return min(null, column, query, shardingKeys, sClass);
    }

    public <S> S min(String column, Map<String, Object> shardingKeys, Class<S> sClass) {
        return min(null, column, null, shardingKeys, sClass);
    }

    public <S> S min(String column, Class<S> sClass) {
        return min(null, column, null, null, sClass);
    }

    public double avg(Map<String, Object> joinTable,
                      @NonNull String column, Query query, Map<String, Object> shardingKeys) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildFuncQuery(SqlBuilder.QueryFunc.AVG,
                getTableName(entryClass, shardingKeys), joinTable, column, query);
        logger.info("{}", sqlObjects);
        Double d = jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), Double.class);
        if (null == d) {
            return 0.0;
        } else {
            return d;
        }
    }

    public double avg(String column, Query query, Map<String, Object> shardingKeys) {
        return avg(null, column, query, shardingKeys);
    }

    public double avg(String column, Map<String, Object> shardingKeys) {
        return avg(null, column, null, shardingKeys);
    }

    public double avg(String column) {
        return avg(null, column, null, null);
    }

    public long sum(Map<String, Object> joinTable,
                      @NonNull String column, Query query, Map<String, Object> shardingKeys) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildFuncQuery(SqlBuilder.QueryFunc.SUM,
                getTableName(entryClass, shardingKeys), joinTable, column, query);
        logger.info("{}", sqlObjects);
        Long d = jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), Long.class);
        if (null == d) {
            return 0;
        } else {
            return d;
        }
    }

    public long sum(String column, Query query, Map<String, Object> shardingKeys) {
        return sum(null, column, query, shardingKeys);
    }

    public long sum(String column, Map<String, Object> shardingKeys) {
        return sum(null, column, null, shardingKeys);
    }

    public long sum(String column) {
        return sum(null, column, null, null);
    }

    public int update(T type) {
        return update(type, null);
    }

    public int update(T type, Map<String, Object> shardingKeys) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildUpdate(getTableName(entryClass, shardingKeys),
                entryClass, type);
        logger.info("{}", sqlObjects);
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int update(Map<String, Object> updateData, Query query) {
        return update(updateData, query, null);
    }

    public int update(Map<String, Object> updateData, Query query, Map<String, Object> shardingKeys) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildUpdate(getTableName(entryClass, shardingKeys), updateData, query);
        logger.info("{}", sqlObjects);
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int delete(Query query, Map<String, Object> shardingKeys) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildDelete(getTableName(entryClass, shardingKeys), query);
        logger.info("{}", sqlObjects);
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int delete(Query query) {
        return delete(query, null);
    }
}
