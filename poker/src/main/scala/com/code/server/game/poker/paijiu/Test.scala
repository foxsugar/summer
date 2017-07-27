package com.code.server.game.poker.paijiu

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
    playerCardInfoPaijiu.bet = new Bet(1,1)
    println(playerCardInfoPaijiu.bet)
  }

  def main(args: Array[String]): Unit = {
//    test1()
    testSame()
  }
}
