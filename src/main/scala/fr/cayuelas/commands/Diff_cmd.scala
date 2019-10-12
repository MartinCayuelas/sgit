package fr.cayuelas.commands

object Diff_cmd {


  def diff() : Unit ={
    val str1 = "CHOCOLAT"
    val str2 = "CACHALOT"

    val seq1 = str1.split("").toSeq
    val seq2 = str2.split("").toSeq


    /*val path = getPathForDiffDown(seq1,seq2,seq1.length,seq2.length,List((seq1.length,seq2.length)))
    path.foreach(elem => {
      println(s"(${elem._1},${elem._2})")
    })
    println(path.size)*/

    val path2 = getPathForDiffUp(seq1,seq2,0,0,List((0,0)))
    path2.foreach(elem => {
      println(s"(${elem._1},${elem._2})")
    })
    println(path2.size)
  }

  def getPathForDiffUp(elementOne: Seq[String], elementTwo: Seq[String], i: Int, j:Int, path:List[(Int,Int)]): List[(Int,Int)] = {

    if ((i == elementOne.length && j == elementTwo.length)) path
    else {
      if ( i < elementOne.length && j == elementTwo.length) {
        getPathForDiffUp(elementOne,elementTwo,i+1,j, (i+1,j) :: path)
      }
      else if ( i == elementOne.length && j < elementTwo.length) {
        getPathForDiffUp(elementOne,elementTwo,i,j+1, (i,j+1) :: path)
      }
      else{
        if (elementOne(i) == elementTwo(j)){ //T[i] == T[j]
          getPathForDiffUp(elementOne,elementTwo,i+1,j+1,(i+1,j+1) :: path)
        }else{//T[i] != T[j]
          getPathForDiffUp(elementOne,elementTwo,i+1,j, (i+1,j) :: path)
          getPathForDiffUp(elementOne,elementTwo,i,j+1, (i,j+1) :: path)

        }

      }
    }
  }

  /*
    def getPathForDiffDown(elementOne: Seq[String], elementTwo: Seq[String], i: Int, j:Int, path:List[(Int,Int)]): List[(Int,Int)] = {
     // println(i +" , "+j)

      if ((i == 0 && j == 0)) path
      else {
        if ( i > 0 && j == 0) {
          getPathForDiffDown(elementOne,elementTwo,i-1,j, (i-1,j) :: path)
        }
        else if ( i == 0 && j > 0) {
          getPathForDiffDown(elementOne,elementTwo,i,j-1, (i,j-1) :: path)
        }
        else{

          if (elementOne(i-1) == elementTwo(j-1)){ //T[i] == T[j]
            getPathForDiffDown(elementOne,elementTwo,i-1,j-1,(i-1,j-1) :: path)
          }else{//T[i] != T[j]
            getPathForDiffDown(elementOne,elementTwo,i-1,j, (i-1,j) :: path)
            getPathForDiffDown(elementOne,elementTwo,i,j-1, (i,j-1) :: path)
          }

        }
      }
    }
  */
}