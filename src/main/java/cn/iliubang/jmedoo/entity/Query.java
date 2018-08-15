package cn.iliubang.jmedoo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 09:21 $
 * @see
 */
@Getter
@Setter
@ToString
public class Query {
    private Map<String, Object> where;
    private LinkedHashMap<String, Object> order;
    private List<Integer> limit;
}
