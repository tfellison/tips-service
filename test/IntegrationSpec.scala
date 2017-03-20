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

    "work from within a browser" in new WithBrowser {

      browser.goTo("http://localhost:" + port)

      browser.pageSource must contain("Application ready...")
    }

    "handle invalid route requests" in new WithBrowser {

      browser.goTo("http://localhost:" + port + "/invalid")

      browser.pageSource must contain("The specified route is not defined:")
    }
  }
}