package model

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}

import scala.collection.immutable

sealed trait Status extends EnumEntry

object Status extends Enum[Status] with PlayJsonEnum[Status] {

  case object Ready extends Status

  case object InProgress extends Status

  case object Sent extends Status

  override def values: immutable.IndexedSeq[Status] = findValues
}
