package gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.jscience.physics.amount.Amount;
import org.jscience.physics.amount.AmountFormat;

import java.lang.reflect.Type;

/**
 * @author Ilya Ivanov
 */
public class AmountSerializer implements JsonSerializer<Amount> {
    @Override
    public JsonElement serialize(Amount amount, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(AmountFormat.getExactDigitsInstance().format(amount).toString());
    }
}
