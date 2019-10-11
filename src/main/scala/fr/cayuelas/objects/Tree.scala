package fr.cayuelas.objects


import java.io.{File, PrintWriter}
import java.nio.file.Paths

import fr.cayuelas.managers.{FilesManager, IOManager}
import fr.cayuelas.helpers.{HelperPaths, HelperSha1}

case class Tree(var contentTree: List[Wrapper] = List.empty, var id: String = "") {

  val treesPath: String = HelperPaths.objectsPath + File.separator + "trees"

  def addElement(elem: Wrapper): List[Wrapper] = {
    elem :: this.contentTree
  }

  def createTreeId(contentTree: List[Wrapper]): String = {
    val content = treeContent(contentTree)
    HelperSha1.convertToSha1(content)
  }

  def treeContent(contentTree: List[Wrapper]): String = {
    var acc = ""
    contentTree.map(x => acc = acc + x.typeElement+ " " + x.hash+" "+ x.path+ "\n")
    acc
  }


  def saveTreeInObjects(idSha1: String, contentTree: List[Wrapper]): Unit = {

    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    val pathFile = treesPath + File.separator +  folder + File.separator + nameFile

    FilesManager.createNewFolder(treesPath + File.separator +  folder)
    FilesManager.createNewFile(pathFile)

    val file = Paths.get(pathFile).toFile
    clearTreefile(pathFile,folder,nameFile)
    IOManager.writeInFile(pathFile,treeContent(contentTree),true)//WriteInTree

  }

  def clearTreefile(pathLine: String, folder: String, nameFile: String): Unit = {
    val path = treesPath + File.separator + folder + File.separator + nameFile
    //Clean the file
    val writer = new PrintWriter(path)
    writer.print("")
    writer.close()
  }

}

object Tree {


  def createTree(nonRootFiles: Option[List[Wrapper]], rootFiles: Option[List[Wrapper]]): String = {
    val tree = new Tree()
    var contentTreeH: List[Wrapper] = List().empty

    if (nonRootFiles.isDefined) nonRootFiles.get.map(element => {
      val nTree: Tree = tree.copy(contentTree = tree.addElement(element))
      contentTreeH = nTree.contentTree ++ contentTreeH
    })

    if (rootFiles.isDefined) rootFiles.get.map(element => {
      val nTree: Tree = tree.copy(contentTree = tree.addElement(element))
      contentTreeH = nTree.contentTree ++ contentTreeH
    })

    val hash = tree.createTreeId(contentTreeH)

    val treeCopy = tree.copy(id = hash, contentTree = contentTreeH)
    treeCopy.saveTreeInObjects(treeCopy.id, treeCopy.contentTree)
    treeCopy.id
  }
}


