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
      case Nil => IOManager.printFatalError(nameBranch)
      case _ => {
        IOManager.printBranch(nameBranch)
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
   * @param logStat
   */
  def displayLogsOption(logStat: Boolean): Unit = {
    val branches = FilesManager.getListOfFiles(HelperPaths.branchesPath)
    branches.map(b => {
      displayOption(logStat,b.getName)
    })
  }

  /**
   *
   * @param logStat
   * @param nameBranch
   */
  def displayOption(logStat: Boolean, nameBranch: String): Unit = {
    val logs = getLogsForBranch(nameBranch)
    logs match {
      case Nil => IOManager.printFatalError(nameBranch)
      case _ => {
        IOManager.printBranch(nameBranch)
        recursiveLogs(logs)
      }
    }
    @tailrec
    def recursiveLogs(logs: List[String]): Unit = {
      if(logs.nonEmpty){
        println(logFormatting(logs.head)) //Format Display
        val (parentCommit,currentCommit): (String,String) =  (logs.head.split(" ")(0),logs.head.split(" ")(1))
        val res = HelperDiff.diffBetweenTwoCommits(currentCommit,parentCommit,logStat)
        if(logStat){
          val (inserted,deleted) = res
          val filesChanged = retrieveChanges(currentCommit,parentCommit)
          val resToPrint = filesChanged match {
            case  1 => filesChanged +s" file changed, ${inserted} insertions(+), ${deleted} deletions(-)"
            case _ => filesChanged +s" files changed, ${inserted} insertions(+), ${deleted} deletions(-)"
          }
          println(resToPrint)
        }
        recursiveLogs(logs.tail)
      }
    }
  }



  def retrieveChanges(lastCommit : String, parentLastCommit: String): Int = {
    if(parentLastCommit.equals("0000000000000000000000000000000000000000")){
      HelperCommit.getAllBlobsFromCommit(lastCommit).length
    }else{
      val listBlobLastCommit = HelperCommit.getAllBlobsFromCommit(lastCommit)
      val listBlobParentLastCommit = HelperCommit.getAllBlobsFromCommit(parentLastCommit)
      val listOfChanges = listBlobLastCommit.diff(listBlobParentLastCommit).length
      listOfChanges
    }
  }


  /**
   *
   * @param oldContent
   * @param newContent
   * @param path
   * @param sha1
   */
  def displayStatsLog(oldContent: List[String], newContent: List[String], path: String, sha1: String): (Int,Int) = {
    if (oldContent.isEmpty && newContent.nonEmpty) {
      val linesCounted = newContent.length
      printLineStat(oldContent,newContent,path,"+",linesCounted)
      (linesCounted,0)
    }
    else if (newContent.isEmpty && oldContent.nonEmpty) {
      val linesCounted = oldContent.length
      printLineStat(oldContent,newContent,path,"-",linesCounted)
      (0,linesCounted)
    }
    else {
      val matrix = createMatrix(oldContent, newContent, 0, 0, Map())
      val deltas = getDeltas(oldContent, newContent, oldContent.length - 1, newContent.length - 1, matrix, List())
      if (deltas.nonEmpty) {
        val (inserted, deleted) = calculateDeletionAndInsertion(deltas)
        val changes = inserted + deleted
        printLineStat(oldContent,newContent,path,"+-",changes)
        (inserted,deleted)
      }else (0,0)
    }
  }

  /**
   *
   * @param oldContent
   * @param newContent
   * @param path
   * @param typePrint
   * @param changes
   */
  def printLineStat(oldContent: List[String], newContent: List[String], path: String, typePrint: String, changes: Int): Unit ={
    typePrint match {
      case x if x.equals("+") =>println(path + " "*15+"|" + changes + Console.GREEN +"+"*changes+Console.RESET)
      case y if y.equals("-") => println(path + "                            | " + changes + Console.RED +"-"*changes+Console.RESET)
      case _ =>  println(path + " "*15+"|"  + changes + s"${Console.GREEN}  +${Console.RESET}" + s"${Console.RED}-${Console.RESET}")
    }
  }




}
