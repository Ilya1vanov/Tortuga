package properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.plist.ParseException;
import org.apache.commons.configuration2.tree.ImmutableNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Provide loading properties from XML of .properties files. Files should have
 * declared structure to be processed. More information provided in {@link #loadProperties loadProperties()}
 * and {@link #loadXMLConfiguration loadXMLConfiguration().}
 */
public class PropertiesLoader {
    /**
     * <p>Load properties from the given .properties file. Manage file input stream and pass control to
     * {@link #loadProperties(InputStream, String) loadProperties()}. Stream closes automatically.</p>
     * @see #loadProperties(InputStream, String) loadProperties()
     */
    public static LinkedHashMap<String, Configuration> loadProperties(File file, String nameOfElementsArray)
            throws ParseException, ConfigurationException, IOException {
        try (InputStream in = new FileInputStream(file)) {
            return loadProperties(in, nameOfElementsArray);
        }
    }

    /**
     * <p>
     * Loads configurations from given .properties file.<br>
     *     Supports the following file structure:
     *    </p>
     * <pre>
     * nameOfElementsArray = name1, name2, ...
     *
     * name1.property1=value1
     * name1.property2=value2
     *
     * name2.property1=...
     * </pre>
     * Returns map, that contain values of nameAttribute as a key and flat map of properties
     * implemented by {@link Configuration} class as a value.
     * <p>Properties, that are not specified in {@code elementsArray} are ignored. If element name is repeated, than map will
     * contains only last element.</p>
     * @param in input stream to read from
     * @param nameOfElementsArray name of property that reflects element's names. This names parses as a prefix.
     * @return map, that contain values of nameAttribute as a key and flat map of properties
     * implemented by {@link Configuration} class as a value.
     * @throws ConfigurationException
     */
    public static LinkedHashMap<String, Configuration> loadProperties(InputStream in, String nameOfElementsArray)
            throws ParseException, ConfigurationException, IOException {
        LinkedHashMap<String, Configuration> map = new LinkedHashMap<>();
        Parameters parameters = new Parameters();

        FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(parameters
                                .properties()
                                .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                        );


        PropertiesConfiguration config = builder.getConfiguration();
        FileHandler file = new FileHandler(config);
        file.load(in);

        String[] names = (String[]) config.getArray(String.class, nameOfElementsArray);
        if (names == null)
            throw new ParseException("There is no attribute with the given name [" + nameOfElementsArray + "]");
        for (String name : names) {
            Configuration subset = config.subset(name);
            map.put(name, subset);
        }

        return map;
    }

    /**
     * <p>Loads hierarchical configurations from given XML file. Manage file input stream and pass control to
     * {@link #loadXMLConfiguration(InputStream, String, String) loadXMLConfiguration()}. Stream closes automatically.</p>
     * @see #loadXMLConfiguration(InputStream, String, String) loadXMLConfiguration()
     */
    public static LinkedHashMap<String, Configuration> loadXMLConfiguration(File file, String nameAttribute, String elementName)
            throws ParseException, ConfigurationException, IOException {
        try (InputStream in = new FileInputStream(file)) {
            return loadXMLConfiguration(in, nameAttribute, elementName);
        }
    }

    /**
     * <p>
     * Loads hierarchical configurations from given {@code InputStream}.<br>
     *     Supports the following file structure:
     *    </p>
     * <pre>
     * &lt;elements&gt;
     *   &lt;element nameAttribute="name of the element"&gt;
     *      &lt;property1&gt;value1&lt;/property&gt;
     *      &lt;property2&gt;value2&lt;/property&gt;
     *   &lt;/element&gt;
     * &lt;/config&gt;
     * </pre>
     * Return map, that contain values of nameAttribute as a key and flat map of properties
     * implemented by {@link Configuration} class as a value.
     * <p>Returns empty map if there are no elements with the given name. Elements, that are not
     * specified in {@code elementName} are ignored. If element name is repeated, than map will
     * contains only last element.</p>
     * @param in input stream to read from
     * @param nameAttribute name of the attribute
     * @param elementName name of elements (excepting root)
     * @return map, that contain values of nameAttribute as a key and flat map of properties
     * implemented by {@link Configuration} class as a value.
     * @throws ParseException if there is no {@code nameAttribute} with the given name
     * @throws ConfigurationException if an XML read error occurs
     * @throws IOException if an input stream error occurs
     */
    public static LinkedHashMap<String, Configuration> loadXMLConfiguration(InputStream in, String nameAttribute, String elementName)
            throws ParseException, ConfigurationException, IOException {
        LinkedHashMap<String, Configuration> map = new LinkedHashMap<>();
        Parameters params = new Parameters();

        FileBasedConfigurationBuilder<XMLConfiguration> builder =
                new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                        .configure(params
                                .xml()
                        );

        XMLConfiguration config = builder.getConfiguration();
        FileHandler file = new FileHandler(config);
        file.load(in);

        List<HierarchicalConfiguration<ImmutableNode>> configurationList = config.configurationsAt(elementName);

        for (HierarchicalConfiguration<ImmutableNode> node : configurationList) {
            Configuration subset = node.subset("");
            String key = subset.get(String.class, "[@" + nameAttribute + "]", null);
            if (key == null)
                throw new ParseException("There is no attribute with the given name: [" + nameAttribute + "]");
            subset.clearProperty("[@" + nameAttribute + "]");
            map.put(key, subset);
        }

        return map;
    }
}