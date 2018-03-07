package com.cool.util;

/**
 * Created by codelover on 17/5/24.
 */
public class IsTheType {
    public static <S,T extends S> boolean isTheType(S s, Class<T> clazz){
        try{
            T t = (T) s;
            return true;
        }catch (Exception ignore){
            return false;
        }
    }
}
