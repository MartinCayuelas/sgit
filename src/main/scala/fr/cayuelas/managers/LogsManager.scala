package fr.cayuelas.managers

import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.commands.Branch_cmd
import fr.cayuelas.helpers.HelperPaths

object LogsManager {

  val currentLogsPath = HelperPaths.logsPath + File.separator + Branch_cmd.getCurrentBranch

  /**
   * Function that creates a new File in .sgit/objects/logs for a new branch
   */
  def createLogFileForBranch(): Unit = {
    val path = Paths.get(currentLogsPath)
    if(Files.notExists(path)) new File(path.toString).createNewFile()
  }


}
