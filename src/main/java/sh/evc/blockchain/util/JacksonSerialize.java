package sh.evc.blockchain.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Jackson 实现的json和xml序列化.
 *
 * @author winixi
 */
public class JacksonSerialize {

  private static final Logger log = LoggerFactory.getLogger(JacksonSerialize.class);
  private static final ObjectMapper OBJECT_MAPPER;

  static {
    OBJECT_MAPPER = new ObjectMapper();
    //下划线
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  public static <T> T jsonToBean(String json, Class<T> clazz) {
    T result = null;
    try {
      result = OBJECT_MAPPER.readValue(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), clazz);
    } catch (IOException e) {
      log.error("Convert json to bean " + clazz.getName() + " error.", e);
    }
    return result;
  }

  public static <T> T jsonToBean(String json, TypeReference valueTypeRef) {
    T result = null;
    try {
      result = (T) OBJECT_MAPPER.readValue(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), valueTypeRef);
    } catch (IOException e) {
      log.error("Convert json to bean " + valueTypeRef.getType().getTypeName() + " error.", e);
    }
    return result;
  }

  public static String beanToJson(Object object) {
    String result = null;
    try {
      result = OBJECT_MAPPER.writeValueAsString(object);
    } catch (Exception e) {
      log.error("Convert bean" + object.getClass().getName() + " to json error.", e);
    }
    return result;
  }

}
