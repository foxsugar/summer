package com.code.server.grpc.idl;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.2.0)",
    comments = "Source: game.proto")
public final class GameServiceGrpc {

  private GameServiceGrpc() {}

  public static final String SERVICE_NAME = "GameService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.code.server.grpc.idl.Order,
      com.code.server.grpc.idl.Response> METHOD_CHARGE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "GameService", "charge"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.code.server.grpc.idl.Order.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.code.server.grpc.idl.Response.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.code.server.grpc.idl.Request,
      com.code.server.grpc.idl.User> METHOD_GET_USER_INFO =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "GameService", "getUserInfo"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.code.server.grpc.idl.Request.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.code.server.grpc.idl.User.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.code.server.grpc.idl.Order,
      com.code.server.grpc.idl.Response> METHOD_EXCHANGE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "GameService", "exchange"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.code.server.grpc.idl.Order.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.code.server.grpc.idl.Response.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GameServiceStub newStub(io.grpc.Channel channel) {
    return new GameServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GameServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GameServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static GameServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GameServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class GameServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *充值
     * </pre>
     */
    public void charge(com.code.server.grpc.idl.Order request,
        io.grpc.stub.StreamObserver<com.code.server.grpc.idl.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CHARGE, responseObserver);
    }

    /**
     * <pre>
     *获得用户信息
     * </pre>
     */
    public void getUserInfo(com.code.server.grpc.idl.Request request,
        io.grpc.stub.StreamObserver<com.code.server.grpc.idl.User> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_USER_INFO, responseObserver);
    }

    /**
     * <pre>
     *交易库存斗
     * </pre>
     */
    public void exchange(com.code.server.grpc.idl.Order request,
        io.grpc.stub.StreamObserver<com.code.server.grpc.idl.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_EXCHANGE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_CHARGE,
            asyncUnaryCall(
              new MethodHandlers<
                com.code.server.grpc.idl.Order,
                com.code.server.grpc.idl.Response>(
                  this, METHODID_CHARGE)))
          .addMethod(
            METHOD_GET_USER_INFO,
            asyncUnaryCall(
              new MethodHandlers<
                com.code.server.grpc.idl.Request,
                com.code.server.grpc.idl.User>(
                  this, METHODID_GET_USER_INFO)))
          .addMethod(
            METHOD_EXCHANGE,
            asyncUnaryCall(
              new MethodHandlers<
                com.code.server.grpc.idl.Order,
                com.code.server.grpc.idl.Response>(
                  this, METHODID_EXCHANGE)))
          .build();
    }
  }

  /**
   */
  public static final class GameServiceStub extends io.grpc.stub.AbstractStub<GameServiceStub> {
    private GameServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GameServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GameServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GameServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     *充值
     * </pre>
     */
    public void charge(com.code.server.grpc.idl.Order request,
        io.grpc.stub.StreamObserver<com.code.server.grpc.idl.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CHARGE, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *获得用户信息
     * </pre>
     */
    public void getUserInfo(com.code.server.grpc.idl.Request request,
        io.grpc.stub.StreamObserver<com.code.server.grpc.idl.User> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_USER_INFO, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *交易库存斗
     * </pre>
     */
    public void exchange(com.code.server.grpc.idl.Order request,
        io.grpc.stub.StreamObserver<com.code.server.grpc.idl.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_EXCHANGE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GameServiceBlockingStub extends io.grpc.stub.AbstractStub<GameServiceBlockingStub> {
    private GameServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GameServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GameServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GameServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *充值
     * </pre>
     */
    public com.code.server.grpc.idl.Response charge(com.code.server.grpc.idl.Order request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CHARGE, getCallOptions(), request);
    }

    /**
     * <pre>
     *获得用户信息
     * </pre>
     */
    public com.code.server.grpc.idl.User getUserInfo(com.code.server.grpc.idl.Request request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_USER_INFO, getCallOptions(), request);
    }

    /**
     * <pre>
     *交易库存斗
     * </pre>
     */
    public com.code.server.grpc.idl.Response exchange(com.code.server.grpc.idl.Order request) {
      return blockingUnaryCall(
          getChannel(), METHOD_EXCHANGE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GameServiceFutureStub extends io.grpc.stub.AbstractStub<GameServiceFutureStub> {
    private GameServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GameServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GameServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GameServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *充值
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.code.server.grpc.idl.Response> charge(
        com.code.server.grpc.idl.Order request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CHARGE, getCallOptions()), request);
    }

    /**
     * <pre>
     *获得用户信息
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.code.server.grpc.idl.User> getUserInfo(
        com.code.server.grpc.idl.Request request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_USER_INFO, getCallOptions()), request);
    }

    /**
     * <pre>
     *交易库存斗
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.code.server.grpc.idl.Response> exchange(
        com.code.server.grpc.idl.Order request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_EXCHANGE, getCallOptions()), request);
    }
  }

  private static final int METHODID_CHARGE = 0;
  private static final int METHODID_GET_USER_INFO = 1;
  private static final int METHODID_EXCHANGE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GameServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GameServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CHARGE:
          serviceImpl.charge((com.code.server.grpc.idl.Order) request,
              (io.grpc.stub.StreamObserver<com.code.server.grpc.idl.Response>) responseObserver);
          break;
        case METHODID_GET_USER_INFO:
          serviceImpl.getUserInfo((com.code.server.grpc.idl.Request) request,
              (io.grpc.stub.StreamObserver<com.code.server.grpc.idl.User>) responseObserver);
          break;
        case METHODID_EXCHANGE:
          serviceImpl.exchange((com.code.server.grpc.idl.Order) request,
              (io.grpc.stub.StreamObserver<com.code.server.grpc.idl.Response>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class GameServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.code.server.grpc.idl.Game.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GameServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GameServiceDescriptorSupplier())
              .addMethod(METHOD_CHARGE)
              .addMethod(METHOD_GET_USER_INFO)
              .addMethod(METHOD_EXCHANGE)
              .build();
        }
      }
    }
    return result;
  }
}
