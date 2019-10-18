package fr.cayuelas.helpers

import fr.cayuelas.managers.{IOManager, LogsManager}

import scala.annotation.tailrec

object HelperDiff {

  /**
   * Methods that calculates the number of deletion or insertion in a file
   *
   * @param deltas : list of opérations
   * @return a tuple2 containing the number of Lines inserted and deleted in a file
   */
  def calculateDeletionAndInsertion(deltas: List[String]): (Int, Int) = {
    val insertedLines = deltas.count(x => x.startsWith("+"))
    val deletedLines = deltas.count(x => x.startsWith("-"))
    (insertedLines, deletedLines)
  }

  /**
   *
   * @param listPathsNewFiles
   * @param acc
   * @return
   */
  def calculateNewsLinesWhenFileHasNeverBeenCommitted(listPathsNewFiles: List[String], acc: Int): Int = {
    if(listPathsNewFiles.isEmpty) acc
    else{
      val linesCounted = IOManager.readInFileAsLine(HelperPaths.sgitPath+listPathsNewFiles.head).length
      val newAcc = acc+linesCounted
      calculateNewsLinesWhenFileHasNeverBeenCommitted(listPathsNewFiles.tail,newAcc)
    }
  }

  /**
   *
   * @param listOfHashesAndPaths
   * @param accumulator
   * @return
   */
  @tailrec
  def accumulateCalculation(listOfHashesAndPaths: List[(String, String)], accumulator: (Int, Int)): (Int, Int) = {
    if (listOfHashesAndPaths.isEmpty) accumulator
    else {
      val contentBlob = HelperBlob.readContentInBlob(listOfHashesAndPaths.head._1)
      val contentOfFile = IOManager.readInFileAsLine(HelperPaths.sgitPath+listOfHashesAndPaths.head._2)
      if (contentBlob.isEmpty && contentOfFile.nonEmpty) {
        val newAccumulator = ((accumulator._1+contentOfFile.length), accumulator._2 )
        accumulateCalculation(listOfHashesAndPaths.tail, newAccumulator)
      }
      else if (contentOfFile.isEmpty && contentBlob.nonEmpty) {
        val newAccumulator = (accumulator._1,(accumulator._2 + contentBlob.length))
        accumulateCalculation(listOfHashesAndPaths.tail, newAccumulator)
      }
      else {
        val matrix = createMatrix(contentBlob, contentOfFile, 0, 0, Map())
        val deltas = getDeltas(contentBlob, contentOfFile, contentBlob.length - 1, contentOfFile.length - 1, matrix, List())
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
   * Displays the différence between two files given in parameters
   *
   * @param oldContent   : content of the blob associated to the file
   * @param newContent : content of the real file
   * @param path          : path of the file
   * @param sha1          : sha1 of the blob
   */
  def displayDifferenceBetweenTwoFiles(oldContent: List[String], newContent: List[String], path: String, sha1: String): Unit = {

    if (oldContent.isEmpty && newContent.nonEmpty) {
      IOManager.printDiffForFile(path,sha1)
      IOManager.printDiff(newContent.map(e => "+ " + e))
    }
    else if (newContent.isEmpty && oldContent.nonEmpty) {
      IOManager.printDiffForFile(path,sha1)
      IOManager.printDiff(oldContent.map(e => "- " + e))
    }
    else {
      val matrix = createMatrix(oldContent, newContent, 0, 0, Map())
      val deltas = getDeltas(oldContent, newContent, oldContent.length - 1, newContent.length - 1, matrix, List())
      if (deltas.nonEmpty) {
        IOManager.printDiffForFile(path,sha1)
        IOManager.printDiff(deltas)
      }
    }
  }


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
    //Step 0 -> end

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
   * Method that retrieves all the deltas (différences between 2 list of strings) of two given lists and a matrix containing the subsequence
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
   *
   * @param commit
   * @param parentCommit
   * @param logStat
   */
  def diffBetweenTwoCommits(commit: String, parentCommit: String, logStat: Boolean): (Int,Int) ={
    val listBlobCommit: List[(String,String)] = HelperCommit.getAllBlobsFromCommit(commit)
    if(!parentCommit.equals("0000000000000000000000000000000000000000")){
      val listBlobLParentCommit: List[(String,String)] = HelperCommit.getAllBlobsFromCommit(parentCommit)
      val res = recursiveDisplay(listBlobCommit,listBlobLParentCommit,logStat, None)
      val (inserted,deleted) = res._3.getOrElse(0,0)
      (inserted,deleted)
    }else {
      val res =  recursiveDisplay(listBlobCommit,List(),logStat,None)
      val (inserted,deleted) = res._3.getOrElse(0,0)
      (inserted,deleted)
    }
  }

  /**
   *
   * @param listBlobCommit
   * @param listBlobParent
   * @param logStat
   * @param res
   * @return
   */
  @tailrec
  def recursiveDisplay(listBlobCommit:List[(String,String)], listBlobParent:List[(String,String)], logStat: Boolean, res: Option[(Int,Int)]): (List[(String,String)],List[(String,String)],Option[(Int,Int)]) = {
    val resOption= res.getOrElse(0,0)
    val ins = resOption._1
    val deleted = resOption._2
    if(listBlobParent.nonEmpty && listBlobCommit.nonEmpty){
      val contentBlobParent = HelperBlob.readContentInBlob(listBlobParent.head._1)
      val contentBlobCurrent = HelperBlob.readContentInBlob(listBlobCommit.head._1)
      if(logStat) {
        val resFunc = LogsManager.displayStatsLog(contentBlobParent, contentBlobCurrent, listBlobCommit.head._2, listBlobCommit.head._1)
        val resInsertedDeleted = Some((resFunc._1+ins,resFunc._2+deleted))
        recursiveDisplay(listBlobCommit.tail,listBlobParent.tail,logStat,resInsertedDeleted)
      }
      else{
        HelperDiff.displayDifferenceBetweenTwoFiles(contentBlobParent, contentBlobCurrent, listBlobCommit.head._2, listBlobCommit.head._1)
        recursiveDisplay(listBlobCommit.tail,listBlobParent.tail,logStat,None)
      }
    }else{
      if(listBlobCommit.nonEmpty){
        val contentBlobCurrent = HelperBlob.readContentInBlob(listBlobCommit.head._1)
        if(logStat){
          val resFunc = LogsManager.displayStatsLog(List(), contentBlobCurrent, listBlobCommit.head._2, listBlobCommit.head._1)
          val resInsertedDeleted = Some((resFunc._1+ins,resFunc._2+deleted))
          recursiveDisplay(listBlobCommit.tail,List(),logStat,resInsertedDeleted)
        }
        else{
          HelperDiff.displayDifferenceBetweenTwoFiles(List(), contentBlobCurrent, listBlobCommit.head._2, listBlobCommit.head._1)
          recursiveDisplay(listBlobCommit.tail, List(),logStat,None)
        }
      }
      else (List(),List(),res)
    }
  }


}
