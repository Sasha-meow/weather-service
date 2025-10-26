import com.google.gson.JsonSyntaxException;
import org.example.utils.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonUtilsTest {

    class TestObj {
        String name;
        int age;
        MarkDescription[] marks;

        class MarkDescription {
            int mark;
            String subject;
        }
    }

    JsonUtils<TestObj> jsonUtils;

    @BeforeEach
    public void setUp() {
        jsonUtils = new JsonUtils<>();
    }

    @Test
    @DisplayName("Конвертация валидной JSON-строки должна работать без ошибок")
    public void testSuccessConvertToObject() {
        String correctStr = "{\"name\":\"Test\",\"age\":18,\"marks\":[{\"mark\":5,\"subject\":\"Математика\"},{\"mark\":4,\"subject\":\"Литература\"}]}";
        assertDoesNotThrow(() -> jsonUtils.convertToObject(correctStr, TestObj.class));
    }

    @Test
    @DisplayName("Конвертация валидной JSON-строки без свойства-массива должна работать без ошибок")
    public void testSuccessConvertWithoutArray() {
        String withoutArrayStr = "{\"name\":\"Test\",\"age\":18}";
        assertDoesNotThrow(() -> jsonUtils.convertToObject(withoutArrayStr, TestObj.class));
        TestObj obj = jsonUtils.convertToObject(withoutArrayStr, TestObj.class);
        assertNull(obj.marks);
    }

    @Test
    @DisplayName("Конвертация валидной JSON-строки без свойства числового формата должна работать без ошибок")
    public void testWithoutNumberEquals() {
        String withoutAgeStr = "{\"name\":\"Test\",\"marks\":[{\"mark\":5,\"subject\":\"Математика\"},{\"mark\":4,\"subject\":\"Литература\"}]}";
        TestObj obj = jsonUtils.convertToObject(withoutAgeStr, TestObj.class);
        assertEquals(obj.age, 0);
    }

    @Test
    @DisplayName("Конвертация пустой строки должна выкидывать ошибку")
    public void testThrowsIfEmptyJson() {
        String emptyStr = "";
        assertThrows(JsonSyntaxException.class, () -> jsonUtils.convertToObject(emptyStr, TestObj.class));
    }

    @Test
    @DisplayName("Конвертация некорректной JSON-строки должна выкидывать ошибку")
    public void testThrowsIfIncorrectJson() {
        String incorrectStr = "{\"name\":\"Test\",\"age\":18,\"marks\":[{\"mark\":5,\"subject\":\"Математика\"},{\"mark\":4,\"subject\":\"Литература\"}}";
        assertThrows(JsonSyntaxException.class, () -> jsonUtils.convertToObject(incorrectStr, TestObj.class));
    }
}
