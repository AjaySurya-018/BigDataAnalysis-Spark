package com.sparkProjects
import org.apache.spark.rdd.RDD

object AverageMark {
  var rdd: RDD[Member] = null

  def calculateStatistics(): Unit = {
    val mathScores = rdd.map(_.mathScore)
    val readingScores = rdd.map(_.readingScore)
    val writingScores = rdd.map(_.writingScore)

    val mathMean = mathScores.mean()
    val readingMean = readingScores.mean()
    val writingMean = writingScores.mean()

    val mathStdDev = calculateStdDev(mathScores, mathMean)
    val readingStdDev = calculateStdDev(readingScores, readingMean)
    val writingStdDev = calculateStdDev(writingScores, writingMean)

    val mathMin = mathScores.min()
    val readingMin = readingScores.min()
    val writingMin = writingScores.min()

    val mathMax = mathScores.max()
    val readingMax = readingScores.max()
    val writingMax = writingScores.max()

    println("Class Statistics in each section: ")
    println(s"Math:")
    println(s"- Average: $mathMean");println(s"- Standard Deviation: $mathStdDev");println(s"- Min: $mathMin");println(s"- Max: $mathMax")
    println(s"Reading:")
    println(s"- Average: $readingMean");println(s"- Standard Deviation: $readingStdDev");println(s"- Min: $readingMin");println(s"- Max: $readingMax")
    println(s"Writing:")
    println(s"- Average: $writingMean");println(s"- Standard Deviation: $writingStdDev");println(s"- Min: $writingMin");println(s"- Max: $writingMax")
  }

  def setRdd(rdd: RDD[Member]): Unit = this.rdd = rdd

  def getRdd(): RDD[Member] = rdd

  def calculateStdDev(data: RDD[Int], mean: Double): Double = {
    val variance = data.map(x => math.pow(x - mean, 2)).mean()
    math.sqrt(variance)
  }
}
