package com.supets.annotationprocessordemo.processor2;

import android.app.Activity;
import android.view.View;

/**
 * AnnotationProcessorDemo
 *
 * @user lihongjiang
 * @description
 * @date 2017/9/25
 * @updatetime 2017/9/25
 */

public class ActivityViewFinder implements ViewFinder {
    @Override
    public View findView(Object object, int id) {
        Activity activity = (Activity) object;
        return activity.findViewById(id);
    }
}
