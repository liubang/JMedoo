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
}
