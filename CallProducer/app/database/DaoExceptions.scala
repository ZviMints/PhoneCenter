package database

abstract class DaoException(message: String) extends Exception(message)
case class DuplicationError(message: String) extends DaoException(message)
case class GeneralDBError(message: String) extends DaoException(message)
case class ManyDuplicationsError(message: String) extends DaoException(message)
