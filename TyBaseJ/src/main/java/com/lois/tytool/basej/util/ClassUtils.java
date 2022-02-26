package com.lois.tytool.basej.util;

import com.lois.tytool.basej.constant.BaseTypeConstants;
import com.lois.tytool.basej.constant.FileConstants;
import com.lois.tytool.basej.string.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 类工具
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class ClassUtils {
	private static Logger logger = LoggerFactory.getLogger(ClassUtils.class);
	private static char DOT = '.';
	private static String DOT_STR = "\\.";
	private static String SPLIT_DOT = ".";
	/**
	 * 获取指定包下的所有class集合.
	 * @param pack 包路径
	 * @return class集
	 */
	private static Set<Class<?>> getClasses(final String pack) {

			// 第一个class类的集合
			Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
			// 是否循环迭代
			boolean recursive = true;
			// 获取包的名字 并进行替换
			String packageName = pack;
			char separator = FileConstants.SLASH_SEPARATOR.toCharArray()[0];
			String packageDirName = packageName.replace(DOT, separator);
			// 定义一个枚举的集合 并进行循环来处理这个目录下的things
			Enumeration<URL> dirs;
			try {
				dirs = Thread.currentThread().getContextClassLoader().getResources(
						packageDirName);
				// 循环迭代下去
				while (dirs.hasMoreElements()) {
					// 获取下一个元素
					URL url = dirs.nextElement();
					// 得到协议的名称
					String protocol = url.getProtocol();
					// 如果是以文件的形式保存在服务器上
					if (FileConstants.FILE_PROTOCOL.equals(protocol)) {
						//file类型的扫描
						// 获取包的物理路径
						String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
						logger.debug("扫描的文件路径filePath: {}", filePath);
						// 以文件的方式扫描整个包下的文件 并添加到集合中
						findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
					} else if (FileConstants.JAR_FILE.contains(protocol) || FileConstants.ZIP_FILE.contains(protocol)) {
						// 如果是jar包文件
						// 定义一个JarFile
						JarFile jar = null;
						try {
							if (FileConstants.ZIP_FILE.contains(protocol)) {
								String jarPackagePath = url.getFile();
								String jarPath;
								if (jarPackagePath.endsWith(FileConstants.SLASH_SEPARATOR)) {
									jarPath = jarPackagePath.replace("!/" + packageDirName + FileConstants.SLASH_SEPARATOR, "");
								} else {
									jarPath = jarPackagePath.replace("!/" + packageDirName, "");
								}
								jar = new JarFile(jarPath);

							} else if (FileConstants.JAR_FILE.contains(protocol)) {
								jar = ((JarURLConnection) url.openConnection()).getJarFile();
							} else {
								throw new IllegalArgumentException("不能识别的jar包协议：" + protocol);
							}
							// 获取jar
							// 从此jar包 得到一个枚举类
							Enumeration<JarEntry> entries = jar.entries();
							// 同样的进行循环迭代
							while (entries.hasMoreElements()) {
								// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
								JarEntry entry = entries.nextElement();
								String name = entry.getName();
								// 如果是以/开头的
								if (name.charAt(0) == separator) {
									// 获取后面的字符串
									name = name.substring(1);
								}
								// 如果前半部分和定义的包名相同
								if (name.startsWith(packageDirName)) {
									int idx = name.lastIndexOf(separator);
									// 如果以"/"结尾 是一个包
									if (idx != -1) {
										// 获取包名 把"/"替换成"."
										packageName = name.substring(0, idx).replace(separator, '.');
									}
									// 如果可以迭代下去 并且是一个包
									if ((idx != -1) || recursive) {
										// 如果是一个.class文件 而且不是目录
										if (name.endsWith(FileConstants.CLASS_FILE) && !entry.isDirectory()) {
											// 去掉后面的".class" 获取真正的类名
											String className = name.substring(packageName.length() + 1, name.length() - 6);
											try {
												// 添加到classes
												String cn = packageName + DOT + className;
												classes.add(Class.forName(cn));
											} catch (ClassNotFoundException e) {
												logger.error("找不到指定class：{}",packageName + DOT + className, e);
											}
										}
									}
									}
							}
						} catch (IOException e) {
							logger.error("从jar包获取文件出错", e);
						} finally {
							if (jar != null) {
								jar.close();
							}
						}
					}
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

			return classes;
	}


	/**
	 * 获取指定目录下的所有class文件.
	 * @param packageName 包名称
	 * @param packagePath 包的路径
	 * @param recursive 是否递归
	 * @param classes class
	 */
	private static void findAndAddClassesInPackageByFile(final String packageName,
		final String packagePath, final boolean recursive, final Set<Class<?>> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			@Override
			public boolean accept(final File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(FileConstants.CLASS_FILE));
			}
		});
		if (dirfiles == null) {
			return;
		}
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "."
						+ file.getName(), file.getAbsolutePath(), recursive, classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 添加到集合中去
					classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					logger.error("找不到指定class：{}", packageName + '.' + className, e);
				}
			}
		}
	}


	/**
	 * 获取指定包下的所有class集合.
	 * @param pkg 包名
	 * @return class名集合
	 */
	public static List<String>  getSpecifiedPkgClasses(final String pkg) {
		List<String> classes = new ArrayList<String>();
		Object[] ts = getClasses(pkg).toArray();
		for (Object t:ts) {
			Class<?> tt = (Class<?>) t;
			//过滤掉内部类或者线程导致的$文件
			if (tt.getName().indexOf("$") == -1) {
				classes.add(tt.getName());
			}
		}
		return classes;
	}

	/**
	 * 获取bean的名称
	 * @param qualifiedName 类全路径名称
	 * @return bean的名称
	 */
	public static String getBeanName(String qualifiedName) {
		String classNamge = qualifiedName;
		if (qualifiedName.indexOf(SPLIT_DOT) != -1) {
			//有包名
			String[] names = qualifiedName.split(DOT_STR);
			classNamge = names[names.length - 1];
		}
		if (classNamge.length() <= 1) {
			return classNamge.toLowerCase();
		} else {
			return classNamge.substring(0, 1).toLowerCase() + classNamge.substring(1, classNamge.length());
		}
	}

	/**
	 * 获取类加载器
	 * @return classLoader
	 */
	public static ClassLoader getClassLoader() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = ClassUtils.getClassLoader();
		}
		return classLoader;
	}

	/**
	 * 根据类名，获取对应的Class对象.<br>
	 * 当指不到类时，则返回null
	 * @param className 类名
	 * @return class对象
	 */
	public static Class<?> getClass(final String className) {
		Class<?> obj = null;
		try {
			//根据类名，映射对象
			obj = Class.forName(className);
		} catch (ClassNotFoundException e) {
			logger.error("找不到指定的class:{}", className, e);
		}
		return obj;
	}
	/**
	 * 判断传入的type（类型）是否是java基型(包括lang包加强型).
	 * @param type 类型
	 * @return 是否是基础类型
	 */
	public static boolean isBaseType(final String type) {
		boolean result = false;
		if (StringUtils.isBlank(type)) {
			return false;
		}
		if (BaseTypeConstants.BYTE_TYPE.equals(type) || BaseTypeConstants.BYTE_OBJECT_TYPE.equals(type)) {
			result = true;
		} else if (BaseTypeConstants.SHORT_TYPE.equals(type) || BaseTypeConstants.SHORT_OBJECT_TYPE.equals(type)) {
			result = true;
		} else if (BaseTypeConstants.INT_TYPE.equals(type) || BaseTypeConstants.INT_OBJECT_TYPE.equals(type)) {
			result = true;
		} else if (BaseTypeConstants.LONG_TYPE.equals(type) || BaseTypeConstants.LONG_OBJECT_TYPE.equals(type)) {
			result = true;
		} else if (BaseTypeConstants.FLOAT_TYPE.equals(type) || BaseTypeConstants.FLOAT_OBJECT_TYPE.equals(type)) {
			result = true;
		} else if (BaseTypeConstants.DOUBLE_TYPE.equals(type) || BaseTypeConstants.DOUBLE_OBJECT_TYPE.equals(type)) {
			result = true;
		} else if (BaseTypeConstants.BOOLEAN_TYPE.equals(type) || BaseTypeConstants.BOOLEAN_OBJECT_TYPE.equals(type)) {
			result = true;
		} else if (BaseTypeConstants.CHAR_TYPE.equals(type) || BaseTypeConstants.CHAR_OBJECT_TYPE.equals(type)) {
			result = true;
		}
		return result;
	}
	/**
	 * 判断class类型是否是int或者integer类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isIntOrInteger(final String type) {
		return BaseTypeConstants.INT_TYPE.equals(type) || BaseTypeConstants.INT_OBJECT_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是byte或者Byte类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isByte(final String type) {
		return BaseTypeConstants.BYTE_TYPE.equals(type) || BaseTypeConstants.BYTE_OBJECT_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是short或者Short类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isShort(final String type) {
		return BaseTypeConstants.SHORT_TYPE.equals(type) || BaseTypeConstants.SHORT_OBJECT_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是long或者Long类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isLong(final String type) {
		return BaseTypeConstants.LONG_TYPE.equals(type) || BaseTypeConstants.LONG_OBJECT_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是float或者Float类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isFloat(final String type) {
		return BaseTypeConstants.FLOAT_TYPE.equals(type) || BaseTypeConstants.FLOAT_OBJECT_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是double或者Double类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isDouble(final String type) {
		return BaseTypeConstants.DOUBLE_TYPE.equals(type) || BaseTypeConstants.DOUBLE_OBJECT_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是boolean或者Boolean类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isBoolean(final String type) {
		return BaseTypeConstants.BOOLEAN_TYPE.equals(type) || BaseTypeConstants.BOOLEAN_OBJECT_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是char或者Character类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isCharOrCharacter(final String type) {
		return BaseTypeConstants.CHAR_TYPE.equals(type) || BaseTypeConstants.CHAR_OBJECT_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是HttpServletRequest类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isHttpServletRequire(final String type) {
		return BaseTypeConstants.HTTP_SERVLET_REQUEST_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是ServletRequest类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isServletRequire(final String type) {
		return BaseTypeConstants.SERVLET_REQUEST_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是ServletResponse类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isServletResponse(final String type) {
		return BaseTypeConstants.SERVLET_RESPONSE_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是HttpServletResponse类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isHttpServletResponse(final String type) {
		return BaseTypeConstants.HTTP_SERVLET_RESPONSE_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是HttpSession类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isSession(final String type) {
		return BaseTypeConstants.HTTP_SESSION_TYPE.equals(type);
	}

	/**
	 * 判断class类型是否是Cookie类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isCookie(final String type) {
		return BaseTypeConstants.COOKIE_TYPE.equals(type);
	}

	/**
	 * 判断class类型是否是String类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isStringType(final String type) {
		return BaseTypeConstants.STRING_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是List类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isListType(final String type) {
		return BaseTypeConstants.LIST_TYPE.equals(type);
	}
	/**
	 * 判断class类型是否是Map类型.
	 * @param type class 类型
	 * @return boolean
	 */
	public static boolean isMapType(final String type) {
		return BaseTypeConstants.MAP_TYPE.equals(type);
	}

	/**
	 * 判断class类型是否是Date类型
	 * @param type class类型
	 * @return boolean
	 */
	public static boolean isDate(final String type) {
		return BaseTypeConstants.DATE_TYPE.equals(type);
	}
}


