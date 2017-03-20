package controllers

import javax.inject.Inject
import java.util.UUID

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._
import persistence.TipsRepo
import models._
import utilities.TimeUtils

import scala.concurrent.Future

/**
  * This controller defines actions to handle HTTP requests made to the tips-service API.
  *
  * @param reactiveMongoApi API object used to communicate with tips collection in MongoDB
  */
class TipsController @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends Controller with MongoController with ReactiveMongoComponents {

  val InvalidInputMessage = "Operation failed: Request body appeared to contain valid JSON, but the structure was incorrect for the specified operation."
  val IdNotFoundMessage = "Specified ID not found."

  def tipsRepo = new TipsRepo(reactiveMongoApi)

  /**
    * Create and store a new tip
    *
    * @return Simple JSON document containing ID of newly created tip
    */
  def createTip(): Action[JsValue]  = Action.async(BodyParsers.parse.json) { implicit request =>

    request.body.validate[CreateTipInput] match {
      case success: JsSuccess[CreateTipInput] => {
        val timestamp = TimeUtils.getCurrentTimestampUTC.toString
        val id = UUID.randomUUID.toString
        tipsRepo.save(BSONDocument(
          TipFieldNames.Id -> id,
          TipFieldNames.Submitter -> success.get.submitter,
          TipFieldNames.CreatedTime -> timestamp,
          TipFieldNames.LastUpdatedTime -> timestamp,
          TipFieldNames.Message -> success.get.message,
          TipFieldNames.Comments -> Array[String]()
        )).map(result => Created(Json.obj("id" -> id)))
      }
      case JsError(error) => scala.concurrent.Future { BadRequest(Json.obj("result" -> InvalidInputMessage))  }
    }

  }

  /**
    * Fetch an existing tip from the database
    *
    * @return Tip indicated by the provided ID
    */
  def fetchTip(): Action[JsValue]  = Action.async(BodyParsers.parse.json) { implicit request =>

    request.body.validate[FetchTipInput] match {
      case success: JsSuccess[FetchTipInput] => {
        tipsRepo.find(BSONDocument(TipFieldNames.Id -> BSONString(success.get.id))).map(tip => Ok(if (tip.isDefined) Json.toJson(tip) else Json.obj("result" -> IdNotFoundMessage)))
      }
      case JsError(error) => scala.concurrent.Future { BadRequest(Json.obj("result" -> InvalidInputMessage))  }
    }

  }

  /**
    * Update the message of an existing tip
    *
    * @return Simple JSON document indicating whether operation was successful
    */
  def updateTip(): Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>

    request.body.validate[UpdateTipInput] match {
      case success: JsSuccess[UpdateTipInput] => {
        tipsRepo.update(BSONDocument(TipFieldNames.Id -> BSONString(success.get.id)),
          BSONDocument("$set" -> BSONDocument(
            TipFieldNames.LastUpdatedTime -> TimeUtils.getCurrentTimestampUTC.toString,
            TipFieldNames.Message -> success.get.message
          ))).map(result => Ok(if (result.n > 0) Json.obj("result" -> s"Operation successful: Message updated for tip ${success.get.id}.") else Json.obj("result" -> IdNotFoundMessage)))
      }
      case JsError(error) => scala.concurrent.Future { BadRequest(Json.obj("result" -> InvalidInputMessage)) }
    }

  }

  /**
    * Delete an existing tip
    *
    * @return Simple JSON document indicating whether operation was successful
    */
  def deleteTip(): Action[JsValue]  = Action.async(BodyParsers.parse.json) { implicit request =>

    request.body.validate[DeleteTipInput] match {
      case success: JsSuccess[DeleteTipInput] => {
        tipsRepo.remove(BSONDocument(TipFieldNames.Id -> BSONString(success.get.id))).map(result => Ok(if (result.n > 0) Json.obj("result" -> s"Operation successful: Tip ${success.get.id} deleted.") else Json.obj("result" -> IdNotFoundMessage)))
      }
      case JsError(error) => scala.concurrent.Future { BadRequest(Json.obj("result" -> InvalidInputMessage))  }
    }

  }

  /**
    * Add a comment to an existing tip
    *
    * @return Simple JSON document indicating whether operation was successful
    */
  def addComment(): Action[JsValue]  = Action.async(BodyParsers.parse.json) { implicit request =>

    request.body.validate[AddCommentInput] match {
      case success: JsSuccess[AddCommentInput] => {
        tipsRepo.update(BSONDocument(TipFieldNames.Id -> BSONString(success.get.id)),
          BSONDocument("$set" -> BSONDocument(TipFieldNames.LastUpdatedTime -> TimeUtils.getCurrentTimestampUTC.toString),
            "$push" -> BSONDocument(TipFieldNames.Comments -> success.get.comment)
          )).map(result => Ok(if (result.n > 0) Json.obj("result" -> s"Operation successful: Comment added to tip ${success.get.id}.") else Json.obj("result" -> IdNotFoundMessage)))
      }
      case JsError(error) => scala.concurrent.Future { BadRequest(Json.obj("result" -> InvalidInputMessage)) }
    }

  }

  /**
    * Fetch all existing tips from the database
    *
    * @return JSON array of all existing tips
    */
  def fetchAllTips(): Action[AnyContent] = Action.async { implicit request =>
    tipsRepo.findAll().map(tips => Ok(Json.toJson(tips)))
  }

  /**
    * Drop the entire collection of tips from the database
    *
    * @return Simple JSON document confirming all tips have been deleted
    */
  def deleteAllTips(): Action[AnyContent] = Action.async { implicit request =>
    tipsRepo.dropAll().map(result => Ok(Json.obj("result" -> "All tips deleted.")))
  }

}


