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
      5, 0, 1, 1, 1, 1, 1, 0, 0,
      1, 1, 1, 1, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0,
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
    Hu.testHun(true, noHunCards, list, allList, groups)

    System.out.println("配好的 : " + list)
    System.out.println("没配好的 : " + allList)


    //有配成的
    if (list.size() > 0) {
      //如果有混 肯定全是混组成的牌型
      if (hunNum > 0) {
        //三个混的数量
        val hun3Num = hunNum / 3
        val isHasHun2 = hunNum % 3 == 2

        for (i <- 0 until hun3Num) {
          list.forEach(l => l.add(new CardGroup(Hu.CARD_GROUP_TYPE_THREE_HUN, -1, 3)))

        }
        if (isHasHun2) list.forEach(l => l.add(new CardGroup(Hu.CARD_GROUP_TYPE_TWO_HUN_JIANG, -1, 2)))

      }

      //      list
      println(list.size())
      println(list)
    }
    var complete: util.List[util.List[CardGroup]] = new util.ArrayList(list)
    //    allList = Hu.removeRepeat(allList)

    println("去重的 : " + allList)


    var temp: util.List[CardGroup] = new util.ArrayList()
    //没有凑成附子
    if (allList.size() == 0) {
      println("没有凑成附子")

      if(hunIsEnough(noHunCards,temp,hunNum)){
        val remainCardNum = getCardNum(noHunCards)
        val isHasJiang = Hu.isHasJiang(temp)
        val needGroupSize = 5 - temp.size
        val need2HunNum = getNeed2HunNum(remainCardNum,hunNum,needGroupSize,isHasJiang)
        getKaoPai(noHunCards, complete, temp, hunNum,need2HunNum)
      }


    } else {
      for (cardGroupList <- allList.asScala) {
        var newCards = for (c <- noHunCards) yield c
        val (cs, cdl) = getRemainCards(newCards, cardGroupList)
        //是否可以胡的初次判断 混必须满足最少需求个数

        val remainCardNum = getCardNum(cs)
        if(hunIsEnough(cs,cdl,hunNum)){

          val isHasJiang = Hu.isHasJiang(cdl)
          val needGroupSize = 5 - cdl.size
          var need2HunNum = getNeed2HunNum(remainCardNum,hunNum,needGroupSize,isHasJiang)
          println("两个混的数量 = "+need2HunNum)
//          need2HunNum = 7
          getKaoPai(cs, complete, cardGroupList, hunNum,need2HunNum)
//          getKaoPai1(cs, complete, cardGroupList, hunNum)
          println("组成的一半: " + cardGroupList)
//          println("带混的最终牌型: " + complete)
        }
      }
    }
    complete = Hu.removeRepeat(complete)
    println("==================================hun==============")
    println(" 不是5 个附子的数量 = "+complete.stream().filter(l=>l.size()!=5).count())
    println(complete.size())
   // println(complete)

    var huCardTypeList = new util.ArrayList[HuCardType]()
    complete.forEach(li=>huCardTypeList.add(Hu.convert2HuCardType(li)))
    huCardTypeList.forEach(huType=>println(huType))
  }


  def isHu(cardGroupList:util.List[CardGroup],lastCard:Int):Boolean = {
    false
  }

  private def isHun(hun:util.List[Int],cardType:Int): Boolean ={
    for(h <- hun.asScala){
      if(cardType == h) return true
    }
    false
  }

  /**
    * 是否是捉五(有组成456万的顺)
    * @param huCardType
    * @param hun
    * @param lastCard
    * @return
    */
  def isZhuo5(huCardType: HuCardType,hun:util.List[Int],lastCard:Int):Boolean = {
    //不是五万 不是混 肯定不是捉5
    if(lastCard != 4 && !isHun(hun, lastCard)){
      return false
    }

    //三个都是混
    if(huCardType.hun3.size() >0 ) return true
    //两个混
    for(hun2Card <- huCardType.hun2.asScala){
      if(hun2Card == 3 || hun2Card==4 || hun2Card==5) return true
    }
    //一个混或者没有混
    for(shun <- huCardType.shun.asScala){
      if(shun == 3) return true
    }
    false
  }

  def isLong(huCardType: HuCardType, hun:util.List[Int], lastCard:Int):Boolean = {

    false
  }

  def hunIsEnough(cards:Array[Int], cardGroupList:util.List[CardGroup], hunNum:Int):Boolean = {
    val cardNum = getCardNum(cards)
    if(Hu.isHasJiang(cardGroupList)){
      hunNum >= cardNum/2
    }else{
      hunNum >= (cardNum-1)/2
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
        case Hu.CARD_GROUP_TYPE_JIANG => cards(cardIndex) -= 2
        case Hu.CARD_GROUP_TYPE_SHUN => {
          cards(cardIndex) -= 1
          cards(cardIndex + 1) -= 1
          cards(cardIndex + 2) -= 1
        }
        case Hu.CARD_GROUP_TYPE_KE => cards(cardIndex) -= 3
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

      result.add(cardgroup)
    }
    //混填充
    var index = rem
    for (i <- 0 until hunNum) {
      if (index >= needGroupNum) {
        index = 0
      }
      result.get(index).add(1)
      index += 1
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


  def getKaoPai(cards: Array[Int], complete: util.List[util.List[CardGroup]], cardGroupList: util.List[CardGroup], hunNum: Int , hun2Num:Int): Unit = {
    count_hun += 1
    println("-----dai hun --- " + count_hun)
    for (i <- cards.indices if cards(i) != 0) {
      //凑将
      if (hunNum > 0 && cards(i) >= 1 && !Hu.isHasJiang(cardGroupList)) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_JIANG, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        //数量
        if (getCardNum(newCards) == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1,hun2Num)
        }
      }

      //两张牌凑成顺

      //凑刻
      if (hunNum > 0 && cards(i) >= 2) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_KE, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 2
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 2,hun2Num)
        }
      }


      //凑顺 前
      if (hunNum > 0 && isHasShun(i) && cards(i) >= 1 && cards(i + 1) >= 1 && i != 0 && i != 9 && i != 18) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_SHUN, i - 1, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        newCards(i + 1) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1,hun2Num)
        }
      }

      //凑顺 后
      if (hunNum > 0 && isHasShun(i) && cards(i) >= 1 && cards(i + 1) >= 1) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_SHUN, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        newCards(i + 1) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1,hun2Num)
        }
      }

      //凑顺 中间
      if (hunNum > 0 && isHasShun(i) && cards(i) >= 1 && cards(i + 2) >= 1) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_SHUN, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        newCards(i + 2) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1,hun2Num)
        }
      }

      //两个混
      if (hunNum > 1 && hun2Num >0) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_TWO_HUN, i, 2))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 2 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 2,hun2Num-1)
        }
      }

      //两个混凑将

      if (hunNum > 1 && !Hu.isHasJiang(cardGroupList) && hun2Num>0) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_TWO_HUN_JIANG, i, 2))
        var newCards = for (c <- cards) yield c

        //数量
        if (getCardNum(newCards) == 0 && hunNum - 2 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 2,hun2Num-1)
        }
      }

      //三个混
      if (hunNum > 2) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_THREE_HUN, -1, 3))
        var newCards = for (c <- cards) yield c

        //数量
        if (getCardNum(newCards) == 0 && hunNum - 3 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 3,hun2Num)
        }
      }


    }

  }


  def getKaoPai1(cards: Array[Int], complete: util.List[util.List[CardGroup]], cardGroupList: util.List[CardGroup], hunNum: Int): Unit = {
    count_hun += 1
    println("-----dai hun --- " + count_hun)
    for (i <- cards.indices if cards(i) != 0) {
      //凑将
      if (hunNum > 0 && cards(i) >= 1 && !Hu.isHasJiang(cardGroupList)) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_JIANG, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        //数量
        if (getCardNum(newCards) == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai1(newCards, complete, newCardGroupList, hunNum - 1)
        }
      }

      //两张牌凑成顺

      //凑刻
      if (hunNum > 0 && cards(i) >= 2) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_KE, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 2
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai1(newCards, complete, newCardGroupList, hunNum - 2)
        }
      }


      //凑顺 前
      if (hunNum > 0 && isHasShun(i) && cards(i) >= 1 && cards(i + 1) >= 1 && i != 0 && i != 9 && i != 18) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_SHUN, i - 1, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        newCards(i + 1) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai1(newCards, complete, newCardGroupList, hunNum - 1)
        }
      }

      //凑顺 后
      if (hunNum > 0 && isHasShun(i) && cards(i) >= 1 && cards(i + 1) >= 1) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_SHUN, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        newCards(i + 1) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai1(newCards, complete, newCardGroupList, hunNum - 1)
        }
      }

      //凑顺 中间
      if (hunNum > 0 && isHasShun(i) && cards(i) >= 1 && cards(i + 2) >= 1) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_SHUN, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        newCards(i + 2) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 1 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai1(newCards, complete, newCardGroupList, hunNum - 1)
        }
      }

      //两个混
      if (hunNum > 1 ) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_TWO_HUN, i, 2))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 2 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai1(newCards, complete, newCardGroupList, hunNum - 2)
        }
      }

      //两个混凑将

      if (hunNum > 1 && !Hu.isHasJiang(cardGroupList)) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_TWO_HUN_JIANG, i, 2))
        var newCards = for (c <- cards) yield c

        //数量
        if (getCardNum(newCards) == 0 && hunNum - 2 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai1(newCards, complete, newCardGroupList, hunNum - 2)
        }
      }

      //三个混
      if (hunNum > 2 ) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_THREE_HUN, i, 3))
        var newCards = for (c <- cards) yield c

        //数量
        if (getCardNum(newCards) == 0&& hunNum - 3 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai1(newCards, complete, newCardGroupList, hunNum - 3)
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
