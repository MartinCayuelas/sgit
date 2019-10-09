package app.commands

import java.io.File

import app.filesManager.Stage
import app.objects.{Commit, Tree, Wrapper}

import scala.annotation.tailrec


object Commit_cmd {

  def commit(): Unit = {
    if(Stage.stageEmpty()) println("Nothing to commit")
    else {
      val stage: List[Wrapper] = Stage.retrieveStageStatus()
      val blobsRoot: List[Wrapper] = Stage.retrieveStageRootBlobs()

      val resTrees = addTrees(stage, None)
      /*
      Creating the tree for commit
       */
      val hashFinalGhostTree = createTreeGhost(blobsRoot,resTrees)

      //Creation and process about a Commit object (class)
      Commit.commit(hashFinalGhostTree)
    }


  }

  @tailrec
  def addTrees(l: List[Wrapper], hashFinal: Option[List[Wrapper]]): List[Wrapper] = {
    if(l.size == 0){
      hashFinal.get
    } else {
      val (deeper, rest, parent) = getDeeperDirectory(l)
      val hash = createTree(deeper)
      if(parent.isEmpty) {
        if (hashFinal.isEmpty){
          addTrees(rest, Some(List(Wrapper(deeper(0).get_path(),hash,"Tree"))))
        } else {
          addTrees(rest, Some(Wrapper(deeper(0).get_path(),hash,"Tree") :: hashFinal.get))
        }
      } else {
        addTrees(Wrapper(parent.get, hash, "Tree") :: rest, hashFinal)
      }
    }
  }

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
