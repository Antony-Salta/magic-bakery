package test.structural;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

enum AccessType {PUBLIC, PROTECTED, PRIVATE}

public class StructuralHelper {

	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Field[] getFields(String className){
		try {
			return Class.forName(className).getDeclaredFields();
		} catch (ClassNotFoundException e) { return null; }
	}

	public static Method[] getMethods(String className){
		try {
			return Class.forName(className).getDeclaredMethods();
		} catch (ClassNotFoundException e) { return null; }
	}


    // ---------------------- Class --------------------------
    
	public static boolean classExists(String className) {
		return getClass(className) != null;
	}

	public static boolean classIsPublic(String className){
		Class<?> clazz = getClass(className);
		if (clazz == null) return false;
		return Modifier.isPublic(clazz.getModifiers());
	}

    public static boolean classIsAbstract(String className) {
		Class<?> clazz = getClass(className);
        if (clazz == null) return false;
		return Modifier.isAbstract(clazz.getModifiers());
	}

    public static boolean classIsComparable(String className) {
        Class<?> clazz = getClass(className);
        if (clazz == null) return false;
        return Comparable.class.isAssignableFrom(clazz);
    }

    public static boolean classIsSubclassOf(String className, String parentName) {
        Class<?> superclass = getClass(parentName);
        Class<?> subclass = getClass(className);
        if (subclass == null || superclass == null) return false;
        return superclass.isAssignableFrom(subclass);
    }

    public static boolean classIsSerializable(String className) {
        Class<?> clazz = getClass(className);
        if (clazz == null) return false;
        return Serializable.class.isAssignableFrom(clazz);
    }

    public static boolean classIsException(String className) {
        Class<?> clazz = getClass(className);
        if (clazz == null) return false;
        return Exception.class.isAssignableFrom(clazz);
    }


    // -------------------- Constructor ------------------------

    public static boolean ctorExists(String className, AccessType type, Class<?>... argClasses) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = Class.forName(className);
        Constructor<?> c = clazz.getDeclaredConstructor(argClasses);
        return isAccessType(c.getModifiers(), type);
    }

    public static boolean ctorIsPublic(String className, Class<?>... argClasses) throws ClassNotFoundException, NoSuchMethodException {
        return ctorExists(className, AccessType.PUBLIC, argClasses);
	}

    public static boolean ctorIsProtected(String className, Class<?>... argClasses) throws ClassNotFoundException, NoSuchMethodException {
        return ctorExists(className, AccessType.PROTECTED, argClasses);
	}

    public static boolean ctorIsPrivate(String className, Class<?>... argClasses) throws ClassNotFoundException, NoSuchMethodException {
        return ctorExists(className, AccessType.PRIVATE, argClasses);
	}


    // -------------------- Field Modifiers ------------------------

    public static boolean isAccessType(int mods, AccessType type) {
		if (type == AccessType.PUBLIC) {
			if (Modifier.isPublic(mods)) return true;
		} else if (type == AccessType.PROTECTED) {
			if (Modifier.isProtected(mods)) return true;
		} else if (type == AccessType.PRIVATE) {
			if (Modifier.isPrivate(mods)) return true;
		}
		return false;
	}

	public static Field getField(String fieldName, Field[] fields) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(fieldName)) {
				return fields[i];
			}
		}
		return null;
	}

    public static boolean fieldAccessIsAccessType(String fieldName, Field[] fields, AccessType type){
		Field fld = getField(fieldName, fields);
		
		if (fld == null)
			return false;

		return isAccessType(fld.getModifiers(), type);
	}

    public static boolean fieldExists(String fieldName, Field[] fields) {
		return getField(fieldName, fields) != null;
	}

	public static boolean fieldIsFinal(String fieldName, Field[] fields) {
		Field fld = getField(fieldName, fields);
		if (fld == null)
			return false;

		return Modifier.isFinal(fld.getModifiers());
	}

	public static boolean fieldIsStatic(String fieldName, Field[] fields) {
		Field fld = getField(fieldName, fields);
		if (fld == null)
			return false;

		return Modifier.isStatic(fld.getModifiers());
	}

	public static boolean hasSerialVersionUID(Field[] fields) {
		return fieldExists("serialVersionUID", fields);
	}


	// -------------------- Field Types ------------------------

	public static boolean fieldHasType(String fieldName, Field[] fields, Class<?> type) {
		Field fld = getField(fieldName, fields);
		if (fld == null)
			return false;
		
		Class<?> tmp = fld.getType();
		return tmp == type;
	}

	public static boolean fieldHasGenericType(String fieldName, Field[] fields, Class<?> type, Class<?> enclosedType) {
		Field fld = getField(fieldName, fields);
		if (fld == null)
			return false;
		
		Class<?> outer = fld.getType();
		if (outer != type)
			return false;
		
		Type fieldType = fld.getGenericType();
		if (!(fieldType instanceof ParameterizedType))
			return false;
		ParameterizedType paramType = (ParameterizedType)fieldType;
		Class<?> inner = (Class<?>)paramType.getActualTypeArguments()[0];
		return inner == enclosedType;
	}


	// -------------------- Method Modifiers ------------------------

	public static Method getMethod(String methodName, Method[] methods){
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				return methods[i];
			}
		}
		return null;
	}

	public static Method getMethod(String methodName, Method[] methods, Class<?>... parameterTypes){
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				
				Class<?>[] thisParameterTypes = methods[i].getParameterTypes();
				if (thisParameterTypes.length != parameterTypes.length)
					continue;

				boolean found = true;
				for (int j = 0; j < thisParameterTypes.length; j++) {
					if (parameterTypes[j] != thisParameterTypes[j]) {
						found = false;
						break;
					}
				}
				if (found)
					return methods[i];
			}
		}
		return null;
	}


    public static boolean methodAccessIsAccessType(String methodName, Method[] methods, AccessType type) {
		Method mtd = getMethod(methodName, methods);
		if (mtd == null)
			return false;
		return isAccessType(mtd.getModifiers(), type);
	}

	public static boolean methodIsStatic(String methodName, Method[] methods) {
		Method mtd = getMethod(methodName, methods);
		if (mtd == null)
			return false;
		return Modifier.isStatic(mtd.getModifiers());
	}

	public static int methodCount(String methodName, Method[] methods){
		int found = 0;
		for(int i = 0; i < methods.length; i++){
			if (methods[i].getName().equals(methodName)) {
				++found;
			}
		}
		return found;
	}

	public static boolean methodExists(String methodName, Method[] methods) {
		return methodCount(methodName, methods) > 0;
	}

	public static boolean methodExists2(String methodName, Method[] methods, Class<?>... parameterTypes) {
		return getMethod(methodName, methods, parameterTypes) != null;
	}

	// -------------------- Method Types ------------------------

	public static boolean methodHasReturnType(String methodName, Method[] methods, Class<?> type, Class<?>... parameterTypes) {
		Method mtd = getMethod(methodName, methods, parameterTypes);
		if (mtd == null)
			return false;
		
		Class<?> tmp = mtd.getReturnType();
		if (tmp.isArray())
			return type.isArray() && (tmp.getComponentType() == type.getComponentType());
		return tmp == type;
	}

	public static boolean methodHasGenericReturnType(String methodName, Method[] methods, Class<?> type, Class<?> enclosedType, Class<?>... parameterTypes) {
		Method mtd = getMethod(methodName, methods, parameterTypes);
		if (mtd == null)
			return false;
		
		Class<?> outer = mtd.getReturnType();
		if (outer != type)
			return false;
		
		Type returnType = mtd.getGenericReturnType();
		if (!(returnType instanceof ParameterizedType))
			return false;

		ParameterizedType paramReturnType = (ParameterizedType)returnType;
		Class<?> inner = (Class<?>)paramReturnType.getActualTypeArguments()[0];
		return inner == enclosedType;
	}


	// -------------------- ------------ ------------------------
	public static boolean classHasPublicStaticFinalSerialVersionUID(String fqcn) {
        Class<?> clazz = null;
		Field field = null;
		try {
			clazz = Class.forName(fqcn);
            field = clazz.getDeclaredField("serialVersionUID");
		} catch (ClassNotFoundException e) {
			return false;
		} catch (NoSuchFieldException e) {
			return false;
		}
        if (!((Class<?>)field.getType()).equals(long.class)) return false;
        if (!isAccessType(field.getModifiers(), AccessType.PRIVATE)) return false;
        return Modifier.isFinal(field.getModifiers()) &&  Modifier.isStatic(field.getModifiers());
	}

	public static boolean serialVersionUIDIsLong(Field[] fields) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals("serialVersionUID")) {
				if (fields[i].getType().equals(Long.TYPE)) return true;
			}
		}
		return false;
	}

}
