package cn.iliubang.jmedoo;

import cn.iliubang.jmedoo.annotation.Table;
import cn.iliubang.jmedoo.entity.Query;
import cn.iliubang.jmedoo.exception.SqlParseException;
import cn.iliubang.jmedoo.sharding.ShardingStrategyInterface;
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
public abstract class CurdOperator<T> {
    protected JdbcTemplate jdbcTemplateMaster;
    protected JdbcTemplate jdbcTemplateSlave;
    protected final Class<T> entryClass;

    @SuppressWarnings("unchecked")
    public CurdOperator(JdbcTemplate jdbcTemplateMaster, JdbcTemplate jdbcTemplateSlave) {
        this.jdbcTemplateMaster = jdbcTemplateMaster;
        this.jdbcTemplateSlave = jdbcTemplateSlave;
        entryClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), CurdOperator.class);
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
                    throw new RuntimeException("Map should contain all keys of " + Arrays.toString(keys)
                            + ", but null or empty given.");
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

    public long add(T type, Map<String, String> shardingKeys) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildInsert(getTableName(entryClass, shardingKeys),
                entryClass, type);

        logger.info(sqlObjects.toString());

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

<<<<<<< HEAD
    public long insertForUpdate(T type, Map<String, String> tMap) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildInsertForUpdate(getTableName(entryClass, tMap),
                entryClass, type);

        logger.info(sqlObjects.toString());

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

    public Optional<List<T>> select(Query query, Map<String, String> tMap) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect(getTableName(entryClass, tMap), query);
=======
    public Optional<List<T>> select(Query query, Map<String, String> shardingKeys) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect(getTableName(entryClass, shardingKeys), query);
>>>>>>> ec9a25e0c96b445c6cebc0f5790cfa607c572009
        logger.info(sqlObjects.toString());
        List<T> tList = jdbcTemplateSlave.query(sqlObjects.getSql(), sqlObjects.getObjects(),
                new BeanPropertyRowMapper<>(entryClass));
        if (null == tList || tList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(tList);
        }
    }

    public Optional<List<T>> select(Query query) throws SqlParseException {
        return select(query, null);
    }

    public Optional<T> get(Query query, Map<String, String> shardingKeys) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect(getTableName(entryClass, shardingKeys), query);
        logger.info(sqlObjects.toString());
        try {
            T t = jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(),
                    new BeanPropertyRowMapper<>(entryClass));
            return Optional.of(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public Optional<T> get(Query query) throws SqlParseException {
        return get(query, null);
    }

    public long count(Query query, Map<String, String> shardingKeys) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildCount(getTableName(entryClass, shardingKeys), query);
        logger.info(sqlObjects.toString());
        Long co = jdbcTemplateSlave.queryForObject(sqlObjects.getSql(), sqlObjects.getObjects(), Long.class);
        if (null == co) {
            return 0;
        } else {
            return co;
        }
    }

    public long count(Query query) throws SqlParseException {
        return count(query, null);
    }

    public int update(T type) {
        return update(type, null);
    }

    public int update(T type, Map<String, String> shardingKeys) {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildUpdate(getTableName(entryClass, shardingKeys),
                entryClass, type);
        logger.info(sqlObjects.toString());
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int update(Map<String, Object> updateData, Query query) throws SqlParseException {
        return update(updateData, query, null);
    }

    public int update(Map<String, Object> updateData, Query query, Map<String, String> shardingKeys) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildUpdate(getTableName(entryClass, shardingKeys), updateData, query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int delete(Query query, Map<String, String> shardingKeys) throws SqlParseException {
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildDelete(getTableName(entryClass, shardingKeys), query);
        logger.info(sqlObjects.toString());
        return jdbcTemplateMaster.update(sqlObjects.getSql(), sqlObjects.getObjects());
    }

    public int delete(Query query) {
        return delete(query, null);
    }
}
