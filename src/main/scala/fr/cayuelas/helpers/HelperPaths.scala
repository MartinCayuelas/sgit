package fr.cayuelas.helpers

import java.io.File

import scala.annotation.tailrec

object HelperPaths {


  val SgitRepositoryName = ".sgit"
  /**
   * Returns the path of the sgit repository if it exists else returns an empty String
   */
  val sgitPath: String = {
    val workingDirectory = new File(System.getProperty("user.dir"))
    val path = getSgitPath(workingDirectory)
    if (!(path.isEmpty)) path.get + File.separator
    else new String("")
  }

  /**
   * Gives the path of the sgit repository if it exists in the directory given in parameter or in any of its parent directories
   *
   * @param directory : Directory where beginning to search
   * @return The path of .sgit repository if exists, otherwise None
   */
  @tailrec
  def getSgitPath(directory: File): Option[String] = {

    if (directory == null || !directory.isDirectory) None
    else {
      if (new File(directory.getAbsolutePath + File.separator + SgitRepositoryName).exists()) Some(directory.getAbsolutePath)
      else getSgitPath(directory.getParentFile)
    }

  }

  def getRelativePathOfFile(absoluteFilePath: String): String = absoluteFilePath.toSeq.diff(sgitPath.toSeq).unwrap

  //Directories absolute paths
  val objectsPath: String = sgitPath + SgitRepositoryName + File.separator + "objects"
  val tagsPath: String = sgitPath + SgitRepositoryName + File.separator + "refs" + File.separator + "tags"
  val branchesPath: String = sgitPath + SgitRepositoryName + File.separator + "refs" + File.separator + "heads"
  val logsPath: String = sgitPath + SgitRepositoryName + File.separator + "logs"
  val stagePath: String = sgitPath + SgitRepositoryName + File.separator + "stages"

  //files absolute path
  val headFile: String = sgitPath + SgitRepositoryName + File.separator + "HEAD"

}
