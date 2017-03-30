package properties;

import junitparams.JUnitParamsRunner;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.Before;

import java.io.InputStream;
import java.util.LinkedHashMap;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class PropertiesLoaderTest {
    InputStream validPropInp;

    private static final String INVALID_NAME_OF_ELEMENT = "invalid";
    private static final String VALID_NAME_OF_ELEMENT = "types";
    private static final String FIRST_ELEMENT = "documents";
    private static final String SECOND_ELEMENT = "books";
    private static final int NUMBER_OF_PROPERTIES = 2;

    @Before
    public void setUp() {
        validXMLInp = IOUtils.toInputStream("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "\n" +
                "<stages>\n" +
                "    <" + VALID_ELEMENT_NAME + " " + VALID_ATTRIBUTE + "=\"" + FIRST_XML_ELEMENT + "\">\n" +
                "        <title>Welcome!</title>\n" +
                "        <resizable>false</resizable>\n" +
                "    </" + VALID_ELEMENT_NAME + ">\n" +
                "\n" +
                "    <" + VALID_ELEMENT_NAME + " " + VALID_ATTRIBUTE + "=\"" + SECOND_XML_ELEMENT + "\">\n" +
                "        <resizable>true</resizable>\n" +
                "    </" + VALID_ELEMENT_NAME + ">\n" +
                "</stages>");

        validPropInp = IOUtils.toInputStream(
                VALID_NAME_OF_ELEMENT + " = " +
                FIRST_ELEMENT + ", " + SECOND_ELEMENT/* + ", " + THIRD_ELEMENT + ", " + FOURTH_ELEMENT*/);
    }

    @After
    public void tearDown() throws Exception {
        validPropInp.close();
        validXMLInp.close();
    }

    @Test(expected = org.apache.commons.configuration2.plist.ParseException.class)
    public void loadPropertiesShouldThrowPEForInvalidNameOfElement()
            throws Exception {
        PropertiesLoader.loadProperties(validPropInp, INVALID_NAME_OF_ELEMENT);
    }

    @Test
    public void loadPropertiesShouldReturnEmptyMapOnEmptyElementsArray()
            throws Exception {
        final LinkedHashMap<String, Configuration> map = PropertiesLoader.loadProperties(IOUtils.toInputStream("types = "), VALID_NAME_OF_ELEMENT);
        assertThat("Map is not empty, but array of elements is", 0, is(map.size()));
    }

    @Test
    public void loadPropertiesShouldReturnValidMap() throws Exception {
        final LinkedHashMap<String, Configuration> map = PropertiesLoader.loadProperties(validPropInp, VALID_NAME_OF_ELEMENT);
        assertThat(
                "Returned map contains wrong number of elements.\n" +
                        "Expected: " + NUMBER_OF_PROPERTIES + "\n" +
                        "Actual: " + map.size(),
                NUMBER_OF_PROPERTIES, is(map.size()));
        assertThat("Returned map doesn't contain element " + FIRST_ELEMENT + " according to the test context" , map.keySet(), hasItem(FIRST_ELEMENT));
        assertThat("Returned map doesn't contain element " + SECOND_ELEMENT + " according to the test context", map.keySet(), hasItem(SECOND_ELEMENT));
    }

    InputStream validXMLInp;

    private static final String VALID_ATTRIBUTE = "name";
    private static final String VALID_ELEMENT_NAME = "stage";
    private static final String INVALID_ATTRIBUTE = INVALID_NAME_OF_ELEMENT;
    private static final String INVALID_ELEMENT_NAME = INVALID_ATTRIBUTE;
    private static final String FIRST_XML_ELEMENT = "login";
    private static final String SECOND_XML_ELEMENT = "main";
    private static final int NUMBER_OF_XML_ELEMENTS = 2;

    @Test(expected = org.apache.commons.configuration2.plist.ParseException.class)
    public void loadXMLConfigurationShouldThrowPEForInvalidAttribute()
            throws Exception {
        PropertiesLoader.loadXMLConfiguration(validXMLInp, INVALID_ATTRIBUTE, VALID_ELEMENT_NAME);
    }

    @Ignore
    @Deprecated
    @Test(expected = org.apache.commons.configuration2.plist.ParseException.class)
    public void loadXMLConfigurationShouldThrowPEForInvalidElementName()
            throws Exception {
        PropertiesLoader.loadXMLConfiguration(validXMLInp, VALID_ATTRIBUTE, INVALID_ELEMENT_NAME);
    }

    @Test
    public void loadXMLConfigurationShouldReturnEmptyMapForInvalidElementName()
            throws Exception {
        final LinkedHashMap<String, Configuration> map = PropertiesLoader.loadXMLConfiguration(validXMLInp, VALID_ATTRIBUTE, INVALID_ELEMENT_NAME);
        assertThat("Return map is not empty, but elementName was invalid", 0, is(map.size()));
    }

    @Test
    public void loadXMLConfigurationShouldReturnValidMap()
            throws Exception {
        final LinkedHashMap<String, Configuration> map = PropertiesLoader.loadXMLConfiguration(validXMLInp, VALID_ATTRIBUTE, VALID_ELEMENT_NAME);
        assertThat(
                "Returned map contains wrong number of elements.\n" +
                        "Expected: " + NUMBER_OF_XML_ELEMENTS + "\n" +
                        "Actual: " + map.size(),
                NUMBER_OF_XML_ELEMENTS, is(map.size()));
        assertThat("Returned map doesn't contain element " + FIRST_XML_ELEMENT + " according to the test context" , map.keySet(), hasItem(FIRST_XML_ELEMENT));
        assertThat("Returned map doesn't contain element " + SECOND_XML_ELEMENT + " according to the test context", map.keySet(), hasItem(SECOND_XML_ELEMENT));
    }
}