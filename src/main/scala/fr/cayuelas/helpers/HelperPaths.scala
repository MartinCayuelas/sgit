package fr.cayuelas.helpers

import java.io.File

import scala.annotation.tailrec

object HelperPaths {


  def sgitRepositoryName: String = ".sgit"
  /**
   * Returns the path of the sgit repository if it exists else returns an empty String
   */
  def sgitPath: String = {
    val workingDirectory = new File(System.getProperty("user.dir"))
    val path = getSgitPath(workingDirectory)
    if (path.isDefined) path.get + File.separator
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
      if (new File(directory.getAbsolutePath + File.separator + sgitRepositoryName).exists()) Some(directory.getAbsolutePath)
      else getSgitPath(directory.getParentFile)
    }

  }

  /**
   *Method taht calculates the relative apth of a file
   * @param absoluteFilePath  the absolute path of a file in String
   * @return the relative path relative to the .sgit path folder
   */
  def getRelativePathOfFile(absoluteFilePath: String): String = absoluteFilePath.toSeq.diff(sgitPath.toSeq).unwrap

  //Directories absolute paths
  def objectsPath: String = sgitPath + sgitRepositoryName + File.separator + "objects"
  def tagsPath: String = sgitPath + sgitRepositoryName + File.separator + "refs" + File.separator + "tags"
  def branchesPath: String = sgitPath + sgitRepositoryName + File.separator + "refs" + File.separator + "heads"
  def logsPath: String = sgitPath + sgitRepositoryName + File.separator + "logs"
  def stagePath: String = sgitPath + sgitRepositoryName + File.separator + "stages"

  //files absolute path
  def headFile: String = sgitPath + sgitRepositoryName + File.separator + "HEAD"


  /**
   *
   * @param sha1
   * @return
   */
  def getFolderAndFileWithSha1(sha1: String): (String,String)= {
    val folder = sha1.substring(0,2)
    val file = sha1.substring(2,sha1.length)
    (folder,file)
  }

}
