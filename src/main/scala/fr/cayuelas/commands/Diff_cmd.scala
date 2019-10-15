package fr.cayuelas.commands

import fr.cayuelas.helpers.HelperBlob
import fr.cayuelas.managers.{IOManager, StageManager}

import scala.annotation.tailrec

object Diff_cmd {

  /**
   * Main function of the diff_cmd
   * Retrieves the stage and use it to perform the deiff over all the files staged
   */
  def diff(): Unit = {
    val stageToCommitSplited = StageManager.readStageToCommit().map(x => x.split(" "))
    stageToCommitSplited.map(file => {
      val contentBlob = HelperBlob.readContentInBlob(file(1))
      val contentOfFile = IOManager.readInFileAsLine(file(2))
      displayDifferenceBetweenTwoFiles(contentBlob, contentOfFile, file(2), file(1))
    })
  }

  def diffWhenCommitting(): (Int, Int) = {
    val stageToCommitSplited = StageManager.readStageAsLines().map(x => x.split(" "))
    val hashes = stageToCommitSplited.map(x => x(1))
    val paths = stageToCommitSplited.map(x => x(2))
    val listZippedStageToCommit: List[(String, String)] = hashes.zip(paths)
    val stageSplited = StageManager.readStageAsLines().map(x => x.split(" "))
    val hashesS = stageSplited.map(x => x(1))
    val pathsS = stageSplited.map(x => x(2))
    val listZippedStage: List[(String, String)] = hashesS.zip(pathsS)

    val listZippedFiltered = listZippedStage.filter(x => listZippedStageToCommit.exists(y => y._2 == x._2))


    val (inserted, deleted) = accumulateCalculation(listZippedFiltered, (0, 0))
    (inserted, deleted)
  }

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

  @tailrec
  def accumulateCalculation(listOfHashesAndPaths: List[(String, String)], accumulator: (Int, Int)): (Int, Int) = {
    println(accumulator)
    if (listOfHashesAndPaths.isEmpty) accumulator
    else {
      val contentBlob = HelperBlob.readContentInBlob(listOfHashesAndPaths.head._1)
      val contentOfFile = IOManager.readInFileAsLine(listOfHashesAndPaths.head._2)
      val matrix = createMatrix(contentBlob, contentOfFile, 0, 0, Map())
      val deltas = getDeltas(contentBlob, contentOfFile, contentBlob.length - 1, contentOfFile.length - 1, matrix, List())
      val (inserted, deleted) = calculateDeletionAndInsertion(deltas)
      val newAccumulator = (accumulator._1 + inserted, accumulator._2 + deleted)
      accumulateCalculation(listOfHashesAndPaths.tail, newAccumulator)
    }
  }

  /**
   * Displays the différence between two files given in parameters
   *
   * @param contentBlob   : content of the blob associated to the file
   * @param contentOfFile : content of the real file
   * @param path          : path of the file
   * @param sha1          : sha1 of the blob
   */
  def displayDifferenceBetweenTwoFiles(contentBlob: List[String], contentOfFile: List[String], path: String, sha1: String): Unit = {
    if (contentBlob.isEmpty && contentOfFile.nonEmpty) {
      println(s"diff --sgit a/${path} b/${path}\nindex ${sha1.substring(0, 7)}..${sha1.substring(sha1.length - 7, sha1.length)}\n--- a/${path}\n+++ b/${path}\n")
      printDiff(contentOfFile.map(e => "+ " + e))
    }
    else if (contentOfFile.isEmpty && contentBlob.nonEmpty) {
      println(s"\ndiff --sgit a/${path} b/${path}\nindex ${sha1.substring(0, 7)}..${sha1.substring(sha1.length - 7, sha1.length)}\n--- a/${path}\n+++ b/${path}\n")
      printDiff(contentBlob.map(e => "- " + e))
    }
    else {
      val matrix = createMatrix(contentBlob, contentOfFile, 0, 0, Map())
      val deltas = getDeltas(contentBlob, contentOfFile, contentBlob.length - 1, contentOfFile.length - 1, matrix, List())
      if (deltas.nonEmpty) {
        println(s"diff --sgit a/${path} b/${path}\nindex ${sha1.substring(0, 7)}..${sha1.substring(sha1.length - 7, sha1.length)}\n--- a/${path}\n+++ b/${path}\n")
        printDiff(deltas)
      }
    }
  }


  /**
   * Prints the différence with the deltas given
   * In green if content is added else in red
   *
   * @param deltas : List containing the différences
   */
  @tailrec
  def printDiff(deltas: List[String]): Unit = {
    if (deltas.nonEmpty) {
      if (deltas.head.startsWith("+")) println(Console.GREEN + deltas.head + Console.RESET)
      else println(Console.RED + deltas.head + Console.RESET)
      printDiff(deltas.tail)
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
        else if (newJ == 0) createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> 0))
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
    if (i == 0 && j == 0) {
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
}
