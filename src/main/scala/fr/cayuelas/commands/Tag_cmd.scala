package fr.cayuelas.commands


import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.filesManager.FilesManager

object Tag_cmd {

  val tagsPath : String = Paths.get(".sgit/refs/tags").toString
  /*
  TAG---------------------
   */

  def tag(args: Array[String]): Unit = {
    if (args.length == 2)  createTag(args(1))
    else  println(s"Number of arguments not supported for the command '${args(0)}'.")
  }
  /*
  Tags------------------------
   */
  def createTag(nameTag: String): Unit = {
    if(Files.notExists(Paths.get(s".sgit/refs/tags/${nameTag}"))){
      new File(tagsPath + File.separator +  nameTag).createNewFile()
    } else println(s"Fatal: a branch named ${nameTag} is exits already")
  }
  def displayAllTags(): Unit = {
    val listOfTags = FilesManager.getListOfFiles(tagsPath)
    listOfTags.map(b => println(s"  ${b.getName} (tag)"))
  }

}
