package app.filesManager

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.{Files, Paths}

import app.commands.Branch_cmd

object Logs {
  def createLogFileForBranch(nameBranch: String): Unit = {
    val path = Paths.get(s".sgit/logs/${nameBranch}")
    if(Files.notExists(path)){
      new File(path.toString).createNewFile()
    }
  }

  def writeInLogs(contentCommit: String): Unit = {
    val currentBranch = Branch_cmd.getCurrentBranch
    val path = Paths.get(".sgit").toAbsolutePath.toString.concat(s"/logs/${currentBranch}")
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file,true))
    bw.write(contentCommit)
    bw.close()
  }
}
