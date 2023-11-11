package com.sparkProjects

import org.apache.spark.rdd.RDD

object GenderComparison {
  var rdd: RDD[Member] = null

  def compareAverageMarksByGender(): Unit = {
    val maleAvg = calculateAverageMarks(rdd.filter(_.gender == "male"))
    val femaleAvg = calculateAverageMarks(rdd.filter(_.gender == "female"))

    println("Average Marks by Gender:")
    println(s"Male: Math - ${maleAvg._1}, Reading - ${maleAvg._2}, Writing - ${maleAvg._3}")
    println(s"Female: Math - ${femaleAvg._1}, Reading - ${femaleAvg._2}, Writing - ${femaleAvg._3}")
  }

  private def calculateAverageMarks(data: RDD[Member]): (Double, Double, Double) = {
    val mathAvg = data.map(_.mathScore).mean()
    val readingAvg = data.map(_.readingScore).mean()
    val writingAvg = data.map(_.writingScore).mean()
    (mathAvg, readingAvg, writingAvg)
  }

  def setRdd(rdd: RDD[Member]): Unit = this.rdd = rdd

  def getRdd(): RDD[Member] = rdd
}

