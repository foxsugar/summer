// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: game.proto

package com.code.server.grpc.idl;

public interface OrderOrBuilder extends
    // @@protoc_insertion_point(interface_extends:Order)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional int64 userId = 1;</code>
   */
  long getUserId();

  /**
   * <code>optional double num = 2;</code>
   */
  double getNum();

  /**
   * <code>optional int32 type = 3;</code>
   */
  int getType();

  /**
   * <code>optional string token = 4;</code>
   */
  java.lang.String getToken();
  /**
   * <code>optional string token = 4;</code>
   */
  com.google.protobuf.ByteString
      getTokenBytes();

  /**
   * <code>optional int32 agentId = 5;</code>
   */
  int getAgentId();

  /**
   * <code>optional int64 id = 6;</code>
   */
  long getId();

  /**
   * <code>map&lt;int32, string&gt; m = 7;</code>
   */
  int getMCount();
  /**
   * <code>map&lt;int32, string&gt; m = 7;</code>
   */
  boolean containsM(
      int key);
  /**
   * Use {@link #getMMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.Integer, java.lang.String>
  getM();
  /**
   * <code>map&lt;int32, string&gt; m = 7;</code>
   */
  java.util.Map<java.lang.Integer, java.lang.String>
  getMMap();
  /**
   * <code>map&lt;int32, string&gt; m = 7;</code>
   */

  java.lang.String getMOrDefault(
      int key,
      java.lang.String defaultValue);
  /**
   * <code>map&lt;int32, string&gt; m = 7;</code>
   */

  java.lang.String getMOrThrow(
      int key);
}