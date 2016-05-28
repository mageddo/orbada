package pl.mpak.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.StringTokenizer;

public class Main {
	private static File startupJar;
	private static StartupClassLoader classLoader;
	private static String[] fileToDelete = {"hsqldb.jar", "pl.mpak.parser-1.0.0.jar", "pl.mpak.plugins-1.0.0.jar", "pl.mpak.g2-1.0.0.jar", "pl.mpak.id-1.0.0.jar", "pl.mpak.task-1.0.1.jar", "pl.mpak.util-1.0.0.jar", "pl.mpak.sky-2.0.0.jar", "pl.mpak.usedb-1.2.0.jar", "pl.mpak.datatext-1.0.0.jar", "pl.mpak.doscharset-1.0.0.jar", "pl.mpak.waitdlg-1.0.0.jar", "pl.mpak-1.0.0.jar", "swing-layout-1.0.3.jar"};

	private static void searchLibPath(StartupClassLoader paramStartupClassLoader, String paramString) {
		File[] arrayOfFile1 = new File(paramString).listFiles(new FilenameFilter() {
			public boolean accept(File paramAnonymousFile, String paramAnonymousString) {
				return paramAnonymousString.toUpperCase().endsWith(".JAR");
			}
		});
		if(arrayOfFile1 != null) {
			try {
				for(File localFile : arrayOfFile1) {
					int k = 0;
					for(String str : fileToDelete) {
						if(str.equalsIgnoreCase(localFile.getName())) {
							localFile.delete();
							System.out.println("Deleted " + localFile);
							k = 1;
							break;
						}
					}
					if(k == 0) {
						paramStartupClassLoader.addURL(localFile.toURI().toURL());
						System.out.println("Added " + localFile);
					}
				}
			} catch(MalformedURLException localMalformedURLException) {
				localMalformedURLException.printStackTrace();
				System.exit(-1);
			}
		}
		arrayOfFile1 = new File(paramString).listFiles(new FilenameFilter() {
			public boolean accept(File paramAnonymousFile, String paramAnonymousString) {
				return new File(paramAnonymousFile.getAbsolutePath() + "/" + paramAnonymousString).isDirectory();
			}
		});
		if(arrayOfFile1 != null) {
			for(File localFile : arrayOfFile1) {
				searchLibPath(paramStartupClassLoader, localFile.getAbsolutePath());
			}
		}
	}

	public static void main(String[] paramArrayOfString) {
		if((ClassLoader.getSystemClassLoader() instanceof StartupClassLoader)) {
			classLoader = (StartupClassLoader) ClassLoader.getSystemClassLoader();
		} else {
			System.out.println("Can not start application, invalid class loader!");
			System.exit(-1);
		}
		Properties localProperties = new Properties();
		try {
			final String startupIni = "startup.ini";
			File startupFile = new File(startupIni);
			if(!startupFile.exists()) {
				startupFile = new File(ClassLoader.getSystemClassLoader().getResource(startupIni).getFile());
			}
			localProperties.load(new FileInputStream(startupFile));

			if(localProperties.getProperty("Startup-Jar") == null) {
				File[] arrayOfFile1 = new File(".").listFiles(new FilenameFilter() {
					public boolean accept(File paramAnonymousFile, String paramAnonymousString) {
						return paramAnonymousString.toUpperCase().endsWith(".JAR");
					}
				});
				for(File localFile : arrayOfFile1) {
					if(!localFile.getName().equalsIgnoreCase("startup.jar")) {
						startupJar = localFile;
						break;
					}
				}
			} else {
				startupJar = new File(localProperties.getProperty("Startup-Jar"));
			}
			if(startupJar == null) {
				System.out.println("Can not start application, startup jar not found!");
				System.exit(-1);
			}

			System.out.println("Startup with " + startupJar);
			classLoader.addURL(startupJar.toURI().toURL());

			StringTokenizer localStringTokenizer = new StringTokenizer(localProperties.getProperty("Lib-Path", "lib;plugins"), ";");
			while(localStringTokenizer.hasMoreTokens()) {
				searchLibPath(classLoader, localStringTokenizer.nextToken());
			}
			final String className = localProperties.getProperty("Startup-Main");
			System.out.printf("Starting Main invocation: %s\n", className);
			Class localClass = classLoader.loadClass(className);
			System.out.println(String.format("starting main class=%s", localClass.getName()));
			Method localMethod = localClass.getDeclaredMethod("main", new Class[]{String[].class});
			localMethod.invoke(localClass, new Object[]{paramArrayOfString});
			System.out.println("End Main invocation");
		} catch(Throwable localException) {
			System.out.println("classpath: >>>>");
			ClassLoader cl=ClassLoader.getSystemClassLoader();
			URL[]urls=((URLClassLoader)cl).getURLs();
			for(URL url:urls){
				System.out.println(url.getFile());
			}
			System.exit(-1);
			localException.printStackTrace();
		}
	}
}
