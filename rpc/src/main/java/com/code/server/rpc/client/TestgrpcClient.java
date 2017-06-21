package com.code.server.rpc.client;

import com.code.server.grpc.idl.GameServiceGrpc;
import com.code.server.grpc.idl.Order;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by sunxianping on 2017/4/27.
 */
public class TestgrpcClient {


    private final ManagedChannel channel;

    private final GameServiceGrpc.GameServiceBlockingStub blockingStub;

    /** Construct client connecting to gRPC server at {@code host:port}. */
    public TestgrpcClient(String host, int port) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true);
        channel = channelBuilder.build();
        blockingStub = GameServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }



    public static void main(String[] args) throws Exception {
        try {
            for(int i=0;i<10;i++){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int count =0;
                        for(int j=0;j<1000;j++){
                            count++;
                            TestgrpcClient client = new TestgrpcClient("127.0.0.1", 50051);

                            Order.Builder order = Order.newBuilder();
                            order.setId(1);
                            order.setType(1);
                            order.setNum(1);


                            client.blockingStub.charge(order.build());
                            try {
                                client.shutdown();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println(count);
                        }
                    }
                }).start();


            }

        } finally {

        }
    }
}
