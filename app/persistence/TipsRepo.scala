package persistence

import javax.inject.Inject

import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Provides facility for executing standard CRUD operations against tips collection in database
  *
  * @author tfellison
  */
class TipsRepo @Inject() (reactiveMongoApi: ReactiveMongoApi) {

  def collection = reactiveMongoApi.db.collection[JSONCollection]("tips")

  /**
    * Insert a new document into the database
    *
    * @param document Document to be saved
    * @param ec ExecutionContext used to execute the operation
    * @return Result of the write operation
    */
  def save(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.update(BSONDocument("id" -> document.get("id").getOrElse(BSONObjectID.generate)), document, upsert = true)
  }

  /**
    * Find and return a document from the database
    *
    * @param selector Selector used to identify the document
    * @param ec ExecutionContext used to execute the operation
    * @return JsObject representation of the found document
    */
  def find(selector: BSONDocument)(implicit ec: ExecutionContext): Future[Option[JsObject]] = {
    collection.find(selector).one[JsObject]
  }

  /**
    * Update an existing document
    *
    * @param selector Selector used to identify the document
    * @param update Modifications to be made to the document
    * @param ec ExecutionContext used to execute the operation
    * @return Result of the write operation
    */
  def update(selector: BSONDocument, update: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.update(selector, update)
  }

  /**
    * Drop an existing document from the database
    *
    * @param document Document to be dropped
    * @param ec ExecutionContext used to execute the operation
    * @return Result of the write operation
    */
  def remove(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.remove(document)
  }

  /**
    * Find all documents in the database and return them in a list
    *
    * @param ec ExecutionContext used to execute the operation
    * @return List containing all documents in the database
    */
  def findAll()(implicit ec: ExecutionContext): Future[List[JsObject]] = {
    val queryBuilder = collection.find(Json.obj())
    val cursor = queryBuilder.cursor[JsObject](ReadPreference.Primary)
    cursor.collect[List]()
  }

  /**
    * Drop the entire collection of documetns
    *
    * @param ec ExecutionContext used to execute the operation
    * @return void
    */
  def dropAll()(implicit ec: ExecutionContext): Future[Unit] = Future {
    collection.drop()
  }

}


