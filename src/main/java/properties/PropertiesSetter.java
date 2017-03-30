package properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConversionException;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * <p>Provide static methods to automatically set object's properties.
 * More info provided in {@link PropertiesSetter#setProperties setProperties().}</p>
 * Created by Илья on 26.03.2017.
 */
public class PropertiesSetter {
    /** default logger */
    private static final Logger log = Logger.getLogger(PropertiesSetter.class);

    /**
     * Set object's properties according to provided config. Target object must
     * have setProperty or isProperty (in case of boolean) and setProperty methods.
     * @param target target object
     * @param clazz class of the target object
     * @param config configurations
     * @throws NoSuchMethodException if a getter or setter is not found or if the name is "&lt;init&gt;"or "&lt;clinit&gt;".
     * @throws ClassCastException if target is not instance of class represented with {@code clazz}
     * @throws ConversionException if the value is not compatible with the setter argument type
     */
    public static void setProperties(Object target, Class<?> clazz, Configuration config)
            throws NoSuchMethodException, ConversionException, ClassCastException {
        if (!target.getClass().isInstance(clazz) && !target.getClass().equals(clazz))
            throw new ClassCastException(target.getClass() + " class is not instance of " + clazz);

        Iterator<String> keys = config.getKeys();
        while(keys.hasNext()){
            String key = keys.next();
            String propertyName = key.substring(0, 1).toUpperCase() + key.substring(1);
            try {
                Method getter;
                try {
                    getter = clazz.getMethod("get" + propertyName);
                } catch (NoSuchMethodException e) {
                    // if a boolean property
                    getter = clazz.getMethod("is" + propertyName);
                }

                Class<?> getterReturnType = getter.getReturnType();

                Method setter = clazz.getMethod("set" + propertyName, getterReturnType);
                setter.invoke(target, config.get(getterReturnType, key));
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
