package com.supets.annotationprocessordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.librouter.Arouter;
import com.example.librouter.IModuleServiceApi;
import com.supets.annotationprocessordemo.processor.ViewInjector;
import com.supets.annotationprocessordemo.processor2.LCJViewBinder;
import com.zhy.ioc.Bind;
import com.zhy.ioc.test.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_name)
    View mContent;
    @Bind(R.id.tv_name)
    View mContent2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.injectView(this);
        LCJViewBinder.bind(this);

        Arouter.init(getApplication());

        IModuleServiceApi.test();
    }

}
