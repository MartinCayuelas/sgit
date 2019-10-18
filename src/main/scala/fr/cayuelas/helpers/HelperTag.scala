package fr.cayuelas.helpers

import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.managers.{FilesManager, IOManager}

object HelperTag {
  /**
   * Function taht creates a nex branch in .sgit/refs/tags
   * @param nameTag : name of the new tag
   */
  def createTag(nameTag: String): Unit = {
    if (HelperCommit.existsCommit){
      val path = HelperPaths.tagsPath + File.separator + nameTag
      if(Files.notExists(Paths.get(path))){
        FilesManager.createNewFile(path)
        IOManager.writeInFile(path,HelperCommit.getLastCommitInRefs(),false)
      } else IOManager.printFatalCreation("tag",nameTag)
    }else IOManager.printErrorNoCommitExisting()
  }

  /**
   * Function which displays all the tags contained in sgit/refs/tags
   */
  def displayAllTags(): Unit = {
    val listOfTags = FilesManager.getListOfFiles(HelperPaths.tagsPath)
    listOfTags.map(b => println(s"  ${b.getName} (tag)"))
  }

}
