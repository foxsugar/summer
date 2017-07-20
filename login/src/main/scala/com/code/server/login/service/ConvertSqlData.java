package com.code.server.login.service;

import com.code.server.db.model.User;
import com.code.server.db.utils.JdbcUtils;
import org.apache.commons.dbutils.ResultSetHandler;
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

//    @Autowired
//    private IUserDao userDao;


    public static Object getUsers() throws SQLException {
        Object[] params = new Object[]{};
        return JdbcUtils.query("select id,account,password,username,money,openId,marquee,sex from t_user where id>=10001081 limit 3", params, new ResultSetHandler() {

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
    public void convert(){

    }

    public static void main(String[] args) throws SQLException {
        getUsers();
    }

}
