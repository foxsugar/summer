package com.code.server.rpc.server;

import com.code.server.rpc.idl.GameRPC;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by sunxianping on 2017/3/29.
 */
public class GameRpcServer {

    public static TServer StartServer(int port,GameRPC.AsyncIface iface) throws TTransportException {
        TServer server = null;
        try {

            TProcessor tprocessor = new GameRPC.AsyncProcessor<>(iface);
            TNonblockingServerSocket serverTransport = null;
            serverTransport = new TNonblockingServerSocket(port);
            TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
            tArgs.maxReadBufferBytes = 1024 * 1024L;
            tArgs.processor(tprocessor);
            tArgs.transportFactory(new TFramedTransport.Factory());
            tArgs.protocolFactory(new TBinaryProtocol.Factory());



            server = new TThreadedSelectorServer(tArgs);
            server.serve();

        } catch (Exception e) {
            System.out.println("-------------------------------------");
        e.printStackTrace();
        }
        return server;
    }


    public static void main(String[] args) {

    }
}
