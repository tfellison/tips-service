package controllers

import javax.inject.Inject
import play.api.mvc._

/**
  * Defines actions to handle requests not associated with a specific API
  *
  * @author tellison
  */
class DefaultController @Inject() extends Controller {

  /**
    * Handles requests made to application root
    *
    * @return Response indicating application is ready
    */
  def index = Action {
    Ok("Application ready...")
  }

  /**
    * Catches requests made to any path not associated with a valid operation
    *
    * @return
    */
  def catchAll(path: String) = Action {
    Ok(s"The specified route is not defined: $path")
  }

}
