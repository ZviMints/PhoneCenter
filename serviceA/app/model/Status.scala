package model

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import model.Status.findValues

import scala.collection.immutable

sealed trait Status extends EnumEntry

object Status extends Enum[Status] with PlayJsonEnum[Status] {

  case object Ready extends Status

  case object InProgress extends Status

  case object Done extends Status

  case object Failure extends Status

  override def values: immutable.IndexedSeq[Status] = findValues
}
