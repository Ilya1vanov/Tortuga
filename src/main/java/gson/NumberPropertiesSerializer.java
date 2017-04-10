package gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import javafx.beans.binding.NumberExpressionBase;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;

/**
 * @author Ilya Ivanov
 */
public class NumberPropertiesSerializer implements JsonSerializer<NumberExpressionBase> {
    @Override
    public JsonElement serialize(NumberExpressionBase numberExpressionBase, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(numberExpressionBase.getValue());
    }
}
