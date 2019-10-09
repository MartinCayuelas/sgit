package app.commands

import java.io.File

import app.filesManager.FilesIO
import app.objects.{Tree, Wrapper}

import scala.annotation.tailrec

object Commit {

  def commit(): Unit = {
    //val stage = retrieveStageStatus()
    val stage = FilesIO.retrieveStageStatus()
    val blobsRoot = FilesIO.retrieveStageRootBlobs()

    val stageWithNonRootBlobs = stage.filter(x => !root_blobs.contains(x))


    val resTrees = addTrees(stage, None)
    /*
    Creating the tree for commit
     */
  //  val treeCommit = new Tree()
   // treeCommit.set_contentTree(resTrees)

    /*
    Commit
     */

    /*
    TO DO COMMIT
     */
  }

  @tailrec
  def addTrees(l: List[Wrapper], hashFinal: Option[List[String]]): List[String] = {
    if(l.size == 0){
      hashFinal.get
    } else {
      val (deeper, rest, parent) = getDeeperDirectory(l)
      val hash = createTree(deeper)
      if(parent.isEmpty) {
        if (hashFinal.isEmpty){
          addTrees(rest, Some(List(hash)))
        } else {
          addTrees(rest, Some(hash :: hashFinal.get))
        }
      } else {
        addTrees(Wrapper(parent.get, hash, "tree") :: rest, hashFinal)
      }
    }
  }

  def createTree(deeper: List[Wrapper]): String = {
    val tree = new Tree()
    deeper.map(element => tree.set_contentTree(tree.addElement(element)))
    val hash = tree.createTreeId(tree.get_contentTree())
    tree.set_idTree(hash)
    tree.saveTreeFile(tree.get_idTree(), tree.get_contentTree())
    tree.get_idTree()
  }



  def getDeeperDirectory(l: List[Wrapper]): (List[Wrapper], List[Wrapper], Option[String]) = {
    var max = 0
    var pathForMax = ""

    l.map(line => if (line.get_path().split("/").size >= max) {
      max = line.get_path().split("/").size
      pathForMax = line.get_path()
    })

    val rest = l.filter(x => !(x.get_path().equals(pathForMax)))
    val deepest = l.filter(x => x.get_path().equals(pathForMax))

    val parentPath = getParentPath(pathForMax)

    (deepest, rest, parentPath)
  }

  def getParentPath(path: String): Option[String] = {
    val pathSplit = path.split("/")
    if(pathSplit.length <= 1){
      None
    } else {
      var parentPath = ""
      var first_dir = true
      val lastValue = pathSplit.last
      var index =0
      pathSplit.map(x => if(index < pathSplit.length-1){
        if(first_dir){
          parentPath = x
          first_dir = false
        } else {
          parentPath = parentPath + File.separator + x
        }
        index = index+1
      })
      Some(parentPath)
    }
  }
}
