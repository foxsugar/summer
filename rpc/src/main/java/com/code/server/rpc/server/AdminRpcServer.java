package com.code.server.rpc.server;

import com.code.server.rpc.idl.AdminRPC;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by sunxianping on 2017/3/30.
 */
public class AdminRpcServer {

    public static TServer StartServer(int port, AdminRPC.AsyncIface iface) throws TTransportException {
        TProcessor tprocessor = new AdminRPC.AsyncProcessor<>(iface);
        TNonblockingServerSocket serverTransport = null;
        serverTransport = new TNonblockingServerSocket(port);
        TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
        tArgs.processor(tprocessor);
        tArgs.maxReadBufferBytes = 1024 * 1024L;
        tArgs.transportFactory(new TFramedTransport.Factory());
        tArgs.protocolFactory(new TBinaryProtocol.Factory());
        TServer server = new TThreadedSelectorServer(tArgs);
        server.serve();
        return server;
    }

    public static void main(String[] args) {
    }
}
