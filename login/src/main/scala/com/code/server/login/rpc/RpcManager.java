package com.code.server.login.rpc;


import com.code.server.login.config.ServerConfig;
import com.code.server.rpc.client.AdminRpcClient;
import com.code.server.rpc.client.GameRpcClient;
import com.code.server.rpc.client.TransportManager;
import com.code.server.rpc.idl.AdminRPC;
import com.code.server.rpc.idl.GameRPC;
import com.code.server.rpc.idl.Order;
import com.code.server.rpc.idl.Rebate;
import com.code.server.rpc.server.GameRpcServer;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by sunxianping on 2017/3/30.
 */
public class RpcManager {
    private final Logger logger = LoggerFactory.getLogger(RpcManager.class);
    private static RpcManager instance;
    private ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

    //    private TTransport adminTransport;
    private AdminRPC.Client adminRpcClient;

    private GameRPC.Client gameRpcClient;

    public TServer gameRpcServer;

    private List<Rebate> failedRebate = new CopyOnWriteArrayList<>();

    private static Object rpcLock = new Object();

    private RpcManager() {
    }

    public static RpcManager getInstance() {
        if (instance == null) {
            instance = new RpcManager();
        }
        return instance;
    }


    private TTransport getTransport() throws TTransportException {
        return TransportManager.getTransport(serverConfig.getAdminRpcHost().trim(), serverConfig.getAdminRpcPort());
    }


    public static void main(String[] args) {
//        testGame(1);
        try {
//            for(int i=0;i<10001108;i++){

//            charge("123.56.8.137", 10239, 1000);
//            charge("192.168.1.132", 777, 1000);
//            charge("47.92.37.165", 1, 10000);
//            charge("47.92.37.165", 2, 10000);
//            charge("47.92.37.165", 3, 10000);

//            changeInit("47.92.37.165", 10000);
//            charge("183.60.233.86", 1, 10000);
//            charge("183.60.233.86", 2, 10000);
//            charge("183.60.233.86", 3, 10000);
//            charge("183.60.233.86", 4, 10000);
//            charge("183.60.233.86", 5, 10000);
//            charge("183.60.233.86", 6, 10000);

//            charge("47.92.94.105", 71, -2105);
            charge("192.168.1.173", 10001124, 2000);
//            bingReferr("183.60.233.86",10007534 ,-309775);

//            charge("192.168.1.132", 999, 1000);
//            }
//            charge("123.56.8.137",10002,100);
//            charge("123.56.8.137",10003,100);
//            charge("123.56.8.137",10004,100);

//            appCheck("192.168.1.132");
        } catch (TException e) {
            e.printStackTrace();
        }

    }

    private static void testAdmin(int count) {

        for (int i = 0; i < count; i++) {

//                System.out.println(i);

            TTransport adminTransport = null;
            try {
                adminTransport = TransportManager.getTransport("192.168.1.132", 9999);
            } catch (TTransportException e) {
                e.printStackTrace();
            }
//            TTransport adminTransport = TransportManager.getTransport("127.0.0.1",8090);
            AdminRPC.Client adminRpcClient = null;
            try {
                adminRpcClient = AdminRpcClient.getAClient(adminTransport);
            } catch (TTransportException e) {
                e.printStackTrace();
            }
            List<Rebate> list = new ArrayList<>();
            Rebate rebate = new Rebate();
            rebate.setId(1L);
            rebate.setUserId(1L);
            rebate.setTime(2L);
            rebate.setIsHasReferee(true);
            rebate.setRefereeId(1);
            list.add(rebate);
            try {
                adminRpcClient.rebate(list);
            } catch (TException e) {
                e.printStackTrace();
            }
            adminTransport.close();
        }
    }

    private static void bingReferr(String ip, long userId, int ref) throws TException {
        TTransport adminTransport = new TFramedTransport(new TSocket(ip, 9090));
        adminTransport.open();
//        TTransport adminTransport = TransportManager.getTransport(ip, 9090);

        GameRPC.Client client = GameRpcClient.getAClient(adminTransport);
        client.bindReferee(userId, ref);
//

        adminTransport.close();
    }
    private static void charge(String ip, int id, int num) throws TException {

        TTransport adminTransport = new TFramedTransport(new TSocket(ip, 9090));
        adminTransport.open();
//        TTransport adminTransport = TransportManager.getTransport(ip, 9090);

        GameRPC.Client client = GameRpcClient.getAClient(adminTransport);
//                    client.getUserInfo(1);
        Order order = new Order();
        order.setUserId(id);
        order.setNum(num);
        order.setType(1);
        int rtn = client.charge(order);
        adminTransport.close();
        //充值成功
        if (rtn == 0) {
            //todo 插入一条充值记录
        }
    }

    private static void changeInit(String ip, int num) throws TException {
        TTransport adminTransport = TransportManager.getTransport(ip, 9090);

        GameRPC.Client client = GameRpcClient.getAClient(adminTransport);
//                    client.getUserInfo(1);
        client.modifyInitMoney(10000);
        adminTransport.close();
    }

    private static void appCheck(String ip) throws TException {

        TTransport adminTransport = TransportManager.getTransport(ip, 9090);

        GameRPC.Client client = GameRpcClient.getAClient(adminTransport);
        client.modifyAppleCheck(1);

    }

    private static void testGame(int count) {
        for (int i = 0; i < count; i++) {
            try {
                synchronized (rpcLock) {


                    TTransport adminTransport = TransportManager.getTransport("192.168.1.132", 9090);

                    GameRPC.Client client = GameRpcClient.getAClient(adminTransport);
//                    client.getUserInfo(1);
                    Order order = new Order();
                    order.setUserId(1);
                    order.setNum(1);
                    order.setType(1);
                    client.charge(order);
                    adminTransport.close();
                }

            } catch (Exception e) {

            }
        }

    }


    public void sendRpcRebat(List<Rebate> rebates) {
        try {
            TTransport tTransport = getTransport();
            AdminRPC.Client adminRpcClient = AdminRpcClient.getAClient(tTransport);
            adminRpcClient.rebate(rebates);
            tTransport.close();
        } catch (TException e) {
            logger.error("send rpc rebat error ", e);
            //todo 发送不成功处理
//            failedRebate.addAll(rebates);
        }
    }

    public boolean referrerIsExist(long referrer) {
        boolean result = false;
        try {
            TTransport tTransport = getTransport();
            AdminRPC.Client adminRpcClient = AdminRpcClient.getAClient(tTransport);
            result = adminRpcClient.isExist(referrer);
            tTransport.close();
        } catch (TException e) {
            logger.error("send rpc rebat error ", e);
        }
        return result;
    }


    public void startGameRpcServer() {
        ThreadPool.getInstance().executor.execute(() -> {
            try {
                if (serverConfig.getStartNewGameRpc() == 1) {
                    System.out.println("start new----------");
                    gameRpcServer = GameRpcServer.StartServer(serverConfig.getGameRpcServerPort(), new GameRpcNewHandler());
                } else {
                    gameRpcServer = GameRpcServer.StartServer(serverConfig.getGameRpcServerPort(), new GameRpcHandler());
                }
            } catch (TTransportException e) {
                e.printStackTrace();
                logger.error("启动rpc失败");

            }
        });
    }

    public void checkGameRpcServerWork() {
        long time = System.currentTimeMillis();
        GameTimer.addTimerNode(new TimerNode(time, 1000L * 5, true, () -> {
            if (RpcManager.getInstance().gameRpcServer != null && !RpcManager.getInstance().gameRpcServer.isServing()) {
                RpcManager.getInstance().gameRpcServer.stop();
                RpcManager.getInstance().gameRpcServer = null;
                ThreadPool.getInstance().executor.execute(() -> {

                    RpcManager.getInstance().startGameRpcServer();

                });
            }
        }));
    }


}
