package cn.iliubang.jmedoo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {Insert class description here}
 *
 * @author <a href="mailto:it.liubang@gmail.com">liubang</a>
 * @version $Revision: {Version} $ $Date: 2018/5/30 17:29 $
 * @see
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
    long PRIMARY = 1 << 0;
    long AUTO_INCREMENT = 1 << 1;

    long value() default PRIMARY | AUTO_INCREMENT;
}