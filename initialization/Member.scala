package com.sparkProjects
// Student class representing the features of the dataset as class attributes
class Member(var gender: String,
             var ethnicity: String,
             var education: String,
             var lunch: String,
             var preparationCourse: String,
             var mathScore: Int,
             var readingScore: Int,
             var writingScore: Int)  {

  // defining getter methods which allows you to retrieve the values of the attributes
  def getGender(): String = gender
  def getEthnicity(): String = ethnicity
  def getEducation(): String = education
  def getLunch(): String = lunch
  def getPreparationCourse(): String = preparationCourse
  def getMathScore(): Int = mathScore
  def getReadingScore(): Int = readingScore
  def getWritingScore(): Int = writingScore

  // defining the setter methods which allows you to modify the values of the attributes
  def setGender(s: String) = (gender = s)
  def setEthnicity(s: String) = (ethnicity = s)
  def setEducation(s: String) = (education = s)
  def setLunch(s: String) = (lunch = s)
  def setPreparationCourse(s: String) = (preparationCourse = s)
  def setMathScore(s: Int) = (mathScore = s)
  def setReadingScore(s: Int) = (readingScore = s)
  def setWritingScore(s: Int) = (writingScore = s)
}
