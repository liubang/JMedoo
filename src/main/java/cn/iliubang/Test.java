package cn.iliubang;

import cn.iliubang.jmedoo.SqlBuilder;
import cn.iliubang.jmedoo.entity.Query;
import cn.iliubang.jmedoo.exception.SqlParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 17:32 $
 * @see
 */
public class Test {
    public static void main(String[] args) {
        Query query = new Query();
        query.setWhere(new HashMap<String, Object>() {
            {
                put("OR#1", new HashMap<String, Object>() {
                    {
                        put("AND#1", new HashMap<String, Object>() {
                            {
                                put("starta[>]", 1);
                                put("enda[<]", 2);
                            }
                        });
                        put("AND#2", new HashMap<String, Object>() {
                            {
                                put("startb[<>]", new ArrayList<Integer>() {
                                    {
                                        add(3);
                                        add(4);
                                    }
                                });
                                put("endb[!]", 5);
                            }
                        });
                    }
                });
                put("OR#2", new HashMap<String, Object>() {
                    {
                        put("tablea.startc[!]", 6);
                        put("tableb.endc[>]", 7);
                    }
                });
                put("classId", new ArrayList<Integer>() {
                    {
                        add(1);
                        add(2);
                    }
                });
            }
        });
        query.setOrder(new HashMap<String, Object>() {
            {
                put("id", "DESC");
                put("time", "ASC");
            }
        });
        query.setLimit(new ArrayList<Integer>(2) {
            {
                add(10);
                add(234);
            }
        });

        try {
            Map<String, Object> joinTable = new LinkedHashMap<String, Object>() {
                {
                    put("[<]account", "accountId");
                    put("[>]user", new HashMap<String, String>() {
                        {
                            put("tId", "userId");
                            put("tName", "userName");
                        }
                    });
                    put("[<>]inUser", "iId");
                    put("[><]oUser", "oId");
                }
            };
            List<Object> column = new ArrayList<Object>() {
                {
                    add("post.postId");
                    add("post.title");
                    add("account.userId");
                }
            };
            SqlBuilder.SqlObjects objects = (new SqlBuilder()).buildSelect("test", joinTable, column, query);
            System.out.println(objects.getSql());
            System.out.println(Arrays.asList(objects.getObjects()));
        } catch (SqlParseException e) {
            e.printStackTrace();
        }
    }
}
