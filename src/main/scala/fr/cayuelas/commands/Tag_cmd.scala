package fr.cayuelas.commands


import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.helpers.HelperPaths
import fr.cayuelas.managers.FilesManager

object Tag_cmd {

  val tagsPath : String = HelperPaths.tagsPath
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
    val path = tagsPath + File.separator + nameTag
    if(Files.notExists(Paths.get(path))){
      FilesManager.createNewFile(path)
    } else println(s"Fatal: a branch named ${nameTag} is exits already")
  }
  def displayAllTags(): Unit = {
    val listOfTags = FilesManager.getListOfFiles(tagsPath)
    listOfTags.map(b => println(s"  ${b.getName} (tag)"))
  }

}
