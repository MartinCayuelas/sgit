package fr.cayuelas.helpers


import java.io.File
import java.security.MessageDigest

import scala.annotation.tailrec

object HelpersApp {
  def convertToSha1(string : String): String ={
    MessageDigest.getInstance("SHA-1").digest(string.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }

  //base Repository name sgit
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
}
