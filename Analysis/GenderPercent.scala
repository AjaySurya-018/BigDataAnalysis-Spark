package com.sparkProjects

import org.apache.spark.rdd.RDD

object GenderPercent {
  var rdd: RDD[Member] = null

  def getPercent() = {
    val gender = rdd.map(f => (f.gender, 1)).reduceByKey(_ + _)
    val genderCount = getRdd().count()
    gender.foreach(f => {
      println(f._1 +": " + (f._2.toDouble * 100 / genderCount)+"%" )
    })
  }

  def setRdd(rdd: RDD[Member]) = this.rdd = rdd
  def getRdd():RDD[Member]=rdd
}
