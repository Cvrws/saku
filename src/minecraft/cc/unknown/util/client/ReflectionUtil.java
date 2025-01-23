package cc.unknown.util.client;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.config.plugins.ResolverUtil;

import cc.unknown.Sakura;
import lombok.var;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionUtil {
	public <T> List<Class<? extends T>> resolvePackage(String packagePath, Class<T> newClass) {
		ResolverUtil resolver = new ResolverUtil();
		resolver.setClassLoader(newClass.getClassLoader());

		resolver.findInPackage(new ResolverUtil.ClassTest() {
			@Override
			public boolean matches(Class<?> type) {
				return true;
			}
		}, packagePath);

		List<Class<? extends T>> resolvedItems = new ArrayList<>();

		for (Class<?> resolved : resolver.getClasses()) {
			if (resolved == null) {
				Sakura.instance.LOGGER.debug("Encountered a null class in the package resolver.");
				continue;
			}

			if (!newClass.isAssignableFrom(resolved)) {
				continue;
			}

			if (resolved.isInterface() || Modifier.isAbstract(resolved.getModifiers())) {
				continue;
			}

			resolvedItems.add((Class<? extends T>) resolved);
		}

		return resolvedItems;
	}

	private Object getObjectInstance(Class<?> clazz) throws IllegalAccessException {
		for (var field : clazz.getDeclaredFields()) {
			if ("instance".equals(field.getName())) {
				field.setAccessible(true);
				return field.get(null);
			}
		}
		throw new IllegalAccessException("e");
	}
}
