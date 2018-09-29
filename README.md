# JMedoo
Java版的Medoo查询表达式

[![Build Status](https://travis-ci.org/iliubang/JMedoo.svg?branch=master)](https://travis-ci.org/iliubang/JMedoo)

使用MySQL的话请设置

```java
# 命令行
set SQL_MODE=ANSI_QUOTES
# jdbc
jdbc:mysql://host:port/db?sessionVariables=sql_mode=ANSI_QUOTES
```


测试代码

column.

```json
[
  "tableA.column1(tac1)",
  "tableA.column2",
  "tableB.column1(tbc1)",
  "tableB.column2(tbc2)"
]
```
join table 

```json
{
  "[<]tableA": "tac1",
  "[>]tableB": "tbc1",
  "[<>]tableC": "tcc1",
  "[><]tableD": "tdc1",
  "[>]tableE":
    {
      "tableEc1": "masterc1",
      "tableEc2": "masterc2"
    }
}
```

query

```json
{
  "where": {
    "AND#1": {
      "OR#1": {
        "or11[<]": "or11",
        "or12": "or12"
      },
      "OR#2": {
        "or21": "or21",
        "or22": "or22"
      },
      "OR#3": {
        "AND#1": {
          "or3and11": "or3and11",
          "or3and12": "or3and12"
        },
        "AND#2": {
          "or3and21": "or3and21",
          "or3and22": "or3and22"
        }
      }
    },
    "AND#2": {
      "and21[!]": "and21",
      "and22": "and22"
    },
    "updateTime[>]": "2018-12-21 12:12:12",
    "outdateTime[<>]": [
      "t1", "t2"
    ]
  },
  "order": {
    "name": "ASC",
    "id": "DESC"
  },
  "limit": [
    12,
    34
  ]
}
```

公共方法

```java
private static String readFile(String file) throws Exception {
    InputStreamReader inputStreamReader = new InputStreamReader(TestBuilder.class.getResourceAsStream(file), "UTF-8");
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    String line;
    StringBuilder sb = new StringBuilder();
    while ((line = bufferedReader.readLine()) != null) {
        sb.append(line);
    }

    return sb.toString();
}
```

```java
public void testSelect() throws Exception {
    String select = readFile("/select.json");
    Query query = JSON.parseObject(select, Query.class);
    SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect("tableA", query);
    System.out.println(sqlObjects);
}
// 输出
// SqlObjects{sql='SELECT * FROM "table_a" WHERE (("or12" = ? OR "or11" < ?) AND (("or3and12" = ? AND "or3and11" = ?) OR ("or3and21" = ? AND "or3and22" = ?)) AND ("or21" = ? OR "or22" = ?)) AND ("and21" != ? AND "and22" = ?) AND "update_time" > ? AND "outdate_time" BETWEEN (?,?) ORDER BY "name" ASC, "id" DESC LIMIT 12,34', objects=[or12, or11, or3and12, or3and11, or3and21, or3and22, or21, or22, and21, and22, 2018-12-21 12:12:12, t1, t2]}
```

```java
public void testSelect1() throws Exception {
    String select = readFile("/select.json");
    String column = readFile("/column.json");
    String join = readFile("/join.json");
    Query query = JSON.parseObject(select, Query.class);
    @SuppressWarnings("unchecked")
    Map<String, Object> joinTable = (Map<String, Object>) JSON.parseObject(join, Map.class);
    @SuppressWarnings("unchecked")
    List<Object> col = (List<Object>) JSON.parseObject(column, ArrayList.class);
    SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect("test", joinTable, col, query);
    System.out.println(sqlObjects);
}
// 输出
// SqlObjects{sql='SELECT "table_a"."column1" AS "tac1", "table_a"."column2", "table_b"."column1" AS "tbc1", "table_b"."column2" AS "tbc2" FROM "test" INNER JOIN "table_d" USING ("tdc1") LEFT JOIN "table_e" ON "table_d"."table_dc1" = "table_e"."table_ec1" AND "table_d"."table_dc2" = "table_e"."maste_ec2" LEFT JOIN "table_b" USING ("tbc1") RIGHT JOIN "table_a" USING ("tac1") FULL JOIN "table_c" USING ("tcc1") WHERE (("or12" = ? OR "or11" < ?) AND (("or3and12" = ? AND "or3and11" = ?) OR ("or3and21" = ? AND "or3and22" = ?)) AND ("or21" = ? OR "or22" = ?)) AND ("and21" != ? AND "and22" = ?) AND "update_time" > ? AND "outdate_time" BETWEEN (?,?) ORDER BY "name" ASC, "id" DESC LIMIT 12,34', objects=[or12, or11, or3and12, or3and11, or3and21, or3and22, or21, or22, and21, and22, 2018-12-21 12:12:12, t1, t2]}
```

```java
public void testCount() throws Exception {
    String select = readFile("/select.json");
    Query query = JSON.parseObject(select, Query.class);
    SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildCount("tableA", query);
    System.out.println(sqlObjects);
}
// 输出
// SqlObjects{sql='SELECT COUNT(*) FROM "table_a" WHERE (("or12" = ? OR "or11" < ?) AND (("or3and12" = ? AND "or3and11" = ?) OR ("or3and21" = ? AND "or3and22" = ?)) AND ("or21" = ? OR "or22" = ?)) AND ("and21" != ? AND "and22" = ?) AND "update_time" > ? AND "outdate_time" BETWEEN (?,?) ORDER BY "name" ASC, "id" DESC LIMIT 12,34', objects=[or12, or11, or3and12, or3and11, or3and21, or3and22, or21, or22, and21, and22, 2018-12-21 12:12:12, t1, t2]}
```

```java
public void testCount1() throws Exception {
    String select = readFile("/select.json");
    String column = readFile("/column.json");
    String join = readFile("/join.json");
    Query query = JSON.parseObject(select, Query.class);
    @SuppressWarnings("unchecked")
    Map<String, Object> joinTable = (Map<String, Object>) JSON.parseObject(join, Map.class);
    @SuppressWarnings("unchecked")
    List<Object> col = (List<Object>) JSON.parseObject(column, ArrayList.class);
    SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildCount("test", joinTable, col, query);
    System.out.println(sqlObjects);
}
// 输出
// SqlObjects{sql='SELECT COUNT("table_a"."column1" AS "tac1", "table_a"."column2", "table_b"."column1" AS "tbc1", "table_b"."column2" AS "tbc2") FROM "test" INNER JOIN "table_d" USING ("tdc1") LEFT JOIN "table_e" ON "table_d"."table_dc1" = "table_e"."table_ec1" AND "table_d"."table_dc2" = "table_e"."maste_ec2" LEFT JOIN "table_b" USING ("tbc1") RIGHT JOIN "table_a" USING ("tac1") FULL JOIN "table_c" USING ("tcc1") WHERE (("or12" = ? OR "or11" < ?) AND (("or3and12" = ? AND "or3and11" = ?) OR ("or3and21" = ? AND "or3and22" = ?)) AND ("or21" = ? OR "or22" = ?)) AND ("and21" != ? AND "and22" = ?) AND "update_time" > ? AND "outdate_time" BETWEEN (?,?) ORDER BY "name" ASC, "id" DESC LIMIT 12,34', objects=[or12, or11, or3and12, or3and11, or3and21, or3and22, or21, or22, and21, and22, 2018-12-21 12:12:12, t1, t2]}
```
