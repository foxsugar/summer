package com.code.server.rpc.client;

import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by sunxianping on 2017/3/30.
 */
public class TransportManager {

    public static TTransport getTransport(String host, int port) throws TTransportException {
        TTransport transport = new TFramedTransport(new TSocket(host.trim(), port));
        transport.open();
        return transport;

    }

}
