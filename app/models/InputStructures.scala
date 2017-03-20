package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Defines formats of expected valid inputs to service operations
  *
  * @author tfellison
  */

/**
  * Expected input format for create-tip operation
  *
  * @param submitter Name of user submitting the tip
  * @param message Message that makes up the textual body of the tip
  */
case class CreateTipInput(submitter: String, message: String)

object CreateTipInput {
  implicit val reads: Reads[CreateTipInput] = (
    (JsPath \ "submitter").read[String] and
    (JsPath \ "message").read[String])(CreateTipInput.apply _)
}

/**
  * Expected input format for fetch-tip operation
  *
  * @param id Identifier of the tip to fetch
  */
case class FetchTipInput(id: String)

object FetchTipInput {
  implicit val reads: Reads[FetchTipInput] = (JsPath \ "id").read[String].map(FetchTipInput.apply _)
}

/**
  * Expected input format for delete-tip operation
  *
  * @param id Identifier of the tip to fetch
  */
case class DeleteTipInput(id: String)

object DeleteTipInput {
  implicit val reads: Reads[DeleteTipInput] = (JsPath \ "id").read[String].map(DeleteTipInput.apply _)
}

/**
  * Expected input format for the update-tip operation
  *
  * @param id Identifier of the tip to update
  * @param message New message to make up the textual body of the tip
  */
case class UpdateTipInput(id: String, message: String)

object UpdateTipInput {
  implicit val reads: Reads[UpdateTipInput] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "message").read[String])(UpdateTipInput.apply _)
}

/**
  * Expected input format for the add-comment operation
  *
  * @param id Identifier of the tip to which to add the comment
  * @param comment Textual body of the comment
  */
case class AddCommentInput(id: String, comment: String)

object AddCommentInput {
  implicit val reads: Reads[AddCommentInput] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "comment").read[String])(AddCommentInput.apply _)
}