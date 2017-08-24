package com.code.server.game.mahjong.util

import java.util

import scala.collection.JavaConverters._

/**
  * Created by sunxianping on 2017/8/21.
  */
object HuWithHun {

  var count_hun = 0

  def test() = {
    val a = Array[Int](//                3, 2, 0, 0, 0, 0, 0, 0, 0,
      3, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0,
      2, 0, 3, 0, 3, 0, 3, 0, 0,
      0, 0, 0, 0,
      0, 0, 0)
    import java.util

    val huIndex = 0

    val (noHunCards, hunNum) = getNoHunCardsAndHunNum(a, huIndex)
    println("混的数量 : " + hunNum)
    //    for(c<- noHunCards if c !=0) println(c)

    var list: util.List[util.List[CardGroup]] = new util.ArrayList()
    var allList: util.List[util.List[CardGroup]] = new util.ArrayList()
    var groups: util.List[CardGroup] = new util.ArrayList()
    Hu1.testHun(true, noHunCards, list, allList, groups)

    System.out.println("配好的 : " + list)
    //有配成的
    if (list.size() < 0) {
      //如果有混 肯定全是混组成的牌型
      if (hunNum > 0) {
        //三个混的数量
        val hun3Num = hunNum / 3
        val isHasHun2 = hunNum % 3 == 2
        for (i <- 0 until hun3Num) {
          list.forEach(l => {
            l.add(new CardGroup(Hu1.CARD_GROUP_TYPE_THREE_HUN, -1, 3))
            if (isHasHun2) l.add(new CardGroup(Hu1.CARD_GROUP_TYPE_TWO_HUN_JIANG, -1, 2))
          })

        }
        if (isHasHun2) list.forEach(l => l.add(new CardGroup(Hu1.CARD_GROUP_TYPE_TWO_HUN_JIANG, -1, 2))
        )

      }

//      list
      println(list.size())
      println(list)
    } else {

      allList = Hu1.removeRepeat(allList)


      var complete: util.List[util.List[CardGroup]] = new util.ArrayList()
      var temp: util.List[CardGroup] = new util.ArrayList()
      //没有凑成附子
      if (allList.size() == 0) {
        println("没有凑成附子")
        getKaoPai(noHunCards, complete, temp, hunNum)
      } else {
        for (cardGroupList <- allList.asScala) {
          var newCards = for (c <- noHunCards) yield c
          val remainInfo = getRemainCards(newCards, cardGroupList)
//          var newComplete = new util.ArrayList(complete)
          getKaoPai(remainInfo._1, complete, cardGroupList, hunNum)
          println("组成的一半: "+cardGroupList)
          println("带混的最终牌型: " + complete)
        }
      }

      System.out.println("部分: " + allList)
      println("==================================hun==============")
      println(complete.size())
      println(complete)
//      complete
    }
  }


  /**
    * 获得混牌的类型 (包括顺延的一张)
    *
    * @param index
    * @return
    */
  def getHunCardType(index: Int): (Int, Int) = {

    val second = index match {
      case 8 => 0
      case 17 => 1
      case 26 => 18
      case 30 => 27
      case 33 => 31
      case _ => index + 1

    }
    (index, second)
  }


  /**
    * 获得去掉混的牌和混的数量
    *
    * @param cards
    * @param index
    * @return
    */
  def getNoHunCardsAndHunNum(cards: Array[Int], index: Int): (Array[Int], Int) = {
    val newCards = for (c <- cards) yield c
    val huns = getHunCardType(index)
    var count = 0
    for (index <- newCards.indices) {
      if (index == huns._1 || index == huns._2) {
        count += newCards(index)
        newCards(index) = 0
      }
    }
    (newCards, count)

  }

  /**
    * 剩余牌的数量
    *
    * @param cards
    * @param cardGroupList
    * @return
    */
  def getRemainCards(cards: Array[Int], cardGroupList: util.List[CardGroup]): (Array[Int], util.List[CardGroup]) = {
    for (cardGroup <- cardGroupList.asScala) {
      val cardIndex = cardGroup.card
      cardGroup.huType match {
        case Hu1.CARD_GROUP_TYPE_JIANG => cards(cardIndex) -= 2
        case Hu1.CARD_GROUP_TYPE_SHUN => {
          cards(cardIndex) -= 1
          cards(cardIndex + 1) -= 1
          cards(cardIndex + 2) -= 1
        }
        case Hu1.CARD_GROUP_TYPE_KE => cards(cardIndex) -= 3
      }
    }

    (cards, cardGroupList)

  }

  /**
    * 获得数组中牌的数量
    *
    * @param cards
    * @return
    */
  def getCardNum(cards: Array[Int]): Int = {
    var count = 0
    for (c <- cards) {
      count += c
    }
    count
  }


  /**
    * 获得需要两个混的牌组数量
    *
    * @param remainCardsNum
    * @param hunNum
    * @param needGroupNum
    * @param isHasJiang
    * @return
    */
  def getNeed2HunNum(remainCardsNum: Int, hunNum: Int, needGroupNum: Int, isHasJiang: Boolean): Int = {

    var result: util.List[util.List[Int]] = new util.ArrayList()

    val div = remainCardsNum / needGroupNum
    val rem = remainCardsNum % needGroupNum
    //剩余牌填充
    for (group <- 0 until needGroupNum) {
      val cardgroup: util.List[Int] = new util.ArrayList()
      for (i <- 0 until div) {
        cardgroup.add(0)
      }
      if (group <= rem - 1) {
        cardgroup.add(0)
      }

    }
    //混填充
    var index = rem
    for (i <- 0 until hunNum) {
      result.get(index).add(1)
      index += 1
      if (index > needGroupNum) {
        index = 0
      }
    }
    //混的个数为2的
    var resultNum = 0
    for (list <- result.asScala) {
      if (list.stream().filter((num) => num == 1).count() == 2) {
        resultNum += 1
      }
    }
    resultNum
  }


  def getKaoPai(cards: Array[Int], complete: util.List[util.List[CardGroup]], cardGroupList: util.List[CardGroup], hunNum: Int): Unit = {
    count_hun +=1
    println("-----dai hun --- " + count_hun)
    for (i <- cards.indices if cards(i) != 0) {
      //凑将
      if (hunNum > 0 && cards(i) >= 1 && !Hu1.isHasJiang(cardGroupList)) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu1.CARD_GROUP_TYPE_JIANG, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        //数量
        if (getCardNum(newCards) == 0) {
          Hu1.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1)
        }
      }

      //两张牌凑成顺

      //凑刻
      if (hunNum > 0 && cards(i) >= 2) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu1.CARD_GROUP_TYPE_KE, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 2
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu1.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 2)
        }
      }


      //凑顺 前
      if (hunNum > 0 && isHasShun(i) && cards(i) >= 1 && cards(i + 1) >= 1 && i != 0 && i != 9 && i != 18) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu1.CARD_GROUP_TYPE_SHUN, i - 1, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        newCards(i + 1) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu1.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1)
        }
      }

      //凑顺 后
      if (hunNum > 0 && isHasShun(i) && cards(i) >= 1 && cards(i + 1) >= 1) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu1.CARD_GROUP_TYPE_SHUN, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        newCards(i + 1) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu1.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1)
        }
      }

      //凑顺 中间
      if (hunNum > 0 && isHasShun(i) && cards(i) >= 1 && cards(i + 2) >= 1) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu1.CARD_GROUP_TYPE_SHUN, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        newCards(i + 2) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu1.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1)
        }
      }

      //两个混
      if (hunNum > 1) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu1.CARD_GROUP_TYPE_TWO_HUN, i, 2))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 2 == 0) {
          Hu1.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 2)
        }
      }

      //两个混凑将

      if (hunNum > 1 && !Hu1.isHasJiang(cardGroupList)) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu1.CARD_GROUP_TYPE_TWO_HUN_JIANG, i, 2))
        var newCards = for (c <- cards) yield c

        //数量
        if (getCardNum(newCards) == 0 && hunNum - 2 == 0) {
          Hu1.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 2)
        }
      }

      //三个混
      if (hunNum > 2 && hunNum - 3 == 0) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu1.CARD_GROUP_TYPE_THREE_HUN, i, 3))
        var newCards = for (c <- cards) yield c

        //数量
        if (getCardNum(newCards) == 0) {
          Hu1.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 3)
        }
      }


    }

  }


  /**
    * 是否有顺的初级判断
    *
    * @param index
    * @return
    */
  def isHasShun(index: Int): Boolean = {
    if (index >= 26 || index == 8 || index == 17) false
    true
  }

  def main(args: Array[String]) = {
    test()
  }
}
