package fr.cayuelas.managers

import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.commands.Branch_cmd
import fr.cayuelas.helpers.HelperPaths

object LogsManager {

  /**
   * Get the current path for Logs file
   * @return a string containin a path
   */
  def getCurrentPathLogs : String = HelperPaths.logsPath + File.separator + Branch_cmd.getCurrentBranch

  /**
   * Function that creates a new File in .sgit/objects/logs for a new branch
   */
  def createLogFileForBranch(): Unit = {
    val path = Paths.get(getCurrentPathLogs)
    if(Files.notExists(path)) new File(path.toString).createNewFile()
  }

  /**
   * Retrieves Logs
   * @return the content of the logs of the current branch
   */
  def getCurrentLogs : List[String] = IOManager.readInFileAsLine(getCurrentPathLogs).reverse



}
