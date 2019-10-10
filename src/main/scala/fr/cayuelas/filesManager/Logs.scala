package fr.cayuelas.filesManager

import java.io.File
import java.nio.file.{Files, Paths}

object Logs {

  def createLogFileForBranch(nameBranch: String): Unit = {
    val path = Paths.get(s".sgit/logs/${nameBranch}")
    if(Files.notExists(path)) new File(path.toString).createNewFile()
  }


}
