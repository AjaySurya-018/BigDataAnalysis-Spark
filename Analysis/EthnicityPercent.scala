package com.sparkProjects

import org.apache.spark.rdd.RDD

object EthnicityPercent {
  var rdd: RDD[Member] = null

  def getPercent() = {
    val ethnicity = rdd.map(f => (f.ethnicity, 1)).reduceByKey(_ + _)
    val ethnicityCount = getRdd().count()
    ethnicity.foreach(f => {
      println(f._1+": " + (f._2.toDouble * 100 / ethnicityCount)+"%" )
    })
  }

  def setRdd(rdd: RDD[Member]) = this.rdd = rdd

  def getRdd(): RDD[Member] = rdd

}
