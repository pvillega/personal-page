package models

import play.api.Logger
import java.util.Scanner
import java.io.{InputStreamReader, FileWriter, FileReader, File}
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import javax.script.Bindings

/**
 * Created with IntelliJ IDEA.
 * User: pvillega
 * Support object to read/save markdown content
 */
object MarkdownSupport {

  /**
   * Loads and returns the contents of a markdown file
   * @param file path of the file to load
   * @return the contents of a markdown file
   */
  def loadMarkdown(file: String) = {
    val f = new File(file)
    Logger.debug("MarkdownSupport.loadMarkdown - loading file [%s]".format(f.getAbsolutePath))
    val scanner = new Scanner(new FileReader(f));
    val buffer = new StringBuilder
    while (scanner.hasNextLine()) {
      buffer.append(scanner.nextLine()).append("\n");
    }
    Logger.debug("MarkdownSupport.loadMarkdown - loaded content of file [%s] as markdown [%s]".format(f.getAbsolutePath, buffer.toString()))
    buffer.toString()
  }

  /**
   * Saves the given content into the given file
   * @param file the file to save
   * @param markdown the content to save
   */
  def saveMarkdown(file: String, markdown: String) = {
    val f = new File(file)
    Logger.debug("MarkdownSupport.saveMarkdown - saving file [%s]".format(f.getAbsolutePath))
    val writer = new FileWriter(f)
    writer.write(markdown)
    writer.close()
  }

  /**
   * Deletes the given markdown file
   * @param file the file to remove
   */
  def deleteMarkdown(file: String) = {
    val f = new File(file)
    Logger.debug("MarkdownSupport.deleteMarkdown - removing file [%s]".format(f.getAbsolutePath))
    f.delete()
  }

  /**
   * Converts the given markdown into HTML
   * @param markdown the markdown to turn into HTML
   */
  def convertToHtml(markdown: String) = {
    val manager = new ScriptEngineManager()
    val engine = manager.getEngineByName("javascript")

    val b = engine.createBindings().asInstanceOf[Bindings]
    val f = new File("public/javascripts/showdown.js")
    val reader = new FileReader(f)

    try {
      b.put("markdownText", markdown)
      engine.setBindings(b, ScriptContext.ENGINE_SCOPE);

      engine.eval(reader).asInstanceOf[String]
    }
    catch {
      case e: javax.script.ScriptException =>
        "The script had an error: " + e.getMessage();
    }
  }

}
