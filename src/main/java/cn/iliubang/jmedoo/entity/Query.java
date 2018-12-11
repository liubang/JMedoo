package cn.iliubang.jmedoo.entity;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 09:21 $
 */
@Data
public class Query {
    private LinkedHashMap<String, Object> where;
    private LinkedHashMap<String, Object> order;
    private List<Integer> limit;

    public static QueryBuilder builder() {
        return new QueryBuilder();
    }

    public static class QueryBuilder {
        private LinkedHashMap<String, Object> where;
        private LinkedHashMap<String, Object> order;
        private List<Integer> limit;

        public QueryBuilder where(LinkedHashMap<String, Object> where) {
            this.where = where;
            return this;
        }

        public QueryBuilder order(LinkedHashMap<String, Object> order) {
            this.order = order;
            return this;
        }

        public QueryBuilder limit(List<Integer> limit) {
            this.limit = limit;
            return this;
        }

        public Query build() {
            Query query = new Query();
            query.setWhere(where);
            query.setOrder(order);
            query.setLimit(limit);
            return query;
        }
    }
}
