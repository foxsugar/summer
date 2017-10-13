package com.code.server.game.poker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class TimeTest {

    public static void main(String[] args) {
        Date date = new Date(1504092557068L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println(sdf.format(date));

    }

}
