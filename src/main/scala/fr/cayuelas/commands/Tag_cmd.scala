package fr.cayuelas.commands


import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.helpers.{HelperCommit, HelperPaths}
import fr.cayuelas.managers.{FilesManager, IOManager}

object Tag_cmd {

  /**
   * Main function for tags that dispatch actions given a paramater
   * @param args : should contains the name of the new tag
   */
  def tag(args: Array[String]): Unit = {
    if (args.length == 2)  createTag(args(1))
    else  IOManager.numberOfArgumentNotSupported(args(0))
  }

  /**
   * Function taht creates a nex branch in .sgit/refs/tags
   * @param nameTag : name of the new tag
   */
  def createTag(nameTag: String): Unit = {
    val path = HelperPaths.tagsPath + File.separator + nameTag
    if(Files.notExists(Paths.get(path))){
      FilesManager.createNewFile(path)
      IOManager.writeInFile(path,HelperCommit.getLastCommitInRefs(),false)
    } else IOManager.printFatalCreation("tag",nameTag)
  }

  /**
   * Function which displays all the tags contained in sgit/refs/tags
   */
  def displayAllTags(): Unit = {
    val listOfTags = FilesManager.getListOfFiles(HelperPaths.tagsPath)
    listOfTags.map(b => println(s"  ${b.getName} (tag)"))
  }

}
