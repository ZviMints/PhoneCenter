package dto

case class Call( id: String,
                 totalTime: String,
                 city: String,
                 topic: String,
                 language: String,
                 gender: String,
                 age: String,
                 kind: String)

object Call {
  val Id = "call_id"
  val TotalTime = "totalTime"
  val City = "city"
  val Topic = "topic"
  val Language = "language"
  val Gender = "gender"
  val Age = "age"
  val Kind = "kind"
}
