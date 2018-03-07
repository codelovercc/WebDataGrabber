package com.cool.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by codelover on 17/5/15.
 * 数组工具类
 */
public class ArrayUtil {
    private static final int BUFF_SIZE_1024 = 1024;

    /**
     * 把int数组转成list
     * @param ints
     * @return
     */
    public static List<Integer> asList(int[] ints){
        return Arrays.stream(ints).boxed().collect(Collectors.toList());
    }

    /**
     *  创建一个对象的list
     * @param e
     * @param <E> List集合要用的类型
     * @return 返回对象集合
     */
    @SafeVarargs
    public static <E>List<E> createList(E... e){
        if(e.length == 1){
            return Collections.singletonList(e[0]);
        }
        List<E> list = new ArrayList<>();
        list.addAll(Arrays.asList(e));
        return list;
    }

    public static byte[] readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = null;
        BufferedInputStream bufIn = null;
        try {
            bufIn = new BufferedInputStream(in);
            int buffSize = BUFF_SIZE_1024;
            out = new ByteArrayOutputStream(buffSize);
            byte[] temp = new byte[buffSize];
            int size = 0;
            while ((size = bufIn.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            return out.toByteArray();
        }finally {
            if(bufIn != null){
                bufIn.close();
            }
            if(out != null){
                out.close();
            }
        }
    }

    /***
     * 合并字节数组
     * @param a
     * @return
     */
    public static byte[] mergeArray(byte[]... a) {
        // 合并完之后数组的总长度
        int index = 0;
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i].length;
        }
        byte[] result = new byte[sum];
        for (int i = 0; i < a.length; i++) {
            int lengthOne = a[i].length;
            if(lengthOne==0){
                continue;
            }
            // 拷贝数组
            System.arraycopy(a[i], 0, result, index, lengthOne);
            index = index + lengthOne;
        }
        return result;
    }
}
