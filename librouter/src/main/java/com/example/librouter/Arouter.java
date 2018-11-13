package com.example.librouter;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.zhy.ioc.ILoaded;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexFile;

public class Arouter<T extends Object> {


    private static String PACKAGE = PackageScanConfig.PACKAGE;

    private static volatile Arouter instance;

    private static Map<String, Class<?>> SERVICE = new ConcurrentHashMap<>();

    private static Arouter getInstance() {
        if (instance == null) {
            synchronized (Arouter.class) {
                if (instance == null) {
                    instance = new Arouter();
                }
            }
        }
        return instance;
    }


    private static Application mContext;

    public static void init(Application context) {
        mContext = context;

        //查找dex文件
        List<String> apkpath = new ArrayList<>();
        try {
            ApplicationInfo applicationInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), 0);

            //apk路径
            apkpath.add(applicationInfo.sourceDir);
            //开起了instantrun
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != applicationInfo.splitSourceDirs) {
                    apkpath.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //扫描包下面的服务类
        List<String> classNames = new ArrayList<>();

        for (String path : apkpath) {
            DexFile dexFile = null;
            try {
                dexFile = new DexFile(path);
                Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    String classname = entries.nextElement();
                    if (classname.startsWith(PACKAGE)) {
                        classNames.add(classname);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dexFile != null) {
                    try {
                        dexFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        //注册代理映射表
        for (String className : classNames) {
            if (className.startsWith(PACKAGE + ".Service$$")) {
                try {
                    Class<?> loadclass = Class.forName(className);
                    ILoaded load = (ILoaded) loadclass.newInstance();
                    load.load(SERVICE);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    //代理类注入

    private static Map<String, Object> IMPL = new HashMap<>();

    @SuppressWarnings("unchecked")
    protected static <T> T service(String path) {
        T object = (T) IMPL.get(path);
        if (object == null) {
            Class<?> clazz = SERVICE.get(path);
            if (clazz != null) {
                try {
                    object = (T) clazz.newInstance();
                    IMPL.put(path, object);
                    SERVICE.remove(path);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }
}
