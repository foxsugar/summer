package com.code.server.game.poker.hitgoldflower;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.*;

public class ListUtils {


    public static Map<Integer,Integer> cardCode = new HashMap<>();
    static {
        cardCode.put(1,2);
        cardCode.put(2,3);
        cardCode.put(3,4);
        cardCode.put(4,5);

        cardCode.put(5,50);
        cardCode.put(6,51);
        cardCode.put(7,52);
        cardCode.put(8,53);

        cardCode.put(9,46);
        cardCode.put(10,47);
        cardCode.put(11,48);
        cardCode.put(12,49);

        cardCode.put(13,42);
        cardCode.put(14,43);
        cardCode.put(15,44);
        cardCode.put(16,45);

        cardCode.put(17,38);
        cardCode.put(18,39);
        cardCode.put(19,40);
        cardCode.put(20,41);

        cardCode.put(21,34);
        cardCode.put(22,35);
        cardCode.put(23,36);
        cardCode.put(24,37);

        cardCode.put(25,30);
        cardCode.put(26,31);
        cardCode.put(27,32);
        cardCode.put(28,33);

        cardCode.put(29,26);
        cardCode.put(30,27);
        cardCode.put(31,28);
        cardCode.put(32,29);

        cardCode.put(33,22);
        cardCode.put(34,23);
        cardCode.put(35,24);
        cardCode.put(36,25);

        cardCode.put(37,18);
        cardCode.put(38,19);
        cardCode.put(39,20);
        cardCode.put(40,21);

        cardCode.put(41,14);
        cardCode.put(42,15);
        cardCode.put(43,16);
        cardCode.put(44,17);

        cardCode.put(45,10);
        cardCode.put(46,11);
        cardCode.put(47,12);
        cardCode.put(48,13);

        cardCode.put(49,6);
        cardCode.put(50,7);
        cardCode.put(51,8);
        cardCode.put(52,9);
    }


    /** 
     * 对list的元素按照多个属性名称排序, 
     * list元素的属性可以是数字（byte、short、int、long、float、double等，支持正数、负数、0）、char、String、java.util.Date 
     *  
     *
     *            list元素的属性名称 
     * @param isAsc 
     *            true升序，false降序 
     */  
    public static <E> void sort(List<E> list, final boolean isAsc, final String... sortnameArr) {  
        Collections.sort(list, new Comparator<E>() {  
  
            public int compare(E a, E b) {  
                int ret = 0;  
                try {  
                    for (int i = 0; i < sortnameArr.length; i++) {  
                        ret = ListUtils.compareObject(sortnameArr[i], isAsc, a, b);  
                        if (0 != ret) {  
                            break;  
                        }  
                    }  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
                return ret;  
            }  
        });  
    }  
      
    /** 
     * 给list的每个属性都指定是升序还是降序 
     *  
     * @param list 
     * @param sortnameArr  参数数组 
     * @param typeArr      每个属性对应的升降序数组， true升序，false降序 
     */  
  
    public static <E> void sort(List<E> list, final String[] sortnameArr, final boolean[] typeArr) {  
        if (sortnameArr.length != typeArr.length) {  
            throw new RuntimeException("属性数组元素个数和升降序数组元素个数不相等");  
        }  
        Collections.sort(list, new Comparator<E>() {  
            public int compare(E a, E b) {  
                int ret = 0;  
                try {  
                    for (int i = 0; i < sortnameArr.length; i++) {  
                        ret = ListUtils.compareObject(sortnameArr[i], typeArr[i], a, b);  
                        if (0 != ret) {  
                            break;  
                        }  
                    }  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
                return ret;  
            }  
        });  
    }  
  
    /** 
     * 对2个对象按照指定属性名称进行排序 
     *  
     * @param sortname 
     *            属性名称 
     * @param isAsc 
     *            true升序，false降序 
     * @param a 
     * @param b 
     * @return 
     * @throws Exception 
     */  
    private static <E> int compareObject(final String sortname, final boolean isAsc, E a, E b) throws Exception {  
        int ret;  
        Object value1 = ListUtils.forceGetFieldValue(a, sortname);  
        Object value2 = ListUtils.forceGetFieldValue(b, sortname);  
        String str1 = value1.toString();  
        String str2 = value2.toString();  
        if (value1 instanceof Number && value2 instanceof Number) {  
            int maxlen = Math.max(str1.length(), str2.length());  
            str1 = ListUtils.addZero2Str((Number) value1, maxlen);  
            str2 = ListUtils.addZero2Str((Number) value2, maxlen);  
        } else if (value1 instanceof Date && value2 instanceof Date) {  
            long time1 = ((Date) value1).getTime();  
            long time2 = ((Date) value2).getTime();  
            int maxlen = Long.toString(Math.max(time1, time2)).length();  
            str1 = ListUtils.addZero2Str(time1, maxlen);  
            str2 = ListUtils.addZero2Str(time2, maxlen);  
        }  
        if (isAsc) {  
            ret = str1.compareTo(str2);  
        } else {  
            ret = str2.compareTo(str1);  
        }  
        return ret;  
    }  
  
    /** 
     * 给数字对象按照指定长度在左侧补0. 
     *  
     * 使用案例: addZero2Str(11,4) 返回 "0011", addZero2Str(-18,6)返回 "-000018" 
     *  
     * @param numObj 
     *            数字对象 
     * @param length 
     *            指定的长度 
     * @return 
     */  
    public static String addZero2Str(Number numObj, int length) {  
        NumberFormat nf = NumberFormat.getInstance();  
        // 设置是否使用分组  
        nf.setGroupingUsed(false);  
        // 设置最大整数位数  
        nf.setMaximumIntegerDigits(length);  
        // 设置最小整数位数  
        nf.setMinimumIntegerDigits(length);  
        return nf.format(numObj);  
    }  
  
    /** 
     * 获取指定对象的指定属性值（去除private,protected的限制） 
     *  
     * @param obj 
     *            属性名称所在的对象 
     * @param fieldName 
     *            属性名称 
     * @return 
     * @throws Exception 
     */  
    public static Object forceGetFieldValue(Object obj, String fieldName) throws Exception {  
        Field field = obj.getClass().getDeclaredField(fieldName);  
        Object object = null;  
        boolean accessible = field.isAccessible();  
        if (!accessible) {  
            // 如果是private,protected修饰的属性，需要修改为可以访问的  
            field.setAccessible(true);  
            object = field.get(obj);  
            // 还原private,protected属性的访问性质  
            field.setAccessible(accessible);  
            return object;  
        }  
        object = field.get(obj);  
        return object;  
    }



}  