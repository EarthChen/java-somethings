package com.earthchen.somethings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

/**
 * 这个类是从 {@link java.lang.invoke.SerializedLambda} 里面 copy 过来的，
 * 字段信息完全一样
 * <p>负责将一个支持序列的 Function 序列化为 SerializedLambda</p>
 *
 * @author HCL
 * @since 2018/05/10
 */
@SuppressWarnings("unused")
public class SerializedLambda implements Serializable {

    private static final long serialVersionUID = 8025925345765570181L;

    private Class<?> capturingClass;
    private String functionalInterfaceClass;
    private String functionalInterfaceMethodName;
    private String functionalInterfaceMethodSignature;
    private String implClass;
    private String implMethodName;
    private String implMethodSignature;
    private int implMethodKind;
    private String instantiatedMethodType;
    private Object[] capturedArgs;

    public static SerializedLambda resolve(SFunction<?, ?> lambda) {
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(serialize(lambda))) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                Class<?> clazz;
                try {
                    clazz = ClassUtil.toClassConfident(objectStreamClass.getName());
                } catch (Exception ex) {
                    clazz = super.resolveClass(objectStreamClass);
                }
                return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
            }
        }) {
            return (SerializedLambda) objIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("This is impossible to happen", e);
        }
    }

    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            oos.flush();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
        }
        return baos.toByteArray();
    }

    /**
     * 获取接口 class
     *
     * @return 返回 class 名称
     */
    public String getFunctionalInterfaceClassName() {
        return normalizedName(functionalInterfaceClass);
    }

    /**
     * 获取实现的 class
     *
     * @return 实现类
     */
    public Class<?> getImplClass() {
        return ClassUtil.toClassConfident(getImplClassName());
    }

    /**
     * 获取 class 的名称
     *
     * @return 类名
     */
    public String getImplClassName() {
        return normalizedName(implClass);
    }

    /**
     * 获取实现者的方法名称
     *
     * @return 方法名称
     */
    public String getImplMethodName() {
        return implMethodName;
    }

    /**
     * 正常化类名称，将类名称中的 / 替换为 .
     *
     * @param name 名称
     * @return 正常的类名
     */
    private String normalizedName(String name) {
        return name.replace('/', '.');
    }

    /**
     * @return 获取实例化方法的类型
     */
    public Class<?> getInstantiatedType() {
        String instantiatedTypeName = normalizedName(instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(';')));
        return ClassUtil.toClassConfident(instantiatedTypeName);
    }

    /**
     * @return 字符串形式
     */
    @Override
    public String toString() {
        String interfaceName = getFunctionalInterfaceClassName();
        String implName = getImplClassName();
        return String.format("%s -> %s::%s",
                interfaceName.substring(interfaceName.lastIndexOf('.') + 1),
                implName.substring(implName.lastIndexOf('.') + 1),
                implMethodName);
    }

}
