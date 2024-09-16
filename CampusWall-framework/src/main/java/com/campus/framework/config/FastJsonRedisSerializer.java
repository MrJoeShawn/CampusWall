package com.campus.framework.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.util.Assert;
import java.nio.charset.Charset;

/**
 * Redis使用FastJson序列化
 * 
 * @author sg
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T>
{

    // 默认字符集
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    // 泛型类类型
    private Class<T> clazz;

    // 静态初始化块，设置FastJson的全局配置允许自动类型支持
    static
    {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    /**
     * 构造函数
     *
     * @param clazz 泛型类的Class对象
     */
    public FastJsonRedisSerializer(Class<T> clazz)
    {
        super();
        this.clazz = clazz;
    }

    /**
     * 序列化方法
     *
     * @param t 待序列化的对象
     * @return 序列化后的字节数组
     * @throws SerializationException 序列化异常
     */
    @Override
    public byte[] serialize(T t) throws SerializationException
    {
        if (t == null)
        {
            return new byte[0];
        }
        // 使用FastJson将对象序列化为JSON字符串，并转为字节数组
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    /**
     * 反序列化方法
     *
     * @param bytes 待反序列化的字节数组
     * @return 反序列化后的对象
     * @throws SerializationException 反序列化异常
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException
    {
        if (bytes == null || bytes.length <= 0)
        {
            return null;
        }
        // 将字节数组转为字符串，然后使用FastJson将字符串反序列化为对象
        String str = new String(bytes, DEFAULT_CHARSET);
        return JSON.parseObject(str, clazz);
    }

    /**
     * 获取Java类型方法
     *
     * @param clazz 类型的Class对象
     * @return JavaType对象
     */
    protected JavaType getJavaType(Class<?> clazz)
    {
        // 使用Jackson的TypeFactory创建JavaType对象
        return TypeFactory.defaultInstance().constructType(clazz);
    }
}
