package models

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Support methods for URL
 */
object URLSupport {

  /**
   * Slugify code based on code from https://github.com/julienrf/chooze/blob/master/app/util/Util.scala
   * @param str string to sluggify
   * @return slugified string
   */
  def slugify(str: String): String = {
    import java.text.Normalizer
    Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\w ]", "").replace(" ", "-").toLowerCase
  }
}