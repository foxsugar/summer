package com.code.server.game.poker.playseven;

import java.util.ArrayList;
import java.util.List;

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
public class Test {

    public static void main(String[] args) {
        List<Integer> sortList = new ArrayList<>();
        sortList.add(26);
        sortList.add(-26);
        sortList.add(22);
        sortList.add(-22);

        System.out.println(CardsUtil.duiNumZhu(sortList,2));
    }

}
