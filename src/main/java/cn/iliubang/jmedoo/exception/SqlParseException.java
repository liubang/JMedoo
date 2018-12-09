package cn.iliubang.jmedoo.exception;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/29 23:01 $
 */
public class SqlParseException extends RuntimeException {
    public SqlParseException() {
        super();
    }

    public SqlParseException(String message) {
        super(message);
    }
}
