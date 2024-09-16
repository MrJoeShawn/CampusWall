package com.campus.framework.untils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean拷贝工具类，提供对象及对象列表的属性拷贝功能
 */
public class BeanCopyUtils {

    // 私有构造方法，避免实例化
    private BeanCopyUtils() {
    }

    /**
     * 将源对象的属性拷贝到目标对象中
     *
     * @param source 源对象
     * @param clazz  目标对象的类
     * @param <V>    目标对象的泛型类型
     * @return 拷贝后的目标对象
     */
    public static <V> V copyBean(Object source, Class<V> clazz) {
        // 创建目标对象
        V result = null;
        try {
            result = clazz.newInstance();
            // 实现属性copy
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回结果
        return result;
    }

    /**
     * 将对象列表的每个元素属性拷贝到指定类的新列表中
     *
     * @param list   源对象列表
     * @param clazz  目标对象的类
     * @param <O>    源对象列表的泛型类型
     * @param <V>    目标对象的泛型类型
     * @return 拷贝后的目标对象列表
     */
    public static <O, V> List<V> copyBeanList(List<O> list, Class<V> clazz) {
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }
}
