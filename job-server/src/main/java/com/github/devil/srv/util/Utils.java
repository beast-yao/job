package com.github.devil.srv.util;

/**
 * @author eric.yao
 * @date 2021/1/22
 **/
public class Utils {

    public static String getVersion(){
        Package aPackage = Utils.class.getPackage();
        return aPackage == null?"???":aPackage.getImplementationVersion();
    }

}
