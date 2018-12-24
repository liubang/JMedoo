package cn.iliubang.jmedoo.test.entity;

import cn.iliubang.jmedoo.annotation.Id;
import cn.iliubang.jmedoo.annotation.Table;
import lombok.Data;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018-12-24 15:59 $
 */
@Data
@Table(value = "user")
public class User {
    @Id
    private long uid;
    private String uname;
    private String desc;
}
