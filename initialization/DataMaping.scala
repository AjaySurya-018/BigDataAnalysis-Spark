package com.sparkProjects

import org.apache.spark.rdd.RDD

object DataMaping {
  val dataMap = Start.getData().map(f => {
    val arr: Array[String] = f.split(",")
    new Member(arr(0), arr(1), arr(2), arr(3), arr(4), arr(5).toInt, arr(6).toInt, arr(7).toInt)
  })

  def getDataMap(): RDD[Member] = dataMap
}

