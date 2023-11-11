package com.sparkProjects

import org.apache.spark.rdd.RDD

object EducationPerformance {
  var rdd: RDD[Member] = null

  def analyzeEducationPerformance(): Unit = {
    val educationScores = getRdd().map(student => (student.education, (student.mathScore.toDouble,
      student.readingScore.toDouble, student.writingScore.toDouble)))

    val avgScoresByEducation = educationScores.groupByKey().mapValues(scores => {
      val count = scores.size
      val mathAvg = scores.map(_._1).sum / count
      val readingAvg = scores.map(_._2).sum / count
      val writingAvg = scores.map(_._3).sum / count
      (mathAvg, readingAvg, writingAvg)
    })

    avgScoresByEducation.foreach { case (education, scores) =>
      println(s"Parents Education Level: $education")
      println(f"Math Average: ${scores._1}%.2f")
      println(f"Reading Average: ${scores._2}%.2f")
      println(f"Writing Average: ${scores._3}%.2f")
      println()
    }
  }

  def setRdd(rdd: RDD[Member]): Unit = this.rdd = rdd

  def getRdd(): RDD[Member] = rdd
}
