package cn.iliubang.jmedoo.entity;

import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 09:21 $
 * @see
 */
public class Query {
    private Map<String, Object> where;

    private Map<String, Object> order;

    private List<Integer> limit;

    public Map<String, Object> getWhere() {
        return where;
    }

    public void setWhere(Map<String, Object> where) {
        this.where = where;
    }

    public Map<String, Object> getOrder() {
        return order;
    }

    public void setOrder(Map<String, Object> order) {
        this.order = order;
    }

    public List<Integer> getLimit() {
        return limit;
    }

    public void setLimit(List<Integer> limit) {
        this.limit = limit;
    }
}
