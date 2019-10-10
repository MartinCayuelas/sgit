package app.objects

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import app.filesManager.FilesIO.writeTree
import app.filesManager.Stage.writeInStage
import app.helpers.HelpersApp

case class Tree(var contentTree: List[Wrapper] = List.empty, var id: String = "") {

  def addElement(elem: Wrapper): List[Wrapper] = {
    elem :: this.contentTree
  }


  def set_contentTree (contentTree: List[Wrapper]): Unit = {
    this.contentTree = contentTree
  }

/*
  def set_idTree (id: String): Unit = {
    this.id = id
  }*/

  def createTreeId(contentTree: List[Wrapper]): String = {
    val content = treeContent(contentTree)
    HelpersApp.convertToSha1(content)
  }

  def treeContent(contentTree: List[Wrapper]): String = {
    var acc = ""
    contentTree.map(x => acc = acc + x.typeE+ " " + x.hash+" "+ x.path+ "\n")
    acc
  }


  def saveTreeFile(idSha1: String, contentTree: List[Wrapper]): Unit = {
    val path = Paths.get(".sgit/objects/trees").toAbsolutePath.toString
    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    new File(path + File.separator +  folder).mkdir()
    new File(path + File.separator +  folder + File.separator + nameFile).createNewFile()
    val file = Paths.get(path + File.separator +  folder + File.separator + nameFile).toFile
    deleteLineInTreeFileIfFileAlreadyExists(file.getPath,folder,nameFile)
    writeTree(path + File.separator +  folder + File.separator + nameFile,treeContent(contentTree))

  }


  def deleteLineInTreeFileIfFileAlreadyExists(pathLine: String, folder: String, nameFile: String): Unit = {
    val path = Paths.get(".sgit".concat(File.separator).concat("objects").concat(File.separator).concat("trees").concat(File.separator).concat(folder).concat(File.separator).concat(nameFile)).toAbsolutePath.toString
    val file = new File(path)
    val source = scala.io.Source.fromFile(file)
    val lines = source.getLines.toList
    source.close()

    //Clean the file
    val writer = new PrintWriter(path)
    writer.print("")
    writer.close()

    val treeContent = lines.map(x => x.split(" "))
    val treeContentFiltered =  treeContent.filter(x => !x(2).equals(pathLine))
    val contentTree: List[String] = treeContentFiltered.map(x => x(0)+" "+x(1)+" "+x(2)+"\n")

    contentTree.map(line => writeInStage(line))
  }

}

object Tree {
  def createTree(content: List[Wrapper]): String = {
    val tree = new Tree()
    content.map(element => tree.set_contentTree(tree.addElement(element)))
    val hash = tree.createTreeId(tree.contentTree)
    val treeCopy = tree.copy(id = hash)
    treeCopy.saveTreeFile(treeCopy.id, treeCopy.contentTree)
    treeCopy.id
  }

  def createTreeGhost(nonRootFiles: List[Wrapper], rootFiles: List[Wrapper]): String = {
    val tree = new Tree()

    if(nonRootFiles.length > 0) nonRootFiles.map(element => tree.set_contentTree(tree.addElement(element)))
    if(rootFiles.length > 0) rootFiles.map(element => tree.set_contentTree(tree.addElement(element)))

    val hash = tree.createTreeId(tree.contentTree)
    //tree.set_idTree(hash)
    val treeCopy = tree.copy(id = hash)
    treeCopy.saveTreeFile(treeCopy.id, treeCopy.contentTree)
    treeCopy.id
  }
}


