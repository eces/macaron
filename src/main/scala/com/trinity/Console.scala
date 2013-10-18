package com.trinity

import java.io._
import scalax.file._
import scala.annotation.tailrec
import jline.console.ConsoleReader
import jline.console.completer.Completer
import jline.console.completer.FileNameCompleter
import jline.console.completer.StringsCompleter
import jline.UnixTerminal

/**
 * provides the console infrastructure
 */
object Console {

  val consoleReader = new ConsoleReader

  val logo = Colors.yellow(
    """|
       | _ __ ___   __ _  ___ __ _ _ __ ___  _ __  
       || '_ ` _ \ / _` |/ __/ _` | '__/ _ \| '_ \ 
       || | | | | | (_| | (_| (_| | | | (_) | | | |
       ||_| |_| |_|\__,_|\___\__,_|_|  \___/|_| |_|
       |
       |""".stripMargin + " http://trinity.so")

  // -- Commands

  def replace(file: File, tokens: (String, String)*) {
    if (file.exists) {
      Path(file).write(tokens.foldLeft(Path(file).string) { (state, token) =>
        state.replace("%" + token._1 + "%", token._2)
      })
    }
  }

  private def generateLocalTemplate(template: String, name: String, path: File): Unit = {
    // val random = new java.security.SecureRandom
    // val newSecret = (1 to 64).map { _ =>
    //   (random.nextInt(74) + 48).toChar
    // }.mkString.replaceAll("\\\\+", "/")

    // def copyRecursively(from: Path, target: Path) {
    //   import scala.util.control.Exception._
    //   from.copyTo(target)
    //   from.children().foreach { child =>
    //     catching(classOf[java.io.IOException]) opt copyRecursively(child, target / child.name)
    //   }
    // }

    // copyRecursively(
    //   from = Path(new File(System.getProperty("play.home") + "/skeletons/" + template)),
    //   target = Path(path))

    // replace(new File(path, "build.sbt"),
    //   "APPLICATION_NAME" -> name)
    // replace(new File(path, "project/plugins.sbt"),
    //   "PLAY_VERSION" -> play.core.PlayVersion.current)
    // replace(new File(path, "project/build.properties"),
    //   "SBT_VERSION" -> play.core.PlayVersion.sbtVersion)
    // replace(new File(path, "conf/application.conf"),
    //   "APPLICATION_SECRET" -> newSecret)
  }

  /**
   * Could use the SBT ID parser, except that means going direct into SBT code, which is supposed to be separated
   * and bridged by the xsbti API.  This is semantically equivalent.
   */
  private val IdParser = """(\p{L}[\p{L}\p{LD}_-]*)""".r

  /**
   * Description
   * @param args first parameter is the application name
   */
  def startCommand(args: Array[String]): (String, Int) = {

    val path = args.headOption.map(new File(_)).getOrElse(new File(".")).getCanonicalFile

    val defaultName = path.getName

    println()
    println(Colors.green("The cluster will be configured by %s".format(path.getAbsolutePath)))
    println()

    ("Canceled", 0)

  }

  def stopCommand(args: Array[String]): (String, Int) = {

    val path = args.headOption.map(new File(_)).getOrElse(new File(".")).getCanonicalFile

    val defaultName = path.getName

    println()
    println(Colors.green("Stopping ..."))
    println()

    ("Canceled", 0)

  }

  // @tailrec
  // def readApplicationName(defaultName: String): String = {
  //   consoleReader.println("What is the application name? [%s]".format(defaultName))
  //   Option(consoleReader.readLine(Colors.cyan("> "))).map(_.trim).filter(_.size > 0).getOrElse(defaultName) match {
  //     case IdParser(name) => name
  //     case _ => {
  //       consoleReader.println(Colors.red("Error: ") + "Application name may only contain letters, digits, '_' and '-', and it must start with a letter.")
  //       consoleReader.println()
  //       readApplicationName(defaultName)
  //     }
  //   }
  // }

  def helpCommand(args: Array[String]): (String, Int) = {
    (
      """|Welcome to macaron!
         |
         |These commands are available:
         |-----------------------------
         |start       TODO
         |stop        TODO
         |
         |You can also browse the complete documentation at http://github.com/eces/macaron.""".stripMargin, 0)
  }

  /**
   * This is a general main method that runs the console and returns an exit code.
   */
  def run(args: Array[String]): Int = {
    // consoleReader.addCompleter( new StringsCompleter("start","stop") )

    println(logo)

    val (text, status) = args.headOption.collect {
      case "start" => startCommand _
      case "stop" => stopCommand _
      case "help" => helpCommand _
    }.map { command =>
      command(args.drop(1))
    }.getOrElse {
      helpCommand(args)
    }

    println(text)
    println()
    consoleReader.getTerminal.restore()

    status
  }

  /**
   * this is the entry point as an application.
   */
  def main(args: Array[String]): Unit = {
    val status = run(args)
    System.exit(status)
  }
}

/**
 * this is the sbt entry point for Play's console implementation
 */
class Console extends xsbti.AppMain {

  def run(app: xsbti.AppConfiguration): Exit = {
    val status = Console.run(app.arguments)
    Exit(status)
  }

  case class Exit(val code: Int) extends xsbti.Exit

}

object Colors {

  import scala.Console._

  lazy val isANSISupported = {
    Option(System.getProperty("sbt.log.noformat")).map(_ != "true").orElse {
      Option(System.getProperty("os.name"))
        .map(_.toLowerCase)
        .filter(_.contains("windows"))
        .map(_ => false)
    }.getOrElse(true)
  }

  def red(str: String): String = if (isANSISupported) (RED + str + RESET) else str
  def blue(str: String): String = if (isANSISupported) (BLUE + str + RESET) else str
  def cyan(str: String): String = if (isANSISupported) (CYAN + str + RESET) else str
  def green(str: String): String = if (isANSISupported) (GREEN + str + RESET) else str
  def magenta(str: String): String = if (isANSISupported) (MAGENTA + str + RESET) else str
  def white(str: String): String = if (isANSISupported) (WHITE + str + RESET) else str
  def black(str: String): String = if (isANSISupported) (BLACK + str + RESET) else str
  def yellow(str: String): String = if (isANSISupported) (YELLOW + str + RESET) else str

}

