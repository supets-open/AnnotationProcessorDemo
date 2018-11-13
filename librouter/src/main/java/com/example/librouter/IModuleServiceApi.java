package com.example.librouter;

public class IModuleServiceApi extends Arouter<IModuleService> {

    private static IModuleService api = service("IModuleService");

    public static void test() {
        api.test();
    }

}
