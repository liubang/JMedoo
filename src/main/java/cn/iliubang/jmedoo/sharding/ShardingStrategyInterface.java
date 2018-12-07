package cn.iliubang.jmedoo.sharding;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/12/7 18:51 $
 * @see
 */
public interface ShardingStrategyInterface {
    public String sharding(String... args);
}
