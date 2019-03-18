package com.code.server.game.poker.paijiu

/**
  * Created by sunxianping on 2019-03-18.
  */
class GamePaijiu100 extends GamePaijiu {


  override protected def initCards(): Unit = {
    super.initCards()
    //牌放到预先设定好的牌中

    val slidList = cards.sliding(4, 4).toList


    for (i <- 0 to 3) {
      commonCards += (i -> slidList(i))
    }

  }


}
