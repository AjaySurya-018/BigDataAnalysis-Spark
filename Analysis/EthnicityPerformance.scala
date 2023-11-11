package com.sparkProjects

import org.apache.spark.rdd.RDD

object EthnicityPerformance {
  var rdd: RDD[Member] = null

  def analyzeEthnicityPerformance(): Unit = {
    val ethnicityScores = getRdd().map(student => (student.ethnicity, (student.mathScore.toDouble,
      student.readingScore.toDouble, student.writingScore.toDouble)))

    val avgScoresByEthnicity = ethnicityScores
      .mapValues(scores => (scores, 1))
      .reduceByKey((std1, std2) => {
        val mathSum = std1._1._1 + std2._1._1
        val readingSum = std1._1._2 + std2._1._2
        val writingSum = std1._1._3 + std2._1._3
        val count = std1._2 + std2._2
        ((mathSum, readingSum, writingSum), count)
      })
      .mapValues { case (scores, count) =>
        val mathAvg = scores._1.toDouble / count
        val readingAvg = scores._2.toDouble / count
        val writingAvg = scores._3.toDouble / count
        (mathAvg, readingAvg, writingAvg)
      }

    avgScoresByEthnicity.foreach { case (ethnicity, scores) =>
      println(s"Ethnicity: $ethnicity")
      println(f"Math Average: ${scores._1}%.2f")
      println(f"Reading Average: ${scores._2}%.2f")
      println(f"Writing Average: ${scores._3}%.2f")
      println()
    }
  }

  def setRdd(rdd: RDD[Member]): Unit = this.rdd = rdd
  def getRdd(): RDD[Member] = rdd
}
