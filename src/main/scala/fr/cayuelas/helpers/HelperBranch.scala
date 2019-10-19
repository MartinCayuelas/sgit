package fr.cayuelas.helpers

import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.managers.{FilesManager, IOManager, LogsManager}

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
        IOManager.writeInFile(path,HelperCommit.getLastCommitInRefs(),append = false)
        createStageForBranch(nameBranch) //Creates a new file in /objects/stage/branchName>
        LogsManager.createLogFileForBranch(nameBranch)
      } else IOManager.printFatalCreation("branch",nameBranch)
    }else IOManager.printErrorNoCommitExisting()

  }

  /**
   *Creates a new file in /objects/stage/branchName>
   * @param nameBranch: name of the new stage
   */
  def createStageForBranch(nameBranch: String): Unit = {
    val path = Paths.get(HelperPaths.stagePath+File.separator+nameBranch)
    if(Files.notExists(path)) FilesManager.createNewFile(path.toString)
    val stageMaster =IOManager.readInFileAsLine(HelperPaths.stagePath+File.separator+"master")
    stageMaster.map(line =>  IOManager.writeInFile(HelperPaths.stagePath+File.separator+nameBranch,line+"\n",true))
  }

  /**
   * Given the content of the HEAD file in .sgit/HEAD
   * @return the current branch
   */
  def getCurrentBranch: String = {
    val path = HelperPaths.headFile
    val content: String = IOManager.readInFile(path)
    val pattern = "([A-Za-z]+)(:) ([A-Za-z]+)(/)([A-Za-z]+)(/)([A-Za-z]+)".r
    val pattern(_, _, _,_,_,_,currentBranch) = content
    currentBranch
  }

  /**
   * Function that display all branches
   */
  def displayAllBranches(): Unit = {
    val listOfBranches = FilesManager.getListOfFiles(HelperPaths.branchesPath) //Getting all the files in objects/refs/heads
    listOfBranches.map(b => formatAndDisplayBranch(b))
  }

  /**
   * Format the string following if the branch is the current or not
   * @param fileToFormat : file to check
   */

  def formatAndDisplayBranch(fileToFormat: File): Unit = {
    if (getCurrentBranch.equals(fileToFormat.getName)) IOManager.printCurrentBranch(fileToFormat.getName)
    else IOManager.printNonCurrentBranch(fileToFormat.getName)
  }

  /**
   *Checks if the branch name given exists
   * @param nameBranch
   * @return true if the branch exists else false
   */

  def isABranch(nameBranch: String): Boolean = {
    val branches = FilesManager.getListOfFiles(HelperPaths.branchesPath)
    branches.exists(b => b.getName == nameBranch)
  }

  /**
   *Writes in the HEAD file the new ref to the new branch
   * @param nameBranch : new name
   */
  def setNewBranchInHEAD(nameBranch: String): Unit = {
    IOManager.writeInFile(HelperPaths.headFile,s"ref: refs/heads/${nameBranch}",append = false)
  }

}
