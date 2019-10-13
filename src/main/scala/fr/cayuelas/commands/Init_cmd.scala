package fr.cayuelas.commands


import java.io.File
import java.nio.file.Paths

import fr.cayuelas.helpers.HelperPaths
import fr.cayuelas.managers.{FilesManager, IOManager, LogsManager, StageManager}

import scala.annotation.tailrec

object Init_cmd {

  /*
INIT -----------
 */
  def init(strPath : String): Unit = {
    initSgitRepository(strPath)
  }
  /*
  --------------
   */

  //base path
  def SgitRepositoryName: String = ".sgit"

  /**
  * initSgitRepository:
  * Function that create all the necessary folders for sgit
   * @param path : the path where the .sgit repository will be created
  *              Inform the user with a warning message if sommething went wrong (i.e .sgit already exists) or Inform the user that all is successful
   *             Creates an architecture with différents files and folders
   *             .sgit/
   *                 ├── HEAD
   *                 ├── logs
   *                 │   └── master
   *                 ├── objects
   *                 │   ├── blobs
   *                 │   ├── commits
   *                 │   └── trees
   *                 ├── refs
   *                 │   ├── heads
   *                 │   │   └── master
   *                 │   └── tags
   *                 └── stages
   *                 │     ├── master
   *                 │     ├── stageCommit
   *                 │     └── stageValidated
   *                 └── master
  */

  def initSgitRepository(path: String): Unit = {
    val listFolders = List("objects", s"objects${File.separator}blobs", s"objects${File.separator}trees", s"objects${File.separator}commits", "refs", s"refs${File.separator}heads", s"refs${File.separator}tags", "logs", "stages")
    val listFiles:List[String] = List("HEAD")
    val path = Paths.get(System.getProperty("user.dir")).toString
    val sgitPath = path + File.separator + SgitRepositoryName

    if (!isInSgitRepository(path)) {
      FilesManager.createNewFolder(SgitRepositoryName) //Creates .sgit
      listFolders.map(folder => FilesManager.createNewFolder(sgitPath + File.separator + folder))
      listFiles.map(file => FilesManager.createNewFile(sgitPath + File.separator + file))

      FilesManager.createNewFile(HelperPaths.branchesPath + File.separator + "master") //Creates file for master branch in refs/heads
      IOManager.writeInFile(HelperPaths.headFile,"ref: refs/heads/master",append = false)//WriteInHEAD
      LogsManager.createLogFileForBranch() //creates file log for master branch
      FilesManager.createNewFile(StageManager.currentStagePath)// Creates file stage for master branch
      FilesManager.createNewFile(StageManager.stageCommitPath)// Creates file for stageCommit
      FilesManager.createNewFile(StageManager.stageValidatedPath)// Creates file for stageValidated

      IOManager.emptyRepositoryInitialized(path)
    }
    else IOManager.sgitRepoAlreadyExists()
  }

  /**
   * Method that create all the necessary folders for sgit
   *
   * @param path : the path we want to check
   * @return true if a .sgit folder already exists in a parent folder else false
   *
   */
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
