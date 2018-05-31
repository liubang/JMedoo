package cn.iliubang.jmedoo.test;

import cn.iliubang.jmedoo.SqlBuilder;
import cn.iliubang.jmedoo.entity.Query;
import com.alibaba.fastjson.JSON;
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
    }

    @Test
    public void testSelect1() throws Exception {
        String select = readFile("/select.json");
        String column = readFile("/column.json");
        String join = readFile("/join.json");
        Query query = JSON.parseObject(select, Query.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> joinTable = (Map<String, Object>)JSON.parseObject(join, Map.class);
        @SuppressWarnings("unchecked")
        List<Object> col = (List<Object>)JSON.parseObject(column, ArrayList.class);
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildSelect("test", joinTable, col, query);
        System.out.println(sqlObjects);
    }

    @Test
    public void testCount() throws Exception {
        String select = readFile("/select.json");
        Query query = JSON.parseObject(select, Query.class);
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildCount("tableA", query);
        System.out.println(sqlObjects);
    }

    @Test
    public void testCount1() throws Exception {
        String select = readFile("/select.json");
        String column = readFile("/column.json");
        String join = readFile("/join.json");
        Query query = JSON.parseObject(select, Query.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> joinTable = (Map<String, Object>)JSON.parseObject(join, Map.class);
        @SuppressWarnings("unchecked")
        List<Object> col = (List<Object>)JSON.parseObject(column, ArrayList.class);
        SqlBuilder.SqlObjects sqlObjects = new SqlBuilder().buildCount("test", joinTable, col, query);
        System.out.println(sqlObjects);
    }
}
