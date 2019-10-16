package fr.cayuelas.managers

import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.helpers.{HelperBranch, HelperDiff, HelperPaths}

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
  def createLogFileForBranch(): Unit = {
    val path = Paths.get(getCurrentPathLogs)
    if(Files.notExists(path)) new File(path.toString).createNewFile()
  }

  /**
   * Retrieves Logs
   * @return the content of the logs of the current branch
   */
  def getLogsForBranch(nameBranch: String) : List[String] = IOManager.readInFileAsLine(HelperPaths.logsPath + File.separator +nameBranch).reverse


  /**
   * Method that transform a given string in other
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
      case Nil => IOManager.printFatalError()
      case _ => {
        IOManager.printCurrentBranch()
        listLogs.map(log => println(logFormatting(log)))
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
   *
   */
  def displayLogOptionP(): Unit = {
    val logs = getLogsForBranch(HelperBranch.getCurrentBranch)
    logs match {
      case Nil => IOManager.printFatalError()
      case _ => {
        IOManager.printCurrentBranch()
        recursiveLogs(logs)
      }
    }
    @tailrec
    def recursiveLogs(logs: List[String]): Unit = {
      if(logs.nonEmpty){
        println(logFormatting(logs.head)) //Format Display
        val (parentCommit,currentCommit): (String,String) =  (logs.head.split(" ")(0),logs.head.split(" ")(1))
        HelperDiff.diffBetweenTwoCommits(currentCommit,parentCommit, logStat = false)
        recursiveLogs(logs.tail)
      }
    }
  }


  def displayLogOptionStat(): Unit = {
    val logs = getLogsForBranch(HelperBranch.getCurrentBranch)
    logs match {
      case Nil => IOManager.printFatalError()
      case _ => {
        IOManager.printCurrentBranch()
        recursiveLogs(logs)
      }
    }
    @tailrec
    def recursiveLogs(logs: List[String]): Unit = {
      if(logs.nonEmpty){
        println(logFormatting(logs.head)) //Format Display
        val (parentCommit,currentCommit): (String,String) =  (logs.head.split(" ")(0),logs.head.split(" ")(1))
        HelperDiff.diffBetweenTwoCommits(currentCommit,parentCommit,logStat = true)
        recursiveLogs(logs.tail)
      }
    }
  }

}
