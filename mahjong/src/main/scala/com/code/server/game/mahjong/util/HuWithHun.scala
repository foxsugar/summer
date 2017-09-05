package com.code.server.game.mahjong.util

import java.util

import com.code.server.game.mahjong.logic.CardTypeUtil

import scala.collection.JavaConverters._


/**
  * Created by sunxianping on 2017/8/21.
  */
object HuWithHun {

  var count_hun = 0


  def getHuCardType(a:Array[Int],chiPengGangNum:Int, hun:util.List[Integer],lastCard:Int):util.List[HuCardType] = {


    val (noHunCards, hunNum) = getNoHunCardsAndHunNum(a,hun)

    println("混的数量 : " + hunNum)
    //    for(c<- noHunCards if c !=0) println(c)

    var list: util.List[util.List[CardGroup]] = new util.ArrayList()
    var allList: util.List[util.List[CardGroup]] = new util.ArrayList()
    var groups: util.List[CardGroup] = new util.ArrayList()
    Hu.testHun(true, noHunCards, list, allList, groups)

    System.out.println("配好的 : " + list)
    System.out.println("没配好的 : " + allList)

    var finalResult:util.List[HuCardType] = new util.ArrayList[HuCardType]()
    //全都是混
    if (hunNum == getCardNum(a)) {
      list.add(new util.ArrayList[CardGroup]())
    }

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

        if (hunIsEnough(noHunCards, temp, hunNum)) {
          val remainCardNum = getCardNum(noHunCards)
          val isHasJiang = Hu.isHasJiang(temp)
          val needGroupSize = 5 - chiPengGangNum -temp.size
          val need2HunNum = getNeed2HunNum(remainCardNum, hunNum, needGroupSize, isHasJiang)
          getKaoPai(noHunCards, complete, temp, hunNum, need2HunNum)
        }


      } else {
        for (cardGroupList <- allList.asScala) {
          var newCards = for (c <- noHunCards) yield c
          val (cs, cdl) = getRemainCards(newCards, cardGroupList)
          //是否可以胡的初次判断 混必须满足最少需求个数

          val remainCardNum = getCardNum(cs)
          if (hunIsEnough(cs, cdl, hunNum)) {

            val isHasJiang = Hu.isHasJiang(cdl)
            val needGroupSize = 5 - chiPengGangNum- cdl.size
            var need2HunNum = getNeed2HunNum(remainCardNum, hunNum, needGroupSize, isHasJiang)
            println("两个混的数量 = " + need2HunNum)
            //          need2HunNum = 7
            getKaoPai(cs, complete, cardGroupList, hunNum, need2HunNum)
            //          getKaoPai1(cs, complete, cardGroupList, hunNum)
            println("组成的一半: " + cardGroupList)
            //          println("带混的最终牌型: " + complete)
          }
        }
      }
      complete = Hu.removeRepeat(complete)
      println("==================================hun==============")
      println(" 不是5 个附子的数量 = " + complete.stream().filter(l => l.size() != 5).count())
      println(complete.size())
      // println(complete)

      var huCardTypeList: util.List[HuCardType] = new util.ArrayList[HuCardType]()
      complete.forEach(li => huCardTypeList.add(Hu.convert2HuCardType(li)))
      huCardTypeList.forEach(huType => println(huType))


      huCardTypeList.forEach(li => getHuType(li, hun, lastCard))
      huCardTypeList


//    finalResult
  }



  def isHu(cardGroupList: util.List[CardGroup], lastCard: Int): Boolean = {
    false
  }

  /**
    * 是否是混
    *
    * @param hun
    * @param cardType
    * @return
    */
  private def isHun(hun: util.List[Integer], cardType: Int): Boolean = {
    for (h <- hun.asScala) {
      if (cardType == h) return true
    }
    false
  }


  def getHuType(huCardType: HuCardType, hun: util.List[Integer], lastCard: Int): Int = {
    var huList = new util.ArrayList[Int]()
    huList.add(isZhuo5(huCardType, hun, lastCard))
    huList.add(isLong(huCardType, hun, lastCard))
    huList.add(isHunDiao(huCardType, hun, lastCard))
    var t = getMaxHuType(huList)
    huCardType.specialHuList.add(t)
    println("最大牌型: " + t)
    t
  }

  /**
    * 是否是捉五(有组成456万的顺)
    *
    * @param huCardType
    * @param hun
    * @param lastCard
    * @return
    */
  def isZhuo5(huCardType: HuCardType, hun: util.List[Integer], lastCard: Int): Int = {
    //不是五万 不是混 肯定不是捉5
    if (lastCard != 4 && !isHun(hun, lastCard)) {
      return 0
    }

    //三个都是混
    if (huCardType.hun3.size() > 0) return HuType.hu_混儿吊捉五
    //两个混
    for (hun2Card <- huCardType.hun2.asScala) {

      if (hun2Card == 4) {
        return HuType.hu_混儿吊捉五
      } else if (hun2Card == 3 || hun2Card == 5) {
        return HuType.hu_捉五
      }

    }
    //一个混或者没有混
    for (shun <- huCardType.shun.asScala) {
      if (shun == 3) return HuType.hu_捉五
    }
    0
  }


  /**
    * 是否是混吊
    * @param huCardType
    * @param hun
    * @param lastCard
    * @return
    */
  def isHunDiao(huCardType: HuCardType, hun:util.List[Integer], lastCard:Int):Int = {
    var result = 0
    val cardIsHun = isHun(hun, lastCard)
    //最后抓的牌是混
    if(cardIsHun) {
      if(huCardType.hunJiang) result = HuType.hu_混吊
      if(huCardType.hun3.size() > 0) result = HuType.hu_混吊

    }else{//不是混
      for(hun2Type <- huCardType.hun2.asScala){
        if(hun2Type == lastCard) result = HuType.hu_混吊
      }
      //一个混的将
      if(huCardType.jiangOneHun == lastCard) result = HuType.hu_混吊
    }
    0
  }

  def isSuBenhunLong(huCardType: HuCardType, hun: util.List[Integer], lastCard: Int):Int = {
    if(huCardType.jiangOneHun != -1) return 0
    if(huCardType.hun3.size() > 0) return 0
    if(huCardType.hun2.size() > 0) return 0
    if(huCardType.hunJiang) return 0
    if(huCardType.shun.size() != 3) return 0

  }
  /**
    * 是否是龙
    *
    * @param huCardType
    * @param hun
    * @param lastCard
    * @return
    */
  def isLong(huCardType: HuCardType, hun: util.List[Integer], lastCard: Int): Int = {
    var huList = new util.ArrayList[Int]()
    huList.add(getLongType(huCardType, FanUtil.ytl1, hun, lastCard))
    huList.add(getLongType(huCardType, FanUtil.ytl2, hun, lastCard))
    huList.add(getLongType(huCardType, FanUtil.ytl3, hun, lastCard))
    getMaxHuType(huList)

  }


  /**
    * 获得龙的牌型
    *
    * @param huCardType
    * @param targetLong
    * @param hun
    * @param lastCard
    * @return
    */
  def getLongType(huCardType: HuCardType, targetLong: util.List[Integer], hun: util.List[Integer], lastCard: Int): Int = {
    var needShunList = new util.ArrayList(targetLong)
    //顺有的牌
    var removeList = new util.ArrayList[Integer]()
    for (index <- targetLong.asScala) {
      if (huCardType.shun.remove(index.asInstanceOf[Integer])) {
        removeList.add(index)
      }
    }
    needShunList.removeAll(removeList)

    //是否是本混
    val isBenhun = isBenHuner(hun, targetLong)
    val lastCardIsHun = isHun(hun, lastCard)
    val isWan = targetLong.get(0) == 0

    var longTypeSet = new util.ArrayList[Int]()

    //三个顺已经凑成了龙
    if (needShunList.size() == 0) {
      longTypeSet.addAll(analyseLongType(huCardType, lastCardIsHun, lastCard, isWan))
    } else {
      //三个顺 没有凑成龙

      //hun2 来充当龙的部分
      var needRemoveList = new util.ArrayList[Int]()
      var hun2RemoveList = new util.ArrayList[Int]()
      for (needCard <- needShunList.asScala) {
        for (hunIndex <- 0 until huCardType.hun2.size()) {
          val nearNode = getNearLongNode(huCardType.hun2.get(hunIndex))
          //龙需要的牌
          if (nearNode == needCard && !needRemoveList.contains(needCard) && !hun2RemoveList.contains(hunIndex)) {
            needRemoveList.add(needCard)
            hun2RemoveList.add(hunIndex)
          }
        }
      }

      //删除 匹配的
      needShunList.removeAll(needRemoveList)
      for (index <- hun2RemoveList.asScala) {
        huCardType.hun2.remove(index.toInt)
      }

      //组成龙了
      if (needShunList.size() == 0) {
        longTypeSet.addAll(analyseLongType(huCardType, lastCardIsHun, lastCard, isWan))
      } else {
        //三个混 组成龙的部分
        if (needShunList.size() <= huCardType.hun3.size()) {
          //去掉三个混
          for (i <- 0 until needShunList.size()) {
            huCardType.hun3.remove(0)
          }
          longTypeSet.addAll(analyseLongType(huCardType, lastCardIsHun, lastCard, isWan))
        }
      }
    }


    var result = getMaxHuType(longTypeSet)
    //是本混龙
    if (isBenhun && result != 0) result += 1
    result

  }

  /**
    * 获得最大的胡类型
    *
    * @param list
    * @return
    */
  private def getMaxHuType(list: util.List[Int]): Int = {
    //    println("list size = " + list.size)
    var result = 0
    for (i <- list.asScala) {
      if (i >= result) {
        result = i
      }
    }
    //    println("result = "+result)
    result
  }

  /**
    * 分析龙的类型
    *
    * @param huCardType
    * @param lastCardIsHun
    * @param lastCard
    * @param isWan
    * @return
    */
  def analyseLongType(huCardType: HuCardType, lastCardIsHun: Boolean, lastCard: Int, isWan: Boolean): util.HashSet[Int] = {
    var longTypeSet = new util.HashSet[Int]()
    //最后抓的是混
    if (lastCardIsHun) {
      //三个混 或者 有四五六万的顺子
      if (huCardType.hun3.size() > 0 || huCardType.shun.contains(3)) longTypeSet.add(HuType.hu_混儿吊捉五龙)
      //两个混的将
      if (huCardType.hunJiang) longTypeSet.add(HuType.hu_混儿吊龙)
    } else {
      //如果最后抓的不是混  并且在hun2里
      if (huCardType.hun2.size() > 0 && huCardType.hun2.contains(lastCard)) {
        //抓的五万 是捉5
        if (lastCard == 4) longTypeSet.add(HuType.hu_混儿吊捉五龙) else longTypeSet.add(HuType.hu_混儿吊龙)
      }
      //抓来的牌是将
      if (huCardType.jiangOneHun == lastCard) longTypeSet.add(HuType.hu_混儿吊龙)

      //有四五六万不在龙里
      if (huCardType.shun.contains(3)) longTypeSet.add(HuType.hu_捉五龙)
    }

    //万字的龙并且捉5
    if ((lastCardIsHun || lastCard == 4) && isWan) longTypeSet.add(HuType.hu_捉五龙)

    longTypeSet.add(HuType.hu_龙)
    longTypeSet
  }


  /**
    * 是否是本混
    *
    * @param hun
    * @param long
    * @return
    */
  def isBenHuner(hun: util.List[Integer], long: util.List[Integer]): Boolean = {
    return CardTypeUtil.getCardGroupByCardType(hun.get(0)) == CardTypeUtil.getCardGroupByCardType(long.get(0))
  }


  /**
    * 获得临近的龙节点
    *
    * @param index
    * @return
    */
  private def getNearLongNode(index: Int): Int = {
    index / 3 * 3
  }

  /**
    * 混是否足够凑成胡
    *
    * @param cards
    * @param cardGroupList
    * @param hunNum
    * @return
    */
  def hunIsEnough(cards: Array[Int], cardGroupList: util.List[CardGroup], hunNum: Int): Boolean = {
    val cardNum = getCardNum(cards)
    if (Hu.isHasJiang(cardGroupList)) {
      hunNum >= cardNum / 2
    } else {
      hunNum >= (cardNum - 1) / 2
    }
  }

  /**
    * 获得混牌的类型 (包括顺延的一张)
    *
    * @param index
    * @return
    */
  def getHunType(index: Int): util.List[Integer] = {
    var result: util.List[Integer] = new util.ArrayList()
    val second = index match {
      case 8 => 0
      case 17 => 1
      case 26 => 18
      case 30 => 27
      case 33 => 31
      case _ => index + 1
    }
    result.add(index)
    result.add(second)
    result
  }

  /**
    * 得到刮大风的混
    *
    * @param index
    * @return
    */
  def getHunTypeGDF(index: Int): util.List[Integer] = {
    var result: util.List[Integer] = new util.ArrayList()
    val second = index match {
      case 7 =>
        result.add(7)
        result.add(8)
        result.add(0)
      case 8 =>
        result.add(8)
        result.add(0)
        result.add(1)
      case 16 =>
        result.add(16)
        result.add(17)
        result.add(9)
      case 17 =>
        result.add(17)
        result.add(9)
        result.add(10)
      case 25 =>
        result.add(25)
        result.add(26)
        result.add(18)
      case 26 =>
        result.add(26)
        result.add(18)
        result.add(19)
      case 27 | 28 | 29 | 30 =>
        result.add(27)
        result.add(28)
        result.add(29)
        result.add(30)
      case 31 | 32 | 33 =>
        result.add(31)
        result.add(32)
        result.add(33)
      case _ =>
        result.add(index)
        result.add(index + 1)
        result.add(index + 2)
    }
    result
  }


    /**
      * 获得去掉混的牌和混的数量
      * @param cards
      * @param hun
      * @return
      */
  def getNoHunCardsAndHunNum(cards: Array[Int], hun:util.List[Integer]): (Array[Int], Int) = {
    val newCards = for (c <- cards) yield c
    var count = 0
    for (index <- newCards.indices) {
      if (hun.contains(index)) {
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


  def getKaoPai(cards: Array[Int], complete: util.List[util.List[CardGroup]], cardGroupList: util.List[CardGroup], hunNum: Int, hun2Num: Int): Unit = {
    count_hun += 1
//    println("-----dai hun --- " + count_hun)
    for (i <- cards.indices if cards(i) != 0) {
      //凑将
      if (hunNum > 0 && cards(i) >= 1 && !Hu.isHasJiang(cardGroupList)) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_ONE_HUN_JIANG, i, 1))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        //数量
        if (getCardNum(newCards) == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1, hun2Num)
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
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 2, hun2Num)
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
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1, hun2Num)
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
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1, hun2Num)
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
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 1, hun2Num)
        }
      }

      //两个混
      if (hunNum > 1 && hun2Num > 0) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_TWO_HUN, i, 2))
        var newCards = for (c <- cards) yield c
        newCards(i) -= 1
        //数量
        if (getCardNum(newCards) == 0 && hunNum - 2 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 2, hun2Num - 1)
        }
      }

      //两个混凑将

      if (hunNum > 1 && !Hu.isHasJiang(cardGroupList) && hun2Num > 0) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_TWO_HUN_JIANG, i, 2))
        var newCards = for (c <- cards) yield c

        //数量
        if (getCardNum(newCards) == 0 && hunNum - 2 == 0) {
          Hu.add2List(complete, newCardGroupList)
        } else {
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 2, hun2Num - 1)
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
          getKaoPai(newCards, complete, newCardGroupList, hunNum - 3, hun2Num)
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
      if (hunNum > 1) {
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
      if (hunNum > 2) {
        var newCardGroupList = new util.ArrayList(cardGroupList)
        newCardGroupList.add(new CardGroup(Hu.CARD_GROUP_TYPE_THREE_HUN, i, 3))
        var newCards = for (c <- cards) yield c

        //数量
        if (getCardNum(newCards) == 0 && hunNum - 3 == 0) {
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
//    test()

    val a = Array[Int](//                3, 2, 0, 0, 0, 0, 0, 0, 0,
      11, 1, 0, 0, 0, 1, 0, 0, 0,
      0, 0, 0, 0, 0, 1, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0,
      0, 0, 0)

    val huIndex = 0
    var hun = getHunTypeGDF(huIndex)
    var lastCard = 4

//    println(getHuCardType(a,0,hun,lastCard))
    getHuCardType(a,0,hun,lastCard).forEach(hucardType=>println(hucardType))
    //    var list: util.List[Int] = new util.ArrayList[Int]()
    //    list.add(3)
    //    list.add(1)
    //    list.add(2)
    //    list.add(9)


  }
}
