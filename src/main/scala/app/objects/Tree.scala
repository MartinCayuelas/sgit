package app.objects

import java.io.File
import java.nio.file.Paths

import app.filesManager.FilesIO.writeTree
import app.helpers.HelpersApp

case class Tree(var items: List[Wrapper] = List.empty, var id: String = "") {

  def addElement(elem: Wrapper): List[Wrapper] = {
    elem :: this.get_contentTree()
  }

  def get_contentTree(): List[Wrapper] = {
    this.items
  }

  def set_contentTree (items: List[Wrapper]): Unit = {
    this.items = items
  }

  def get_idTree(): String = {
    this.id
  }

  def set_idTree (id: String): Unit = {
    this.id = id
  }


  def saveTreeFile(idSha1: String, contentTree: List[Wrapper]): Unit = {
    val path = Paths.get(".sgit/objects/trees").toAbsolutePath.toString
    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    new File(path + File.separator +  folder).mkdir()
    new File(path + File.separator +  folder + File.separator + nameFile).createNewFile()

    writeTree(path + File.separator +  folder + File.separator + nameFile,treeContent(contentTree))

  }

  def createTreeId(items: List[Wrapper]): String = {
    val content = treeContent(items)
    HelpersApp.convertToSha1(content)
  }

  def treeContent(items: List[Wrapper]): String = {
    var acc = ""
    items.map(x => acc = acc + x.get_TypeE() + " " + x.get_hash() +" "+ x.get_path()+ "\n")
    acc
  }

}

object Tree {
  def createTree(content: List[Wrapper]): String = {
    val tree = new Tree()
    content.map(element => tree.set_contentTree(tree.addElement(element)))
    val hash = tree.createTreeId(tree.get_contentTree())
    tree.set_idTree(hash)
    tree.saveTreeFile(tree.get_idTree(), tree.get_contentTree())
    tree.get_idTree()
  }

  def createTreeGhost(nonRootFiles: List[Wrapper], rootFiles: List[Wrapper]): String = {
    val tree = new Tree()

    if(nonRootFiles.length > 0) nonRootFiles.map(element => tree.set_contentTree(tree.addElement(element)))
    if(rootFiles.length > 0) rootFiles.map(element => tree.set_contentTree(tree.addElement(element)))

    val hash = tree.createTreeId(tree.get_contentTree())
    tree.set_idTree(hash)
    tree.saveTreeFile(tree.get_idTree(), tree.get_contentTree())
    tree.get_idTree()
  }
}


