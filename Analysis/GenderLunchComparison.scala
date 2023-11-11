package com.sparkProjects
import org.apache.spark.rdd.RDD

object GenderLunchComparison {
  var rdd: RDD[Member] = null

  def compareGenderLunch(): Unit = {
    val genderLunchCounts = rdd.map(member => ((member.gender, member.lunch), 1))
      .reduceByKey(_ + _)
      .map { case ((gender, lunch), count) => (gender, (lunch, count)) }

    genderLunchCounts.foreach { case (gender, (lunch, count)) =>
      println(s"$gender: $lunch - Count: $count")
    }
  }

  def setRdd(rdd: RDD[Member]): Unit = this.rdd = rdd
  def getRdd(): RDD[Member] = rdd
}