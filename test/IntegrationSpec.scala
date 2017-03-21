import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.WithBrowser

/**
  * Test basic application functionality using a headless browser
  *
  * @author tfellison
  */
@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

  "Application" should {

    "respond to index requests from browser by confirming ready status" in new WithBrowser {

      browser.goTo("http://localhost:" + port)

      browser.pageSource must be equalTo "Application ready..."
    }

    "connect to database and clear existing data" in new WithBrowser {

      browser.goTo("http://localhost:" + port + "/api/tips/delete-all-tips")

      browser.pageSource must contain("Tips collection cleared.")
    }

    "fetch empty result set from database" in new WithBrowser {

      browser.goTo("http://localhost:" + port + "/api/tips/fetch-all-tips")

      browser.pageSource must be equalTo "[]"
    }
  }
}