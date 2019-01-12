package cn.iliubang.jmedoo;


import cn.iliubang.jmedoo.parser.*;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 21:03 $
 */
public final class ParserFactory {
    private ParserFactory() {
    }

    public static final ParserInterface WHERE_PARSER = new WhereParser();
    public static final ParserInterface AND_PARSER = new AndParser();
    public static final ParserInterface OR_PARSER = new OrParser();
    public static final ParserInterface ORDER_PARSER = new OrderParser();
    public static final ParserInterface LIMIT_PARSER = new LimitParser();
    public static final ParserInterface JOIN_PARSER = new JoinParser();
    public static final ParserInterface COLUMN_PARSER = new ColumnParser();
    public static final ParserInterface GROUP_PARSER = new GroupParser();
    public static final ParserInterface HAVING_PARSER = new HavingParser();
}
