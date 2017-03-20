package models

/**
  * Contains structures/formats for representing objects persisted in database
  *
  * @author tfellison
  */

/** Field names of a tip as persisted in a database document */
object TipFieldNames {
  val Id = "id"
  val Submitter = "submitter"
  val CreatedTime = "createdTime"
  val LastUpdatedTime = "lastUpdatedTime"
  val Message = "message"
  val Comments = "comments"
}