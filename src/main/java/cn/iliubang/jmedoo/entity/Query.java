package cn.iliubang.jmedoo.entity;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 09:21 $
 * @see
 */
@Data
public class Query {
    private LinkedHashMap<String, Object> where;
    private LinkedHashMap<String, Object> order;
    private List<Integer> limit;
}
