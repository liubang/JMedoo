package cn.iliubang.jmedoo.test;

import cn.iliubang.jmedoo.SqlBuilder;
import cn.iliubang.jmedoo.entity.Query;
import com.alibaba.fastjson.JSON;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:liubang@staff.weibo.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/31 11:10 $
 * @see
 */


public class TestBuilder {

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

    @Test
    public void testSelect() throws Exception {
        String select = readFile("/select.json");
        Query query = JSON.parseObject(select, Query.class);
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect("tableA", query);
        System.out.println(sqlObjects);
        Assert.assertEquals("SqlObjects{sql='SELECT * FROM \"table_a\" WHERE ((\"or12\" = ? OR \"or11\" < ?) AND ((\"or3and12\" = ? AND \"or3and11\" = ?) OR (\"or3and21\" = ? AND \"or3and22\" = ?)) AND (\"or21\" = ? OR \"or22\" = ?)) AND (\"and21\" != ? AND \"and22\" = ?) AND \"update_time\" > ? AND \"outdate_time\" BETWEEN (?,?) ORDER BY \"name\" ASC, \"id\" DESC LIMIT 12, 34', objects=[or12, or11, or3and12, or3and11, or3and21, or3and22, or21, or22, and21, and22, 2018-12-21 12:12:12, t1, t2]}", sqlObjects.toString());
    }

    @Test
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

        Assert.assertEquals("SqlObjects{sql='SELECT \"table_a\".\"column1\" AS \"tac1\", \"table_a\".\"column2\", \"table_b\".\"column1\" AS \"tbc1\", \"table_b\".\"column2\" AS \"tbc2\" FROM \"test\" INNER JOIN \"table_d\" USING (\"tdc1\") LEFT JOIN \"table_e\" ON \"table_d\".\"table_dc1\" = \"table_e\".\"table_ec1\" AND \"table_d\".\"table_dc2\" = \"table_e\".\"maste_ec2\" LEFT JOIN \"table_b\" USING (\"tbc1\") RIGHT JOIN \"table_a\" USING (\"tac1\") FULL JOIN \"table_c\" USING (\"tcc1\") WHERE ((\"or12\" = ? OR \"or11\" < ?) AND ((\"or3and12\" = ? AND \"or3and11\" = ?) OR (\"or3and21\" = ? AND \"or3and22\" = ?)) AND (\"or21\" = ? OR \"or22\" = ?)) AND (\"and21\" != ? AND \"and22\" = ?) AND \"update_time\" > ? AND \"outdate_time\" BETWEEN (?,?) ORDER BY \"name\" ASC, \"id\" DESC LIMIT 12, 34', objects=[or12, or11, or3and12, or3and11, or3and21, or3and22, or21, or22, and21, and22, 2018-12-21 12:12:12, t1, t2]}", sqlObjects.toString());
    }

    @Test
    public void testCount() throws Exception {
        String select = readFile("/select.json");
        Query query = JSON.parseObject(select, Query.class);
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildCount("tableA", query);
        System.out.println(sqlObjects);

        Assert.assertEquals("SqlObjects{sql='SELECT COUNT(*) FROM \"table_a\" WHERE ((\"or12\" = ? OR \"or11\" < ?) AND ((\"or3and12\" = ? AND \"or3and11\" = ?) OR (\"or3and21\" = ? AND \"or3and22\" = ?)) AND (\"or21\" = ? OR \"or22\" = ?)) AND (\"and21\" != ? AND \"and22\" = ?) AND \"update_time\" > ? AND \"outdate_time\" BETWEEN (?,?) ORDER BY \"name\" ASC, \"id\" DESC LIMIT 12, 34', objects=[or12, or11, or3and12, or3and11, or3and21, or3and22, or21, or22, and21, and22, 2018-12-21 12:12:12, t1, t2]}", sqlObjects.toString());
    }

    @Test
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

        Assert.assertEquals("SqlObjects{sql='SELECT COUNT(\"table_a\".\"column1\" AS \"tac1\", \"table_a\".\"column2\", \"table_b\".\"column1\" AS \"tbc1\", \"table_b\".\"column2\" AS \"tbc2\") FROM \"test\" INNER JOIN \"table_d\" USING (\"tdc1\") LEFT JOIN \"table_e\" ON \"table_d\".\"table_dc1\" = \"table_e\".\"table_ec1\" AND \"table_d\".\"table_dc2\" = \"table_e\".\"maste_ec2\" LEFT JOIN \"table_b\" USING (\"tbc1\") RIGHT JOIN \"table_a\" USING (\"tac1\") FULL JOIN \"table_c\" USING (\"tcc1\") WHERE ((\"or12\" = ? OR \"or11\" < ?) AND ((\"or3and12\" = ? AND \"or3and11\" = ?) OR (\"or3and21\" = ? AND \"or3and22\" = ?)) AND (\"or21\" = ? OR \"or22\" = ?)) AND (\"and21\" != ? AND \"and22\" = ?) AND \"update_time\" > ? AND \"outdate_time\" BETWEEN (?,?) ORDER BY \"name\" ASC, \"id\" DESC LIMIT 12, 34', objects=[or12, or11, or3and12, or3and11, or3and21, or3and22, or21, or22, and21, and22, 2018-12-21 12:12:12, t1, t2]}", sqlObjects.toString());
    }

    @Test
    public void testLimit() throws Exception {
        String limit = readFile("/limit.json");
        Query query = JSON.parseObject(limit, Query.class);
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect("tableA", query);
        Assert.assertEquals("SqlObjects{sql='SELECT * FROM \"table_a\" LIMIT 1, 20', objects=[]}", sqlObjects.toString());
    }

    @Test
    public void testLike() throws Exception {
        String like = readFile("/like.json");
        Query query = JSON.parseObject(like, Query.class);
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect("tableA", query);
        Assert.assertEquals("SqlObjects{sql='SELECT * FROM \"table_a\" WHERE \"aaa\" LIKE '%' ? '%' AND \"ccc\" NOT LIKE '%' ? '%'', objects=[bbb, ddd]}", sqlObjects.toString());
    }
}
