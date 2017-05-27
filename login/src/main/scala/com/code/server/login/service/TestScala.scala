package com.code.server.login.service

import java.util

/**
  * Created by sunxianping on 2017/5/10.
  */
object TestScala {
  def main(args: Array[String]): Unit = {

    testMatch1()
//    testMatch()
  }

  def testMatch1()={
    def f(ll:List[Int]) = (ll.size,ll)
    val a = f(List(1,2,3))
    println(a._1)
  }

  def testStudent() = {
    val stu:Student = Student.apply(1,"sun")
    println (stu.id)



    println(1 + 1)
//    println(a.isInstanceOf[Int])

  }

  def testList()={
    val list = 1 +: 1+:Nil
    println(list)
  }
  def testMatch() = {
    val seq = Seq(1,2,3,None,"A")

    for {s<-seq} {
      val a = s match {
        case 1 => 1.toString
        case _ => "fdfds"
      }
      println(a)

    }
  }
}
