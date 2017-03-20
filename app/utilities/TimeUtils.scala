package utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
  * Provides utilities for working with time values
  *
  * @author tellison
  */
object TimeUtils {

  /**
    * Returns the UTC current timestamp at millisecond resolution in ISO 8601 format
    *
    * @return Current UTC timestamp at millisecond resolution in ISO 8601 format
    */
  def getCurrentTimestampUTC : String = {
    val timeZone = TimeZone.getTimeZone("UTC")
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    dateFormat.setTimeZone(timeZone)
    return dateFormat.format(new Date())
  }

}
