package app.commands

import java.io.File
import java.nio.file.Paths

import app.filesManager.FilesIO.writeHead
import app.filesManager.Logs

import scala.annotation.tailrec

object Init_cmd {

  /*
INIT -----------
 */
  def init(): Unit = {
    initSgitRepository(".")
  }
  /*
  --------------
   */

  /*
  * initSgitRepository:
  * Method that create all the necessary folders for sgit
  * Inform the user with a warning message if sommething went wrong (i.e .sgit already exists) or Inform the user that all is successful
  */

  def initSgitRepository(path: String): Unit = {
    val listFolders = List("objects", s"objects${File.separator}blobs", s"objects${File.separator}trees", s"objects${File.separator}commits", "refs", s"refs${File.separator}heads", s"refs/tags", "logs", "stages")
    val listFiles = List("HEAD")
    val path = Paths.get("").toAbsolutePath.toString
    val sgitPath = path + File.separator + ".sgit"
    val sgitRepository = new File(".sgit")

    if (!isInSgitRepository(path)) {
      sgitRepository.mkdir()
      listFolders.map(folder => new File(sgitPath + File.separator + folder).mkdir())
      listFiles.map(file => new File(sgitPath + File.separator + file).createNewFile())
      new File(Paths.get(".sgit").toAbsolutePath.toString.concat(s"${File.separator}refs${File.separator}heads${File.separator}master")).createNewFile()
      writeHead()

      val currentBranch = Branch_cmd.getCurrentBranch
      Logs.createLogFileForBranch(currentBranch)
      new File(Paths.get(".sgit").toAbsolutePath.toString.concat(s"${File.separator}stages${File.separator}${currentBranch}")).createNewFile()

      println(s"Empty Sgit repository initialized in ${path}/.sgit/")
    }
    else println(s"Sgit repository already exists.")
  }


  def isInSgitRepository(path: String): Boolean = {
    @tailrec
    def searchForSgitRepo(currentPath: String): Boolean = {
      val currentSgitFile = new File(s"${currentPath}${File.separator}.sgit")
      val currentFile = new File(currentPath)
      if (currentFile.getParent != null) currentSgitFile.exists() || searchForSgitRepo(currentFile.getParent)
      else currentSgitFile.exists()
    }
    searchForSgitRepo(path)
  }
}
