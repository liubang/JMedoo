package cn.iliubang.jmedoo.sharding;

import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/12/7 18:51 $
 */
public interface ShardingStrategyInterface {
    /**
     * sharding method
     *
     * @param shardingKeys key-values needed by sharding.
     * @return String
     */
    String sharding(Map<String, Object> shardingKeys);
}
