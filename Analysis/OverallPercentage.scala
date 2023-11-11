package com.sparkProjects

import org.apache.spark.rdd.RDD

object OverallPercentage {
  var rdd: RDD[Member] = null

  def calculateOverallPercentage(): Unit = {
    val overallPercentages = getRdd().map(student => {
      val totalMarks = student.mathScore + student.readingScore + student.writingScore
      val overallPercentage = (totalMarks.toDouble / 300) * 100
      (student.gender, student.ethnicity, overallPercentage)
    })
    overallPercentages.foreach { case (gender, ethnicity, percentage) =>
      println(s"Gender: $gender, Ethnicity: $ethnicity, Overall Percentage: $percentage%")
    }
  }

  def setRdd(rdd: RDD[Member]): Unit = this.rdd = rdd
  def getRdd(): RDD[Member] = rdd
}
