package fr.cayuelas.helpers

import fr.cayuelas.managers.{IoManager, LogsManager, StageManager}
import fr.cayuelas.objects.Wrapper

import scala.annotation.tailrec

object HelperDiff {


  /**
   * Creates a matrix containing the Longest common subsequence
   *
   * @param oldContent : old list of string ( Old content )
   * @param newContent : new list of String (new content)
   * @param i          : index for rows
   * @param j          : index for column
   * @param matrix     : Map used to store the subsequence
   * @return a map containing the Longest common subsequence
   */
  @tailrec
  def createMatrix(oldContent: List[String], newContent: List[String], i: Int, j: Int, matrix: Map[(Int, Int), Int]): Map[(Int, Int), Int] = {

    if (oldContent.length - 1 <= i && newContent.length <= j) matrix
    else {
      val newI = if (j == newContent.length) i + 1 else i
      val newJ = if (j == newContent.length) 0 else j
      if (newI == 0) {
        if (oldContent(newI) == newContent(newJ))
          createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> 1))
        else if (newJ == 0) createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> 0))
        else createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> matrix.getOrElse((newI - 1, newJ), 0)))
      }
      else if (newJ == 0) {
        if (oldContent(newI) == newContent(newJ)) createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> 1))
        else if (newI == 0) createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> 0))
        else createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> matrix.getOrElse((newI - 1, newJ), 0)))
      }
      else {
        if (oldContent(newI) == newContent(newJ)) {
          val newVal = matrix.getOrElse((newI - 1, newJ - 1), 0) + 1
          createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> newVal))
        }
        else {
          val newValue = Math.max(matrix.getOrElse((newI, newJ - 1), 0), matrix.getOrElse((newI - 1, newJ), 0))
          createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> newValue))
        }
      }
    }
  }
  /**
   * Function that retrieves all the deltas (différences between 2 list of strings) of two given lists and a matrix containing the subsequence
   *
   * @param oldContent : old list of string ( Old content )
   * @param newContent : new list of String (new content)
   * @param i          : index for rows
   * @param j          : index for column
   * @param matrix     : Map containing the Longest common subsequence
   * @param deltas     : List containing the resultat and all the trasformations
   * @return a list containing all the operations effectued (ADD or Remove content)
   */
  @tailrec
  def getDeltas(oldContent: List[String], newContent: List[String], i: Int, j: Int, matrix: Map[(Int, Int), Int], deltas: List[String]): List[String] = {
    if (i <= 0 && j <= 0) {
      if (matrix(i, j) == 0) List("+ " + newContent(j), "- " + oldContent(i)) ++ deltas
      else deltas
    }
    else if (i == 0 && j > 0) {
      if (matrix(i, j - 1) == matrix(i, j)) getDeltas(oldContent, newContent, i, j - 1, matrix, "+ " + newContent(j) :: deltas)
      else getDeltas(oldContent, newContent, i, j - 1, matrix, deltas)
    }
    else if (i > 0 && j == 0) {
      if (matrix(i - 1, j) == matrix(i, j)) getDeltas(oldContent, newContent, i - 1, j, matrix, "- " + oldContent(i) :: deltas)
      else getDeltas(oldContent, newContent, i - 1, j, matrix, deltas)
    }
    else {
      if (matrix(i, j - 1) == matrix(i, j)) getDeltas(oldContent, newContent, i, j - 1, matrix, "+ " + newContent(j) :: deltas)
      else if (matrix(i - 1, j) == matrix(i, j)) getDeltas(oldContent, newContent, i - 1, j, matrix, "- " + oldContent(i) :: deltas)
      else getDeltas(oldContent, newContent, i - 1, j - 1, matrix, deltas)
    }
  }
  /**
   * Functions that calculates the number of deletion or insertion in a file
   *
   * @param deltas : list of opérations
   * @return a tuple2 containing the number of Lines inserted and deleted in a file
   */
  def calculateDeletionAndInsertion(deltas: List[String]): (Int, Int) = (deltas.count(x => x.startsWith("+")), deltas.count(x => x.startsWith("-")))

  /**
   *Calcul the number of inserted lines for files never committed
   * @param listPathsNewFiles: list of new files
   * @param acc: accumulator of lines
   * @return the number of new lines
   */
  @tailrec
  def calculateNewsLinesWhenFileHasNeverBeenCommitted(listPathsNewFiles: List[Wrapper], acc: Int): Int = {
    if(listPathsNewFiles.isEmpty) acc
    else{
      val linesCounted = IoManager.readInFileAsLine(HelperPaths.sgitPath+listPathsNewFiles.head.path).length
      val newAcc = acc+linesCounted
      calculateNewsLinesWhenFileHasNeverBeenCommitted(listPathsNewFiles.tail,newAcc)
    }
  }

  /**
   * Accumulate the number of modifications
   * @param listOfHashesAndPaths: list of files to analyse
   * @param accumulator: accumulator of inserted and deleted
   * @return the number of lines inserted and deleted
   */
  @tailrec
  def accumulateCalculation(listOfHashesAndPaths: List[Wrapper], accumulator: (Int, Int)): (Int, Int) = {
    if (listOfHashesAndPaths.isEmpty) accumulator
    else {
      val contentBlob = HelperBlob.readContentInBlob(listOfHashesAndPaths.head.hash)
      val contentOfFile = IoManager.readInFileAsLine(HelperPaths.sgitPath+listOfHashesAndPaths.head.path)
      if (contentBlob.isEmpty && contentOfFile.nonEmpty) {
        val newAccumulator = ((accumulator._1+contentOfFile.length), accumulator._2 )
        accumulateCalculation(listOfHashesAndPaths.tail, newAccumulator)
      }
      else if (contentOfFile.isEmpty && contentBlob.nonEmpty) {
        val newAccumulator = (accumulator._1,(accumulator._2 + contentBlob.length))
        accumulateCalculation(listOfHashesAndPaths.tail, newAccumulator)
      }
      else {
        val deltas = getDeltas(contentBlob, contentOfFile, contentBlob.length - 1, contentOfFile.length - 1, createMatrix(contentBlob, contentOfFile, 0, 0, Map()), List())
        if (deltas.nonEmpty) {
          val (inserted, deleted) = calculateDeletionAndInsertion(deltas)
          val newAccumulator = (accumulator._1 + inserted, accumulator._2 + deleted)
          accumulateCalculation(listOfHashesAndPaths.tail, newAccumulator)
        }else{
          val newAccumulator = (accumulator._1 , accumulator._2 )
          accumulateCalculation(listOfHashesAndPaths.tail, newAccumulator)
        }
      }
    }
  }

  /**
   * Displays the difference between two files given in parameters
   *
   * @param oldContent   : content of the blob associated to the file
   * @param newContent : content of the real file
   * @param path          : path of the file
   * @param sha1          : sha1 of the blob
   */
  def displayDifferenceBetweenTwoFiles(oldContent: List[String], newContent: List[String], path: String, sha1: String): Unit = {
    if (oldContent.isEmpty && newContent.nonEmpty) {
      IoManager.printDiffForFile(path,sha1)
      IoManager.printDiff(newContent.map("+ "+ _))
    }
    else if (newContent.isEmpty && oldContent.nonEmpty) {
      IoManager.printDiffForFile(path,sha1)
      IoManager.printDiff(oldContent.map("- " +_))
    }
    else {
      val deltas = getDeltas(oldContent, newContent, oldContent.length - 1, newContent.length - 1, createMatrix(oldContent, newContent, 0, 0, Map()), List())
      if (deltas.nonEmpty) {
        IoManager.printDiffForFile(path,sha1)
        IoManager.printDiff(deltas)
      }
    }
  }

  /**
   *Do the diff between two commits and display the result with an othen function
   * @param commit: hash of the current commit
   * @param parentCommit: hash of the parent commit
   * @param logStat: boolean that indactes if it is log --stat or -p
   * @return lines inserted and deleted between two commits
   */
  def diffBetweenTwoCommits(commit: String, parentCommit: String, logStat: Boolean): (Int,Int) ={

    val listBlobCommit:  List[Wrapper] = HelperCommit.getAllBlobsFromCommit(commit)

    if(!parentCommit.equals("0000000000000000000000000000000000000000")){
      val listBlobParentCommit: List[Wrapper] = HelperCommit.getAllBlobsFromCommit(parentCommit)
      val res = recursiveDisplay(listBlobCommit,listBlobParentCommit,logStat, None)
      val (inserted,deleted) = res._3.getOrElse(0,0)
      (inserted,deleted)
    }else {
      val res =  recursiveDisplay(listBlobCommit,List(),logStat,None)
      val (inserted,deleted) = res._3.getOrElse(0,0)
      (inserted,deleted)
    }
  }

  /**
   *Function for call the right function of display depending of the logStat boolean
   * @param listBlobCommit: list of blobs of the commit
   * @param listBlobParent: list of blobs of the parent commit
   * @param logStat: boolean that indicates if it is log --stat or -p
   * @param res: accumulator of lines isnerted and deleted
   * @return an accumulation of modifications
   */
  @tailrec
  def recursiveDisplay(listBlobCommit: List[Wrapper], listBlobParent: List[Wrapper], logStat: Boolean, res: Option[(Int,Int)]): (List[Wrapper],List[Wrapper],Option[(Int,Int)]) = {
    val resOption= res.getOrElse(0,0)
    val ins = resOption._1
    val deleted = resOption._2

    if(listBlobParent.nonEmpty && listBlobCommit.nonEmpty){

      val contentBlobParent = HelperBlob.readContentInBlob(listBlobParent.head.hash)
      val contentBlobCurrent = HelperBlob.readContentInBlob(listBlobCommit.head.hash)
      if(logStat) {
        val resFunc = LogsManager.displayStatsLog(contentBlobParent, contentBlobCurrent, listBlobCommit.head.path)
        val resInsertedDeleted = Some((resFunc._1+ins,resFunc._2+deleted))
        recursiveDisplay(listBlobCommit.tail,listBlobParent.tail,logStat,resInsertedDeleted)
      }
      else{
        HelperDiff.displayDifferenceBetweenTwoFiles(contentBlobParent, contentBlobCurrent, listBlobCommit.head.path, listBlobCommit.head.hash)
        recursiveDisplay(listBlobCommit.tail,listBlobParent.tail,logStat,None)
      }
    }else{
      if(listBlobCommit.nonEmpty){
        val contentBlobCurrent = HelperBlob.readContentInBlob(listBlobCommit.head.hash)
        if(logStat){
          val resFunc = LogsManager.displayStatsLog(List(), contentBlobCurrent, listBlobCommit.head.path)
          val resInsertedDeleted = Some((resFunc._1+ins,resFunc._2+deleted))
          recursiveDisplay(listBlobCommit.tail,List(),logStat,resInsertedDeleted)
        }
        else{
          HelperDiff.displayDifferenceBetweenTwoFiles(List(), contentBlobCurrent, listBlobCommit.head.path, listBlobCommit.head.hash)
          recursiveDisplay(listBlobCommit.tail, List(),logStat,None)
        }
      }
      else (List(),List(),res)
    }
  }

  /**
   * Function that processes the diff between the stage beeing committed and the last commit in refs
   * @param lastCommit : Last commit in refs
   * @return the number of lines inserted and deleted over all the files between last commit and new content that will be committed
   */

  def diffWhenCommitting(lastCommit: String): (Int, Int) = {
    val stageSplited= StageManager.readStageAsLines().map(_.split(" "))
    val stageWrappered : List[Wrapper] = stageSplited.map(elem => Wrapper(elem(2),elem(1),"Blob",""))

    val listBlobLastCommit: List[Wrapper] = HelperCommit.getAllBlobsFromCommit(lastCommit)
    val listNewFiles: List[Wrapper] = HelperPaths.inFirstListButNotInSecondListWithPath(stageWrappered,listBlobLastCommit)//Files neverCommited

    val (inserted, deleted) = HelperDiff.accumulateCalculation(listBlobLastCommit, (0, 0))
    (inserted + HelperDiff.calculateNewsLinesWhenFileHasNeverBeenCommitted(listNewFiles,0), deleted)
  }

}
