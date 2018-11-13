package com.supets.annotationprocessordemo.service;


import android.util.Log;

import com.example.librouter.IModuleService;
import com.zhy.ioc.Service;

@Service(name = "IModuleService")
public class ModuleService implements IModuleService {

    @Override
    public void test() {

        Log.v("tag", "testtesttesttesttesttesttesttesttesttesttest");

    }
}
