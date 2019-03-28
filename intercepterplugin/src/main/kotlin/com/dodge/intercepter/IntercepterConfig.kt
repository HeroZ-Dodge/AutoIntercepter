package com.dodge.intercepter

/**
 *  Created by linzheng on 2019/3/26.
 */
open class IntercepterConfig {

    var isDebug: Boolean = false
    var name: String = "IntercepterConfig"
    var intercepterList: List<Map<String, String>> = mutableListOf()

    override fun toString(): String {
        return "IntercepterConfig(isDebug=$isDebug, name='$name', intercepterList=$intercepterList)"
    }


}