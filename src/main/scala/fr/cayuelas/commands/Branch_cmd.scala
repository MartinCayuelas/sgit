package fr.cayuelas.commands


import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.helpers.HelperPaths
import fr.cayuelas.managers.{FilesManager, IOManager, StageManager}

object Branch_cmd {
  /*
  BRANCH---------------------
   */

  def branch(args: Array[String]): Unit = {
    if ((args.length == 2) && (args(1).equals("-av"))) {
      displayAllBranches()
      println("   ------------")
      Tag_cmd.displayAllTags()
    }
    else if (args.length == 2) createBranch(args(1))
    else println("Number of arguments not supported for the command 'branch'.")
  }


  def createBranch(nameBranch: String): Unit = {

    val path = Paths.get(HelperPaths.branchesPath + File.separator + nameBranch)
    if(Files.notExists(path)){
      FilesManager.createNewFile(path.toString)
      createStageForBranch(nameBranch)
    } else println(s"Fatal: a branch named ${nameBranch} is exits already")
  }

  def createStageForBranch(nameBranch: String): Unit = {
    val path = Paths.get(StageManager.currentStagePath)
    if(Files.notExists(path)) FilesManager.createNewFile(path.toString)
  }
  def getCurrentBranch: String = {
    val path = HelperPaths.headFile
    val content: String = IOManager.readInFile(path)
    val pattern = "([A-Za-z]+)(:) ([A-Za-z]+)(/)([A-Za-z]+)(/)([A-Za-z]+)".r
    val pattern(ref, a, refs,b,heads,c,currentBranch) = content
    currentBranch
  }
  def displayAllBranches(): Unit = {
    val currentBranch: String = getCurrentBranch
    val listOfBranches = FilesManager.getListOfFiles(HelperPaths.branchesPath)
    listOfBranches.map(b =>{
      if(currentBranch.equals(b.getName))println(s"* ${b.getName} (branch)")
      else println(s"  ${b.getName} (branch)")
    })
  }
}
