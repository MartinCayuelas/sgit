package fr.cayuelas.managers

import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.helpers.HelperDiff.{calculateDeletionAndInsertion, createMatrix, getDeltas}
import fr.cayuelas.helpers.{HelperBranch, HelperCommit, HelperDiff, HelperPaths}

import scala.annotation.tailrec

object LogsManager {

  /**
   * Get the current path for Logs file
   * @return a string containin a path
   */
  def getCurrentPathLogs : String = HelperPaths.logsPath + File.separator + HelperBranch.getCurrentBranch

  /**
   * Function that creates a new File in .sgit/objects/logs for a new branch
   */
  def createLogFileForBranch(nameBranch: String): Unit = {
    val path = Paths.get(HelperPaths.logsPath+File.separator+nameBranch)
    if(Files.notExists(path)) new File(path.toString).createNewFile()
  }

  /**
   * Retrieves Logs
   * @return the content of the logs of the current branch
   */
  def getLogsForBranch(nameBranch: String) : List[String] = IoManager.readInFileAsLine(HelperPaths.logsPath + File.separator +nameBranch).reverse


  /**
   * Function that transform a given string in other
   * @param stringToFormat : the string tha will be formated
   * @return a string to be displayed
   */
  def logFormatting(stringToFormat : String) : String = {
    val splitedString = stringToFormat.split(" ")
    s"${Console.YELLOW}commit ${splitedString(1)} ${Console.RESET} \nAuthor: ${splitedString(2)}\nDate:   ${splitedString(3)} ${splitedString(4)} ${splitedString(5)} ${splitedString(6)} ${splitedString(8)} +0200\n\n   ${splitedString(9)}\n"
  }

  /**
   * Lists all the logs for a given branch
   * @param nameBranch : current branch that will be diplayed
   */
  def listLogsForBranch(nameBranch: String): Unit = {
    val  listLogs = LogsManager.getLogsForBranch(nameBranch)
    listLogs match {
      case Nil => IoManager.printFatalError(nameBranch)
      case _ => {
        IoManager.printBranch(nameBranch)
        listLogs.map(IoManager.printLogFormated)
      }
    }
  }
  /**
   * Function that displays all the logs for the branches
   */
  def displayLogs() : Unit = {
    LogsManager.listLogsForBranch(HelperBranch.getCurrentBranch)
    FilesManager.getListOfFiles(HelperPaths.branchesPath).filter(b => b.getName != HelperBranch.getCurrentBranch).map(branch => {
      LogsManager.listLogsForBranch(branch.getName)
    })
  }

  /**
   *Call the displayOption Function for all the branches
   * @param logStat: boolean used to do the right action (true= log stat --stat, false log stat -p
   * */
  def displayLogsOption(logStat: Boolean): Unit = FilesManager.getListOfFiles(HelperPaths.branchesPath).map(b => {displayOption(logStat,b.getName)})

  /**
   *Display logs recursively given an option boolean
   * @param logStat: Boolean for option
   * @param nameBranch: name of the branch we display
   */
  def displayOption(logStat: Boolean, nameBranch: String): Unit = {
    val logs = getLogsForBranch(nameBranch)
    logs match {
      case Nil => IoManager.printFatalError(nameBranch)
      case _ => {
        IoManager.printBranch(nameBranch)
        recursiveLogs(logs)
      }
    }
    @tailrec
    def recursiveLogs(logs: List[String]): Unit = {
      if(logs.nonEmpty){
       IoManager.printLogFormated(logs.head)
        val (parentCommit,currentCommit): (String,String) =  (logs.head.split(" ")(0),logs.head.split(" ")(1))
        val res = HelperDiff.diffBetweenTwoCommits(currentCommit,parentCommit,logStat)
        if(logStat){
          val filesChanged = retrieveChanges(currentCommit,parentCommit)
          IoManager.printChanges(filesChanged,res._1,res._2)
        }
        recursiveLogs(logs.tail)
      }
    }
  }

  /**
   *Retrieves number of changed files
   * @param lastCommit: hash of the commit
   * @param parentLastCommit: hash of the parent commit
   * @return the number of files Changed
   */

  def retrieveChanges(lastCommit : String, parentLastCommit: String): Int = {
    if(parentLastCommit.equals("0000000000000000000000000000000000000000")) HelperCommit.getAllBlobsFromCommit(lastCommit).length
    else{
      val listBlobLastCommit = HelperCommit.getAllBlobsFromCommit(lastCommit)
      val listBlobParentLastCommit = HelperCommit.getAllBlobsFromCommit(parentLastCommit)
      listBlobLastCommit.diff(listBlobParentLastCommit).length
    }
  }

  /**
   * Dispatch and diplay to user the number of modifications given a file
   * @param oldContent : content of the old blob
   * @param newContent: Content of the new blob
   * @param path : path of the file
   */
  def displayStatsLog(oldContent: List[String], newContent: List[String], path: String): (Int,Int) = {
    if (oldContent.isEmpty && newContent.nonEmpty) {
      val linesCounted = newContent.length
      IoManager.printLineStat(path,"+",linesCounted)
      (linesCounted,0)
    }
    else if (newContent.isEmpty && oldContent.nonEmpty) {
      val linesCounted = oldContent.length
      IoManager.printLineStat(path,"-",linesCounted)
      (0,linesCounted)
    }
    else {
      val matrix = createMatrix(oldContent, newContent, 0, 0, Map())
      val deltas = getDeltas(oldContent, newContent, oldContent.length - 1, newContent.length - 1, matrix, List())
      if (deltas.nonEmpty) {
        val (inserted, deleted) = calculateDeletionAndInsertion(deltas)
        val changes = inserted + deleted
        IoManager.printLineStat(path,"+-",changes)
        (inserted,deleted)
      }else (0,0)
    }
  }


}
