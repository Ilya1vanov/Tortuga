package gson;

import java.lang.annotation.*;

/**
 * @author Ilya Ivanov
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GSONExclude {
}
