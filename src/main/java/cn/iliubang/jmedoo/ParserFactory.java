package cn.iliubang.jmedoo;


import cn.iliubang.jmedoo.parser.AndParser;
import cn.iliubang.jmedoo.parser.ColumnParser;
import cn.iliubang.jmedoo.parser.JoinParser;
import cn.iliubang.jmedoo.parser.LimitParser;
import cn.iliubang.jmedoo.parser.OrParser;
import cn.iliubang.jmedoo.parser.OrderParser;
import cn.iliubang.jmedoo.parser.ParserInterface;
import cn.iliubang.jmedoo.parser.WhereParser;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 21:03 $
 * @see
 */
public class ParserFactory {
    private static final ParserInterface whereParser = new WhereParser();
    private static final ParserInterface andParser = new AndParser();
    private static final ParserInterface orParser = new OrParser();
    private static final ParserInterface orderParser = new OrderParser();
    private static final ParserInterface limitParser = new LimitParser();
    private static final ParserInterface joinParser = new JoinParser();
    private static final ParserInterface columnParser = new ColumnParser();

    public static ParserInterface getColumnParser() {
        return columnParser;
    }

    public static ParserInterface getWhereParser() {
        return whereParser;
    }

    public static ParserInterface getAndParser() {
        return andParser;
    }

    public static ParserInterface getOrParser() {
        return orParser;
    }

    public static ParserInterface getOrderParser() {
        return orderParser;
    }

    public static ParserInterface getLimitParser() {
        return limitParser;
    }

    public static ParserInterface getJoinParser() {
        return joinParser;
    }
}
