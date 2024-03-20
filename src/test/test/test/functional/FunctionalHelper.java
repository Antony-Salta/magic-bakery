package test.functional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FunctionalHelper {

	public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = obj.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(obj);
	}

	public static Class<?> getFieldRuntimeType(Object obj, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		return getFieldValue(obj, fieldName).getClass();
	}

	public static void setFieldValue(Object obj, String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
		Field field = obj.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(obj, newValue);
	}

	public static boolean testFieldHasMethod(Object obj, String fieldName, String methodName) {
		try {
			getFieldRuntimeType(obj, fieldName).getMethod(methodName);
			return true;
		} catch (ClassNotFoundException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		} catch (NoSuchMethodException e) {
		}
		return false;
	}

	public static Method getMethod(Class<?> cls, String methodName, Class<?>... a) {
		Method mtd;
		try {
			mtd = cls.getDeclaredMethod(methodName, a);
		} catch (NoSuchMethodException e){
			return null;
		}

		mtd.setAccessible(true);
		return mtd;
	}

	public static Method getMethod(Object obj, String methodName, Class<?>... a) {
		Method mtd;
		try {
			mtd = obj.getClass().getDeclaredMethod(methodName, a);
		} catch (NoSuchMethodException e){
			return null;
		}

		mtd.setAccessible(true);
		return mtd;
	}



}
