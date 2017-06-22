package com.code.server.rpc.client;

import com.code.server.rpc.idl.GameRPC;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import static com.code.server.rpc.client.TransportManager.getTransport;

/**
 * Created by sunxianping on 2017/3/29.
 */
public class GameRpcClient {


    public static GameRPC.Client getAClient(String host, int port) throws TTransportException {
        TTransport transport = getTransport(host, port);
        TProtocol protocol = new TBinaryProtocol(transport);
        GameRPC.Client client = new GameRPC.Client(protocol);
        return client;
    }

    public static GameRPC.Client getAClient(TTransport transport) throws TTransportException {
        TProtocol protocol = new TBinaryProtocol(transport);
        GameRPC.Client client = new GameRPC.Client(protocol);
        return client;
    }



    public static void main(String [] args) {
        try {
            TTransport tTransport = TransportManager.getTransport("127.0.0.1", 9090);
            GameRPC.Client client = GameRpcClient.getAClient(tTransport);


            tTransport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }
}
