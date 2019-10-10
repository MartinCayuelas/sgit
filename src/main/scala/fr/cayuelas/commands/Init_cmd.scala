package fr.cayuelas.commands


import java.io.File
import java.nio.file.Paths

import fr.cayuelas.filesManager.{FilesIO, Logs}

import scala.annotation.tailrec

object Init_cmd {

  /*
INIT -----------
 */
  def init(): Unit = {
    initSgitRepository(System.getProperty("user.dir"))
  }
  /*
  --------------
   */

  //base path
  val SgitRepositoryName = ".sgit"

  /*
  * initSgitRepository:
  * Method that create all the necessary folders for sgit
  * Inform the user with a warning message if sommething went wrong (i.e .sgit already exists) or Inform the user that all is successful
  */

  def initSgitRepository(path: String): Unit = {
    val listFolders = List("objects", s"objects${File.separator}blobs", s"objects${File.separator}trees", s"objects${File.separator}commits", "refs", s"refs${File.separator}heads", s"refs${File.separator}tags", "logs", "stages")
    val listFiles:List[String] = List("HEAD")
    val path = Paths.get(System.getProperty("user.dir")).toString
    val sgitPath = path + File.separator + SgitRepositoryName
    val sgitRepository = new File(SgitRepositoryName)

    if (!isInSgitRepository(path)) {
      sgitRepository.mkdir()
      listFolders.map(folder => new File(sgitPath + File.separator + folder).mkdir())
      listFiles.map(file => new File(sgitPath + File.separator + file).createNewFile())
      new File(Paths.get(SgitRepositoryName).toString.concat(s"${File.separator}refs${File.separator}heads${File.separator}master")).createNewFile()

      FilesIO.writeInFile(Paths.get(".sgit").toString.concat("/HEAD"),"ref: refs/heads/master",false)//WriteInHEAD

      val currentBranch = Branch_cmd.getCurrentBranch
      Logs.createLogFileForBranch(currentBranch)
      new File(Paths.get(SgitRepositoryName).toString.concat(s"${File.separator}stages${File.separator}${currentBranch}")).createNewFile()

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
