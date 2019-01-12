package cn.iliubang.jmedoo;


import cn.iliubang.jmedoo.parser.*;
import lombok.Getter;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 21:03 $
 */
public final class ParserFactory {
    private ParserFactory() { }

    @Getter
    private static final ParserInterface WHERE_PARSER = new WhereParser();
    @Getter
    private static final ParserInterface AND_PARSER = new AndParser();
    @Getter
    private static final ParserInterface OR_PARSER = new OrParser();
    @Getter
    private static final ParserInterface ORDER_PARSER = new OrderParser();
    @Getter
    private static final ParserInterface LIMIT_PARSER = new LimitParser();
    @Getter
    private static final ParserInterface JOIN_PARSER = new JoinParser();
    @Getter
    private static final ParserInterface COLUMN_PARSER = new ColumnParser();
}
