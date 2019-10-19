package fr.cayuelas.helpers

import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.managers.{FilesManager, IoManager, LogsManager}

object HelperBranch {
  /**
   * Function that creates a new branch
   * @param nameBranch : the name of the new branch
   *  Creates a new file in /objects/refs/heads/<branchName>
   *
   */
  def createBranch(nameBranch: String): Unit = {
    if (HelperCommit.existsCommit){
      val path = HelperPaths.branchesPath + File.separator + nameBranch
      if(Files.notExists(Paths.get(path))){
        FilesManager.createNewFile(path)
        IoManager.writeInFile(path,HelperCommit.getLastCommitInRefs(),append = false)
        createStageForBranch(nameBranch) //Creates a new file in /objects/stage/branchName>
        LogsManager.createLogFileForBranch(nameBranch)
      } else IoManager.printFatalCreation("branch",nameBranch)
    }else IoManager.printErrorNoCommitExisting()

  }

  /**
   *Creates a new file in /objects/stage/branchName>
   * @param nameBranch: name of the new stage
   */
  def createStageForBranch(nameBranch: String): Unit = {
    val path = Paths.get(HelperPaths.stagePath+File.separator+nameBranch)
    if(Files.notExists(path)) FilesManager.createNewFile(path.toString)
    val stageMaster = IoManager.readInFileAsLine(HelperPaths.stagePath+File.separator+"master")
    stageMaster.map(line =>  IoManager.writeInFile(HelperPaths.stagePath+File.separator+nameBranch,line+"\n",append = true))
  }

  /**
   * Given the content of the HEAD file in .sgit/HEAD
   * @return the current branch
   */
  def getCurrentBranch: String = {
    val path = HelperPaths.headFile
    val content: String = IoManager.readInFile(path)
    val pattern = "([A-Za-z]+)(:) ([A-Za-z]+)(/)([A-Za-z]+)(/)([A-Za-z]+)".r
    val pattern(_, _, _,_,_,_,currentBranch) = content
    currentBranch
  }

  /**
   * Function that display all branches
   */
  def displayAllBranches(): Unit = FilesManager.getListOfFiles(HelperPaths.branchesPath).map(formatAndDisplayBranch) //Getting all the files in objects/refs/heads

  /**
   * Format the string following if the branch is the current or not
   * @param fileToFormat : file to check
   */

  def formatAndDisplayBranch(fileToFormat: File): Unit = {
    if (getCurrentBranch.equals(fileToFormat.getName)) IoManager.printCurrentBranch(fileToFormat.getName)
    else IoManager.printNonCurrentBranch(fileToFormat.getName)
  }

  /**
   *Checks if the branch name given exists
   * @param nameBranch: name of yhe branch we want to checks
   * @return true if the branch exists else false
   */

  def isABranch(nameBranch: String): Boolean = FilesManager.getListOfFiles(HelperPaths.branchesPath).exists(b => b.getName == nameBranch)

  /**
   *Writes in the HEAD file the new ref to the new branch
   * @param nameBranch : new name
   */
  def setNewBranchInHEAD(nameBranch: String): Unit = IoManager.writeInFile(HelperPaths.headFile,s"ref: refs/heads/${nameBranch}",append = false)

}
