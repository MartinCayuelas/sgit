package app.commands

import java.io.File
import java.nio.file.{Files, Paths}

import app.filesManager.FilesManager

object Branch_cmd {
  /*
  BRANCH---------------------
   */

  def branch(args: Array[String]): Unit = {
    if ((args.length == 2) && (args(1).equals("-av"))) {
      displayAllBranches()
      Tag_cmd.displayAllTags()
    }
    else if (args.length == 2) createBranch(args(1))
    else println("Number of arguments not supported for the command 'branch'.")
  }


  /*
  Branches--------------
   */

  def createBranch(nameBranch: String): Unit = {
    val path = Paths.get(s".sgit/refs/heads/${nameBranch}")
    if(Files.notExists(path)){
      new File(path.toString).createNewFile()
      createBranchStage(nameBranch)
    }else {
      println(s"Fatal: a branch named ${nameBranch} is exits already")
    }
  }

  def createBranchStage(nameBranch: String): Unit = {
    val path = Paths.get(s".sgit/stages/${nameBranch}")
    if(Files.notExists(path)){
      new File(path.toString).createNewFile()
    }
  }
  def getCurrentBranch: String = {
    val path = Paths.get(".sgit/").toAbsolutePath.toString.concat("/HEAD")
    val source = scala.io.Source.fromFile(path)
    val content = try source.mkString finally source.close()
    val pattern = "([A-Za-z]+)(:) ([A-Za-z]+)(/)([A-Za-z]+)(/)([A-Za-z]+)".r
    val pattern(ref, a, refs,b,heads,c,currentBranch) = content
    currentBranch

  }
  def displayAllBranches(): Unit = {
    val currentBranch: String = getCurrentBranch
    val listOfBranches = FilesManager.getListOfFiles(Paths.get(".sgit/refs/heads").toAbsolutePath.toString)
    listOfBranches.map(b =>{
      if(currentBranch.equals(b.getName)){
        println(s"* ${b.getName} (branch)")
      }else{
        println(s"  ${b.getName} (branch)")
      }
    })
  }


}
