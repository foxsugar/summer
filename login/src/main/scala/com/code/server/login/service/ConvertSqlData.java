package com.code.server.login.service;

import com.code.server.db.dao.IUserDao;
import com.code.server.db.model.User;
import com.code.server.db.utils.JdbcUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/7/20.
 */
@Service
public class ConvertSqlData {

    @Autowired
    private IUserDao userDao;



    public Object getUsers() throws SQLException {
        Object[] params = new Object[]{};
        return JdbcUtils.query("select id,account,password,username,money,openId,marquee,vip,sex from t_user where id>=10001082 ", params, new ResultSetHandler() {

            public Object handle(ResultSet rs) throws SQLException {
                List<User> result = new ArrayList<>();
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setMoney(rs.getDouble("money"));
                    user.setGold(rs.getDouble("marquee"));
                    user.setAccount(rs.getString("account"));
                    user.setSex(rs.getInt("sex"));
                    user.setOpenId(rs.getString("openId"));
                    user.setPassword(rs.getString("password"));
                    String vip = rs.getString("vip");
                    vip = vip.trim();
                    user.setReferee(Integer.valueOf(vip));
                    try {
                        String name = URLDecoder.decode(rs.getString("username"), "utf-8");
                        user.setUsername(name);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    System.out.println(user);
                    result.add(user);

                }
                return result;
            }
        });
    }
    public void convert() throws SQLException {
        for(User user : (List<User>)getUsers()){

            userDao.save(user);
        }
    }


    public static void main(String[] args) {
        try {
            String name = URLDecoder.decode("%F0%9F%8C%99%E9%A6%99%E8%8D%89%F0%9F%8C%BF%E9%A6%A8%E6%9C%88%F0%9F%8C%99","utf-8");
            System.out.println(name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
