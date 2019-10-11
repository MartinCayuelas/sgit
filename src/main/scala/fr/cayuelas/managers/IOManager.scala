package fr.cayuelas.managers


import java.io.{BufferedWriter, File, FileWriter}

import fr.cayuelas.managers.StageManager.currentStagePath

object IOManager {


  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param path of file in which we want to write
   * @param content to write in the file
   * @param append could be True or False if we want to rewrite the content or append it
   */
  def writeInFile(path: String, content: String, append: Boolean): Unit = {
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file, append))
    bw.write(content)
    bw.close()
  }

  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param pathToFile in which we want to read the content
   * @return the content of a file in a String (Could be an empty string if the file is empty)
   */

  def readInFile(pathToFile: String): String ={
    val source = scala.io.Source.fromFile(pathToFile)
    val content = try source.mkString finally source.close()
    content
  }

  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param pathToFile in which we want to read the content
   * @return the content of a file in a List
   */
  def readInFileAsLine(pathToFile: String): List[String] = {
    val file = new File(currentStagePath)
    val source = scala.io.Source.fromFile(file)
    val lines = source.getLines.toList
    source.close()
    lines
  }

  /*
  Println part
   */

  def noArgumentsExpected() : Unit = {
    println("No argument(s) expected.")
  }
  def notSgitReposiroty(): Unit = {
    println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
  }
  def notExistingCommand(): Unit = {
    println("This command doesn't exists")
  }

}
