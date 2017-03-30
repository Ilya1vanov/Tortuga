package properties;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConversionException;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class PropertiesSetterTest {
    static class SpecialTestClass {
        boolean boolProp;
        Integer intProp;
        String strProp;
        String propWithOutSetter;

        public String getPropWithOutSetter() {
            return propWithOutSetter;
        }

        public boolean isBoolProp() {
            return boolProp;
        }

        public void setBoolProp(boolean boolProp) {
            this.boolProp = boolProp;
        }

        public Integer getIntProp() {
            return intProp;
        }

        public void setIntProp(Integer intProp) {
            this.intProp = intProp;
        }

        public String getStrProp() {
            return strProp;
        }

        public void setStrProp(String strProp) {
            this.strProp = strProp;
        }
    }

    private static final String VALID_PROPERTY_KEY = "StrProp";
    private static final String VALID_PROPERTY = "New StrProp";

    private static final String VALID_BOOLEAN_PROPERTY_KEY = "boolProp";
    private static final Boolean VALID_BOOLEAN_PROPERTY = true;

    private static final String INVALID_PROPERTY_KEY = "asdasd";
    private static final String ANY_PROPERTY = VALID_PROPERTY;

    private static final String NO_SETTER_PROPERTY_KEY = "propWithOutSetter";

    private static final String VALID_KEY_FOR_INVALID_PROPERTY = "intProp";
    private static final String INVALID_PROPERTY = "Invalid";

    private static final Class<SpecialTestClass> VALID_TARGET_CLASS = SpecialTestClass.class;
    private static final Class<PropertiesSetterTest> INVALID_TARGET_CLASS = PropertiesSetterTest.class;

    private Configuration config;
    private SpecialTestClass target;

    @Before
    public void setUp() {
        config = new PropertiesConfiguration();
        target = mock(VALID_TARGET_CLASS);
        config.addProperty(VALID_PROPERTY_KEY, VALID_PROPERTY);
        config.addProperty(VALID_BOOLEAN_PROPERTY_KEY, VALID_BOOLEAN_PROPERTY);
    }

    @Test
    public void shouldSetProperties() throws Exception {
        PropertiesSetter.setProperties(target, target.getClass(), config);
        verify(target).setStrProp(VALID_PROPERTY);
    }

    @Test
    public void shouldSetBooleanProperties() throws Exception {
        PropertiesSetter.setProperties(target, target.getClass(), config);
        verify(target).setBoolProp(VALID_BOOLEAN_PROPERTY);
    }

    @Test(expected = NoSuchMethodException.class)
    public void shouldTrowNSMExceptionForLackOfGetter() throws Exception {
        config.addProperty(INVALID_PROPERTY_KEY, ANY_PROPERTY);
        PropertiesSetter.setProperties(target, target.getClass(), config);
    }

    @Test(expected = NoSuchMethodException.class)
    public void shouldTrowNSMExceptionForLackOfSetter() throws Exception {
        config.addProperty(NO_SETTER_PROPERTY_KEY, ANY_PROPERTY);
        PropertiesSetter.setProperties(target, target.getClass(), config);
    }

    @Test(expected = ClassCastException.class)
    public void shouldTrowCCEForTargetIsNotInstanceOfClazz() throws Exception {
        PropertiesSetter.setProperties(target, INVALID_TARGET_CLASS, config);
    }

    @Test(expected = ConversionException.class)
    public void shouldTrowCEForPropertyInvalidValue() throws Exception {
        config.addProperty(VALID_KEY_FOR_INVALID_PROPERTY, INVALID_PROPERTY);
        PropertiesSetter.setProperties(target, target.getClass(), config);
    }
}