package fr.cayuelas.helpers

import java.io.File
import java.nio.file.{Files, Paths}

import fr.cayuelas.managers.{FilesManager, IoManager}

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
        IoManager.writeInFile(path,HelperCommit.getLastCommitInRefs(),append = false)
      } else IoManager.printFatalCreation("tag",nameTag)
    }else IoManager.printErrorNoCommitExisting()
  }

  /**
   * Function which displays all the tags contained in sgit/refs/tags
   */
  def displayAllTags(): Unit = FilesManager.getListOfFiles(HelperPaths.tagsPath).map(b => IoManager.printTag(b.getName))

  /**
   * Checks if the givn string is a tag or not
   * @param nameTag
   * @return true if the given string correspond to a tag, else false
   */
  def isATag(nameTag: String): Boolean = FilesManager.getListOfFiles(HelperPaths.tagsPath).exists(t => t.getName == nameTag)

}
