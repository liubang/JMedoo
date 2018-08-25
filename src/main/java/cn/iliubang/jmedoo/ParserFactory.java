package cn.iliubang.jmedoo;


import cn.iliubang.jmedoo.parser.AndParser;
import cn.iliubang.jmedoo.parser.ColumnParser;
import cn.iliubang.jmedoo.parser.JoinParser;
import cn.iliubang.jmedoo.parser.LimitParser;
import cn.iliubang.jmedoo.parser.OrParser;
import cn.iliubang.jmedoo.parser.OrderParser;
import cn.iliubang.jmedoo.parser.ParserInterface;
import cn.iliubang.jmedoo.parser.WhereParser;
import lombok.Getter;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 21:03 $
 * @see
 */
public class ParserFactory {
    @Getter
    private static final ParserInterface whereParser = new WhereParser();
    @Getter
    private static final ParserInterface andParser = new AndParser();
    @Getter
    private static final ParserInterface orParser = new OrParser();
    @Getter
    private static final ParserInterface orderParser = new OrderParser();
    @Getter
    private static final ParserInterface limitParser = new LimitParser();
    @Getter
    private static final ParserInterface joinParser = new JoinParser();
    @Getter
    private static final ParserInterface columnParser = new ColumnParser();
}
