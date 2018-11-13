//package com.supets.annotationprocessordemo.service;
//
//import com.zhy.ioc.ILoaded;
//import com.zhy.ioc.ServiceName;
//
//import java.util.Map;
//
//@ServiceName(value = ModuleService.class, name = "moduleservice")
//public class Service$$Module implements ILoaded {
//    @Override
//    public void load(Map<String, Class<?>> classMap) {
//        ServiceName service = getClass().getAnnotation(ServiceName.class);
//        classMap.put(service.name(), service.value());
//    }
//}
