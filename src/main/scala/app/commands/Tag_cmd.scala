package app.commands

import java.io.File
import java.nio.file.{Files, Paths}

import app.filesManager.FilesManager

object Tag_cmd {
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
      val path =Paths.get(".sgit/refs/tags").toAbsolutePath().toString()
      new File(path + File.separator +  nameTag).createNewFile()
    }else {
      println(s"Fatal: a branch named ${nameTag} is exits already")
    }
  }
  def displayAllTags(): Unit = {
    val listOfTags = FilesManager.getListOfFiles(Paths.get(".sgit/refs/tags").toAbsolutePath.toString)
    listOfTags.map(b =>{
      println(s"  ${b.getName} (tag)")
    })
  }

}
