package model

case class Call( startTime: String,
                 city: String,
                 topic: String,
                 language: String,
                 gender: String,
                 age: String,
                 kind: String
               )

object Call {
  val StartTime = "startTime"
  val City = "city"
  val Topic = "topic"
  val Language = "language"
  val Gender = "gender"
  val Age = "age"
}

