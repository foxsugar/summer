package com.code.server.login.kafka;


import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.*;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.Constant;
import com.code.server.db.model.Record;
import com.code.server.db.model.ServerInfo;
import com.code.server.db.model.User;

import com.code.server.redis.service.UserRedisService;
import com.code.server.util.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.tools.nsc.doc.html.page.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by win7 on 2017/3/10.
 */

@Service
public class GameUserService {

    @Autowired
    UserRedisService userRedisService;

    @Autowired
    MsgProducer kafkaMsgProducer;

    /**
     * 给人充钱
     * @param player
     * @param accepterId
     * @param money
     * @return
     */
    public int giveOtherMoney(Player player, Long accepterId,int money){
        UserService userService = SpringUtil.getBean(UserService.class);
        User user = player.getUser();

        if(money<=0 || user.getMoney() < money){
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }

        Player accepterPlayer = GameManager.getInstance().getPlayers().get(accepterId);

        if (accepterPlayer != null) {
            User userAccepter = accepterPlayer.getUser();
            user.setMoney(user.getMoney()-money);
            accepterPlayer.getUser().setMoney(userAccepter.getMoney() + money);
            GameManager.getInstance().getSaveUser2DB().add(userAccepter);

        } else {
            ThreadPool.getInstance().executor.execute(()->{

                User accepter = userService.getUserByUserId(accepterId);
                if(accepter==null){
                    player.sendMsg("userService","giveOtherMoney",ErrorCode.NOT_HAVE_THIS_ACCEPTER);
                    return;
                }
                user.setMoney(user.getMoney()-money);
                accepter.setMoney(accepter.getMoney()+money);
                userService.save(accepter);

            });
        }
        GameManager.getInstance().getSaveUser2DB().add(user);

        player.sendMsg("userService","giveOtherMoney",0);
        return 0;
    }


    /**
     * 获取昵称
     * @param player
     * @param accepterId
     * @return
     */
    public int getNickNamePlayer(Player player, Long accepterId){
        ThreadPool.getInstance().executor.execute(()->{
            UserService userService = SpringUtil.getBean(UserService.class);
            User accepter = userService.getUserByUserId(accepterId);
            if(accepter==null){
                player.sendMsg("userService","getNickNamePlayer",ErrorCode.NOT_HAVE_THIS_ACCEPTER);
                return;
            }
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("nickname", (URLDecoder.decode(accepter.getUsername(),"utf-8")));
            }catch (Exception e){
                player.sendMsg("userService","getNickNamePlayer",ErrorCode.NOT_HAVE_THIS_ACCEPTER);
                return;
            }
            player.sendMsg("userService","getNickNamePlayer",jSONObject);
        });
        return 0;
    }


    public int getUserRecodeByUserId(Player player, int type){
        User user = player.getUser();
        List<Record.RoomRecord> records = user.getRecord().getRoomRecords().get(type);
        List<Record.RoomRecord> recordNew = new ArrayList<Record.RoomRecord>();
        if(records!= null){
            for (Record.RoomRecord roomRecord:records) {
                boolean temp = false;
                a:for (Record.UserRecord userRecord:roomRecord.getRecords()) {
                    if(userRecord.getScore()!=0.0){
                        temp=true;
                        break a;
                    }
                }
                if(temp){
                    recordNew.add(roomRecord);
                }
            }
        }
        player.sendMsg("userService","getUserRecodeByUserId",recordNew);
        return 0;
    }


    private void sendMsg(long userId, int partition,Object msg){
        kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, partition,""+userId,msg);
    }
    private void sendMsg(KafkaMsgKey msgKey,Object msg){
        kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, msgKey.getPartition(),""+msgKey.getUserId(),msg);
    }



    public int getUserMessage(KafkaMsgKey msgKey){
        UserBean userBean = userRedisService.getUserBean(msgKey.getUserId());
        if (userBean == null) {
            return  ErrorCode.YOU_HAVE_NOT_LOGIN;
        }
        ResponseVo vo = new ResponseVo("userService", "getUserMessage", userBean);
        sendMsg(msgKey, vo);

        return 0;
    }



    public int getRecord(Player player,int type) {
        User user = player.getUser();
        user.getRecord().getRoomRecords().get(type);


        return 0;
    }


    public int bindReferrer(Player player, int referrerId){
        User user = player.getUser();
        if(referrerId<=0 || user.getReferee()!=0){
            return ErrorCode.CAN_NOT_BING_REFERRER;
        }
        ThreadPool.getInstance().executor.execute(()->{

            boolean isExist = RpcManager.getInstance().referrerIsExist(referrerId);
            if(!isExist){
                player.sendMsg("userService","bindReferrer",ErrorCode.REFERRER_NOT_EXIST);
                return;
            }
            user.setReferee(referrerId);
            user.setMoney(user.getMoney() + 10);
            GameManager.getInstance().getSaveUser2DB().add(user);
            player.sendMsg("userService","bindReferrer",0);
        });

        return 0;
    }

    public int checkOpenId(final String openId,String username, final String image,int sex,ChannelHandlerContext ctx){

        ThreadPool.getInstance().executor.execute(()->{
            ResponseVo vo = null;
            UserService userService = SpringUtil.getBean(UserService.class);

            Player player = null;
            if(GameManager.getInstance().openId_uid.containsKey(openId)){
                long userId = GameManager.getInstance().openId_uid.get(openId);
                if (GameManager.getInstance().players.containsKey(userId)) {
                    player = GameManager.getInstance().players.get(userId);
                }
            }
            User user = null;
            if (player != null) {
                user = player.getUser();
                //todo 踢人
                if (ctx != player.getCtx()) {
                    sendExit(player);
                }
            } else {
                user = userService.getUserByOpenId(openId);
            }

            String img = image;
            if(img == null || img.equals("")){
                img = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=253777390,947512827&fm=23&gp=0.jpg/96";

            }

            if(user == null) {
                user = new User();
//                user.setId(0);
//                user.setUserId(GameManager.getInstance().nextId());
                user.setOpenId(openId);
                user.setAccount(UUID.randomUUID().toString());
                user.setPassword("111111");
                try {
                    user.setUsername(URLDecoder.decode(username, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                user.setImage(img);
                user.setSex(sex);
                user.setVip(0);
                user.setUuid("0");
                user.setMoney(GameManager.getInstance().constant.getInitMoney());
                userService.save(user);

                doLogin(user,ctx);
                vo = new ResponseVo("userService", "checkOpenId", getUserVo(user));
                MsgDispatch.sendMsg(ctx,vo);

            }else{
                try {
                    user.setUsername(URLDecoder.decode(username, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                user.setImage(image);
                user.setSex(sex);
                userService.save(user);

                doLogin(user,ctx);
                vo = new ResponseVo("userService", "checkOpenId", getUserVo(user));
                MsgDispatch.sendMsg(ctx,vo);

            }
        });

        return 0;
    }

    public static void main(String[] args) {
        String s = "家用饮水机";
        String s2 = "";
        try {
            String ss = URLDecoder.decode(s,"utf-8");
            System.out.println(ss);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private User createUser(String account, String password){
        User newUser = new User();
//        newUser.setId(-100);
//        newUser.setUserId(GameManager.getInstance().nextId());
        newUser.setAccount(account);
        newUser.setPassword(password);
        newUser.setOpenId(""+GameManager.getInstance().nextId());
        try {
            newUser.setUsername(URLDecoder.decode(account, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        newUser.setImage("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=253777390,947512827&fm=23&gp=0.jpg/96");
        newUser.setSex(1);
        newUser.setVip(0);
        newUser.setUuid("0");
        newUser.setMoney(GameManager.getInstance().constant.getInitMoney());

        return newUser;
    }

    public int getUserMessage(long userId,ChannelHandlerContext ctx) {

        ThreadPool.getInstance().executor.execute(()->{
            UserService userService = SpringUtil.getBean(UserService.class);
            Player player  = GameManager.getInstance().players.get(userId);
            User user = null;
            if (player != null) {
                user = player.getUser();
            } else {
                user = userService.getUserByUserId(userId);
            }
            ResponseVo vo = new ResponseVo("userService","getUserMessage",getUserVo(user));
            MsgDispatch.sendMsg(ctx,vo);
        });
        return 0;
    }

    public int getUserImage(long userId,ChannelHandlerContext ctx) {
        ThreadPool.getInstance().executor.execute(()->{
            UserService userService = SpringUtil.getBean(UserService.class);
            Player player  = GameManager.getInstance().players.get(userId);
            User user = null;
            if (player != null) {
                user = player.getUser();
            } else {
                user = userService.getUserByUserId(userId);
            }

            JSONObject jSONObject = new JSONObject();
            jSONObject.put("Image", user.getImage());

            ResponseVo vo = new ResponseVo("userService","getUserImage",jSONObject);
            MsgDispatch.sendMsg(ctx,vo);
        });
        return 0;
    }


    public int register(long userId,ChannelHandlerContext ctx) {

        ThreadPool.getInstance().executor.execute(()->{
            UserService userService = SpringUtil.getBean(UserService.class);
            Player player  = GameManager.getInstance().players.get(userId);
            User user = null;
            if (player != null) {
                user = player.getUser();
            } else {
                user = userService.getUserByUserId(userId);
            }
            if(user==null){
                ResponseVo vo = new ResponseVo("userService","register",0);
                MsgDispatch.sendMsg(ctx,vo);
            }else{
                ResponseVo vo = new ResponseVo("userService","register",getUserVo(user));
                MsgDispatch.sendMsg(ctx,vo);
            }
        });
        return 0;



    }
}
