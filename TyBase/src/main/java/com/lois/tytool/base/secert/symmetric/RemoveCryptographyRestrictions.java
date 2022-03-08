package com.lois.tytool.base.secert.symmetric;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;

/**
 * 对称加解密工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public final class RemoveCryptographyRestrictions {
    private volatile static RemoveCryptographyRestrictions INSTANCE=null;

    public static void init() throws Exception {
        if(INSTANCE==null) {
            synchronized (RemoveCryptographyRestrictions.class) {
                if(INSTANCE==null) {
                    INSTANCE=new RemoveCryptographyRestrictions();
                }
            }
        }
    }

    private RemoveCryptographyRestrictions() throws Exception {
        Class<?> jceSecurity = getClazz("javax.crypto.JceSecurity");
        Class<?> cryptoPermissions = getClazz("javax.crypto.CryptoPermissions");
        Class<?> cryptoAllPermission = getClazz("javax.crypto.CryptoAllPermission");
        if(jceSecurity == null || cryptoPermissions == null || cryptoAllPermission == null) {
            return;
        }
        setFinalStaticValue(jceSecurity, "isRestricted", false);
        PermissionCollection defaultPolicy = getFieldValue(jceSecurity, "defaultPolicy",  null, PermissionCollection.class);
        Map<?, ?> map=getFieldValue(cryptoPermissions, "perms", defaultPolicy, Map.class);
        map.clear();
        Permission permission=getFieldValue(cryptoAllPermission, "INSTANCE", null, Permission.class);
        defaultPolicy.add(permission);
    }

    private Class<?> getClazz(String className) {
        Class<?> clazz=null;
        try {
            clazz=Class.forName(className);
        } catch (Exception e) {
        }
        return clazz;
    }

    private void setFinalStaticValue(Class<?> srcClazz, String fieldName, Object newValue) throws Exception {
        Field field=srcClazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    private <T> T getFieldValue(Class<?> srcClazz, String fieldName, Object owner, Class<T> dstClazz) throws Exception {
        Field field=srcClazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return dstClazz.cast(field.get(owner));
    }
}
