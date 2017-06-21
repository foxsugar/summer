namespace java com.code.server.rpc.idl


struct User {
  1: i64 id = 0,
  2: string username,
  7: double money,
  13:double gold,

}

enum ChargeType{
      money = 1,
      gold = 2
}
struct Order{
    1:i64 userId,
    2:double num,
    3:i32 type,
    4:string token,
    5:i32 agentId,
    6:i64 id,
}

service GameRPC{
    //充值
    i32 charge(Order order),

   //获得用户信息
   User getUserInfo(i64 userId),

   //交易库存斗
   i32 exchange(Order order),

    //修改公告
   i32 modifyMarquee(string str),
    //修改下载地址
   i32 modifyDownload(string str),
    //修改安卓版本
   i32 modifyAndroidVersion(string str),
    //修改ios版本
   i32 modifyIOSVersion(string str),
   //shutdown
   i32 shutdown(),
   //修改初始金钱
   i32 modifyInitMoney(i32 money),
   //是否苹果审查
   i32 modifyAppleCheck(i32 status),

   i32 modifyDownload2(string str)
}

