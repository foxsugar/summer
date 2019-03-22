package com.code.server.game.poker.paijiu

import com.code.server.constant.data.{DataManager, StaticDataProto}
import com.code.server.constant.response.PlayerCardInfoPaijiuVo
import com.code.server.util.JsonUtil

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
  * Created by sunxianping on 2017/7/21.
  */
object Test {

  def testRand() = {
    val rand = new Random()
    var l = List(1, 13, 5, 9,2,1,3,4,5)
    rand.setSeed(1)
    rand.shuffle(l)

  }

  def testSame() = {
    val l1 = List(3,2,1)
    val l2 = List(1,2,3)
    val l3 = "1,2,3"
    val list3 = l3.split(",").map(s=>s.toInt).toList
    println(l1.diff(list3).isEmpty)
  }

  def test1() = {
    val playerCardInfoPaijiu = new PlayerCardInfoPaijiu
    println(playerCardInfoPaijiu.bet)
    playerCardInfoPaijiu.bet = new Bet(1,1,1,1)
    println(playerCardInfoPaijiu.bet)
  }

  def testAdd()={
    var l1 = List(1)
    var l2 = List(2)
    l1.+:(2)
    l1++= l2

    print(l1)
  }
  def testMap()={
    var m1 = Map(1->22,2->3)
     m1 += (4->6)
    m1 -= 4

    print(JsonUtil.toJson(m1.asJava))
  }

  def testMuList = {
    var l1 = ArrayBuffer(1,3,4)
    l1 +=2
    print(l1)

  }

  def testList = {
    var l1 = List(1,2,3)
    l1.asJava.add(4)
    print(l1)
  }
  def testShuffle() = {
    val list = List(1,23,5,5,7,8,9,2)
    val rand = Random
//    rand.setSeed(rand.nextInt(100))
//    rand.shuffle(list)
    print( rand.shuffle(list).head)
    print(list)
    print( rand.shuffle(list))

  }
  def testSild = {
    val list = List(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16)
//    val list = List(1,2,3,4,5,6,7,8,9,10,11,12)
    print(list.sliding(2,2).toList)
    print(list)
    print(list.sliding(2,2).toList(1))
  }
  def testslice(): Unit ={
    val list = List(0,1,2,3,4,5,6,7,8)
    print(list.slice(0,8))
  }
  def testMap1 = {
    var map = mutable.Map(1 -> 2, 2 -> 3).asJava
    map.put(3, 6)
    print(map)
  }

  def testPlayerVo():Unit = {
    var pl = new PlayerCardInfoPaijiuVo()
    var playerCardInfoPaijiu = new PlayerCardInfoPaijiu



    print(pl)

  }
//  def testCardGroup():Unit = {
////    val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
//    //加载数据
//    DataManager.initData("E:\\summer\\data\\static_data.json")
//    val gamePaijiu = new GamePaijiu
//    gamePaijiu.getGroupScore("15,30")
//  }

  def testIn(): Unit ={
    DataManager.initData("E:\\summer\\data\\static_data.json")
    DataManager.data.getLaotiePaijiuCardGroupScoreDataMap.get("zero").getScore
    val d: StaticDataProto.DataManager = DataManager.data
    val method = d.getClass.getDeclaredMethod("getLaotiePaijiuCardGroupScoreDataMap")
    val m = method.invoke(d)
//    print(m.asInstanceOf[java.util.Map[String,Object]])

    val mp = m.asInstanceOf[java.util.Map[String,Object]]
    val o = mp.get("zero")
    val scoreMethod = o.getClass.getDeclaredMethod("getScore")
    val score = scoreMethod.invoke(o)
    print(score)

//    print(mp)






  }


  def testGetScore(group:String) = {
    DataManager.initData("E:\\summer\\data\\static_data.json")
    val d: StaticDataProto.DataManager = DataManager.data
//    val dataStr = DataManager.data.getRoomDataMap.get(this.roomPaijiu.getGameType).getPaijiuDataName
//    val dataMethodName = "get" + dataStr + "GroupDataMap"
    val method = d.getClass.getDeclaredMethod("getLaotiePaijiuCardGroupDataMap")
    val m = method.invoke(d)
    val mp = m.asInstanceOf[java.util.Map[String,Object]]
    val o = mp.get(group)
    val nameMethod = o.getClass.getDeclaredMethod("getName")
    val name = nameMethod.invoke(o)
    print(name)
    //    name.asInstanceOf[String]
//    getGroupScoreByName(name.asInstanceOf[String])
  }



  def testFilter():Unit = {
    var list = List(1,2,3)
    list = list.filter(_!=2)
    println(list)
  }


  def main(args: Array[String]): Unit = {
//    test1()
//    testSame()
//    testAdd()
//    testMap()
//    testMuList
//    testList
//    testShuffle
//    testSild
//    testMap1
//    testPlayerVo
//    testCardGroup()
//    testslice
//    testIn
//    testFilter
    testGetScore("17,19")
  }
}
