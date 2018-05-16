package com.code.server.game.poker.cow;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

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
public class JustTest {

    public static void main(String[] args) {
        //todo
        //fixme
        System.out.println(System.nanoTime());
        System.out.println(System.currentTimeMillis());
        System.out.println(Instant.now(Clock.system(ZoneId.of("+08:00"))));
    }

}
