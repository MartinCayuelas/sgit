package app.objects

import java.io.File
import java.nio.file.Paths

import app.filesManager.FilesIO.writeTree
import app.helpers.HelpersApp

case class Tree() {
  private var idTree = "new"
  private var contentTree = List("")

  def set_idTree(id: String): Unit = {
    idTree = id
  }
  def set_contentTree(list: List[String]):Unit = {
    contentTree = list
  }

  def get_idTree(): String = {
    return idTree
  }

  def get_contentTree(): List[String] = {
    return contentTree
  }

  def addContentTree(newContent: String): List[String] = {
    newContent::get_contentTree()
  }

  def createId(t: Tree): String = {
    val idToConvert = t.get_contentTree().reduce(_.concat(_))
    val idSha1 = HelpersApp.convertToSha1(idToConvert)
    return  idSha1
  }

  def addTree(idSha1: String, contentTree: List[String]): Unit = {
    val path = Paths.get(".sgit/objects/trees").toAbsolutePath.toString
    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    new File(path + File.separator +  folder).mkdir()
    new File(path + File.separator +  folder + File.separator + nameFile).createNewFile()
    val contentToWrite = contentTree.reduce(_.concat(_))

    writeTree(path + File.separator +  folder + File.separator + nameFile,contentToWrite)

  }

}
