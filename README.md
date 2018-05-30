# JMedoo
Java版的Medoo查询表达式

测试代码

```java

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
        SqlObjects objects = (new SqlBuilder()).buildSelect("test", joinTable, column, query);
        System.out.println(objects.getSql());
        System.out.println(Arrays.asList(objects.getObjects()));
    } catch (SqlParseException e) {
        e.printStackTrace();
    }
}
```

输出

```java
SELECT "post"."post_id", "post"."title", "account"."user_id" FROM "test" RIGHT JOIN "account" USING ("account_id") LEFT JOIN "user" ON "test"."t_name" = "user"."user_name" AND "test"."t_id" = "user"."user_id" FULL JOIN "in_user" USING ("i_id") INNER JOIN "o_user" USING ("o_id") WHERE (("starta" > ? AND "enda" < ?) OR ("startb" BETWEEN (?,?) AND "endb" != ?)) AND ("tableb"."endc" > ? OR "tablea"."startc" != ?) AND "class_id" IN (?,?) ORDER BY "id" DESC,"time" ASC LIMIT 10,234
[1, 2, 3, 4, 5, 7, 6, 1, 2]
```