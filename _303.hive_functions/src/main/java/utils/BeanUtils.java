package utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class BeanUtils {
    /**
     * 通过getter方法根据属性名获取对象的值
     * @param obj        要获取值的对象
     * @param propertyName 属性名（对应getter方法名）
     * @return 属性对应的值
     * @throws Exception 如果获取过程中出错
     */
    public static Object getPropertyValue(Object obj, String propertyName) throws Exception {
        // 获取Bean信息
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        // 获取所有属性描述符
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

        // 查找指定属性
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getName().equals(propertyName)) {
                // 获取getter方法
                Method getter = descriptor.getReadMethod();
                // 调用getter方法获取值
                return getter.invoke(obj);
            }
        }

        throw new NoSuchFieldException("属性 " + propertyName + " 不存在");
    }
}
