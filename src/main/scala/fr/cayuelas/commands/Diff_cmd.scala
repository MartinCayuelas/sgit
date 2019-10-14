package fr.cayuelas.commands

import scala.annotation.tailrec

object Diff_cmd {


  def diff(): Unit = {
    /* val str1 = "LUCAS"
     val str2 = "LICORNE"

     val seq1 = str1.split("").toSeq
     val seq2 = str2.split("").toSeq

     val path2 = getPathForDiffUp(seq1, seq2, 0, 0, List((0, 0)))
     path2.foreach(elem => {
       println(s"(${elem._1},${elem._2})")
     })
     println(path2.size)*/

    val list1 = List("e", "b", "d")
    val list2 = List("a", "b", "d", "e")
    val matrix = createMatrix(list1,list2,0,0,Map())
    println(getDeltas(list1,list2,list1.length-1,list2.length-1,matrix, List()))

  }

  @tailrec
  def createMatrix(oldContent: List[String], newContent: List[String], i: Int, j: Int, matrix: Map[(Int, Int), Int]): Map[(Int, Int), Int] = {
    //Step 0 -> end
    if (oldContent.length - 1 <= i && newContent.length <= j) matrix
    else {
      val newI = if (j == newContent.length) i + 1 else i
      val newJ = if (j == newContent.length) 0 else j
      if (newI == 0) {
        if (oldContent(newI) == newContent(newJ)) createMatrix(oldContent, newContent, newI, newJ + 1, matrix + ((newI, newJ) -> 1))
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

  @tailrec
  def getDeltas(oldContent: List[String], newContent: List[String], i: Int, j: Int, matrix: Map[(Int, Int), Int], deltas: List[String]): List[String] = {
    if(i==0 && j==0) return deltas
    if(matrix(i,j) == 0 ){
      if (i==0 && j==0) List("+ "+newContent(j),"- "+oldContent(i))++deltas
      else deltas
    }
    else if(i==0 || matrix(i,j-1)==matrix(i,j)) getDeltas(oldContent,newContent,i,j-1,matrix,"+ "+newContent(j)::deltas)
    else if(j==0 || matrix(i-1,j)==matrix(i,j)) getDeltas(oldContent,newContent,i-1,j,matrix,"- "+oldContent(i)::deltas)
    else getDeltas(oldContent,newContent,i-1,j-1,matrix,deltas)
  }
}
