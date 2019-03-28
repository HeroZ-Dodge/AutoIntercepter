package com.dodge.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by linzheng on 2019/3/27.
 */

@Retention(RetentionPolicy.CLASS) // this is necessary for java analyzer to work
public @interface TestAnnotation {
}
