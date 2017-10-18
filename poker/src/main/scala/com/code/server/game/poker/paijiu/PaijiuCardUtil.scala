package com.code.server.game.poker.paijiu

import com.code.server.constant.data.DataManager
import scala.collection.JavaConverters._


/**
  * Created by sunxianping on 2017/9/11.
  */
object PaijiuCardUtil {

  var cardScore: List[(String, Int)] = List()

  def initCardScore(): Unit = {
    if (cardScore.nonEmpty) return

    for (card <- DataManager.data.getPaijiuCardGroupDataMap.asScala) {

      val key = card._1
      val groupName = card._2.getName
      val score: Int = DataManager.data.getPaijiuCardGroupScoreDataMap.get(groupName).getScore
      if(score <= 60){
        cardScore = cardScore.+:((key, score))
      }
    }
    //排序
    cardScore = cardScore.sortWith(compare)
  }

  def compare(o1: (String, Int), o2: (String, Int)): Boolean = {
    if (o1._2 >= o2._2) {
      true
    } else {
      false
    }
  }

  /**
    * 获得最大的四张牌和换后的牌
    * @param cards
    * @return
    */
  def getMaxGroupAndNewCards(cards:List[Int]): (List[Int],List[Int]) ={
    var removeList:List[Int] = List()
//    var newCards:List[Int] = List()


    val (removeList1,newCards1):(List[Int],List[Int]) = getMaxGroup(cards)
    val (removeList2,newCards2):(List[Int],List[Int])  = getMaxGroup(newCards1)

    removeList ++= removeList1
    removeList ++= removeList2

    return (removeList, newCards2)


  }

  /**
    * 得到最大的牌组
    * @param cards
    * @return
    */
  def getMaxGroup(cards: List[Int]): (List[Int],List[Int]) = {
    initCardScore()
    var result:(List[Int], List[Int]) = (List(), List())
    var newCards:List[Int] = List()
    newCards ++= cards

    println(newCards)
    for (group <- cardScore) {

      val card1 = group._1.split(",")(0).toInt
      val card2 = group._1.split(",")(1).toInt
      var haveList:List[Int] = List()
      haveList = haveList.+:(card1)
      haveList = haveList.+:(card2)
      var isHasCard:Boolean = false
      if(card1 == card2){
        isHasCard = newCards.count(cd=>cd == card1) == 2
        if (isHasCard) println("两张相同的")
      }else{
        isHasCard = newCards.asJava.containsAll(haveList.asJava)
        if(isHasCard) println("不同的")
      }

      if (isHasCard) {
        println("去掉的牌 : " , haveList)
        newCards = newCards.diff(haveList)
        println("剩下的牌 : ",newCards)
        return (haveList, newCards)
      }

    }
    null
  }




}
