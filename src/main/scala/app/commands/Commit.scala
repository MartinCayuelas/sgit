package app.commands

import java.io.File

import app.filesManager.FilesIO
import app.objects.{Wrapper, Tree}

import scala.annotation.tailrec
import better.files.{File => BFile}

object Commit {

  def commit(): Unit = {
    //val stage = retrieveStageStatus()
    val stage = retrieveStageStatus()

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
    //deeper.map(x => println(x))
    deeper.map(element => tree.set_contentTree(tree.addElement(element)))
    val hash = tree.createTreeId(tree.get_contentTree())
    tree.set_idTree(hash)
    tree.saveTreeFile(tree.get_idTree(), tree.get_contentTree())
    tree.get_idTree()
  }

  //Returns a list containing the path to a file that has been converted to a Blob (because it's in the STAGE) and its Hash
  //OUTPUT is something like this:
  //(src/main/scala/objects,a7dbb76b0406d104b116766a40f2e80a79f40a0349533017253d52ea750d9144)
  //(src/main/scala/utils,29ee69c28399de6f830f3f0f55140ad97c211fc851240901f9e030aaaf2e13a0)
  def retrieveStageStatus(): List[Wrapper]= {
    //Retrieve useful data
    val files = FilesIO.readStage()
    val base_dir = System.getProperty("user.dir")

    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))

    //Cleaning from the filenames
    val paths = stage_content.map(x => BFile(base_dir).relativize(BFile(x(2)).parent).toString).toList

    val hashs = stage_content.map(x =>x(1)).toList
    val blob = List.fill(paths.size)("blob")
    //Merging the result
    val listTobeReturned=((paths,hashs,blob).zipped.toList)
    listTobeReturned.map(elem => Wrapper(elem._1,elem._2,elem._3))
  }

  def getDeeperDirectory(l: List[Wrapper]): (List[Wrapper], List[Wrapper], Option[String]) = {
    var max = 0
    var pathForMax = ""

    l.map(line => if (line.get_path().split("/").size >= max) {
      max = line.get_path().split("/").size
      pathForMax = line.get_path()
    })

    val rest = l.filter(x => !(x.get_path().equals(pathForMax)))
    val deepest = l.filter(x => x.get_path()equals(pathForMax))

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
      pathSplit.map(x => if(x != lastValue){
        if(first_dir){
          parentPath = x
          first_dir = false
        } else {
          parentPath = parentPath + File.separator + x
        }
      })
      Some(parentPath)
    }
  }
}
