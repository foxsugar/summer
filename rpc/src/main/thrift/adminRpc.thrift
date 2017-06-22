namespace java com.code.server.rpc.idl

include "response.thrift"

struct Rebate{
    1: i64 id,
    2:i64 userId,
    3:i32 refereeId,
    4:double rebateNum,
    5:i64 time,
    6:bool isHasReferee,


}
service AdminRPC{
    //返利
    i32 rebate(1:list<Rebate> rebates),

    //获得用户信息
    bool isExist(1:i64 refereeId),
}