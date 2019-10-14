package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.objects.{Tree, Wrapper}

import scala.annotation.tailrec

object HelperCommit {


  /**
   * FMethod that creates all the trees for the commit with the non root files
   * @param l : list to use
   * @param WrapperWithHashFinal : Potential final hash for the last tree in a list to have it every step
   * @return a list containing the final hash and the final path
   */

  @tailrec
  def createAllTrees(l: List[Wrapper], WrapperWithHashFinal: Option[List[Wrapper]]): List[Wrapper] = {
    if (l.isEmpty) WrapperWithHashFinal.get
    else {
      val (deeper, rest, father, oldPath) = getDeeperDirectory(l)
      val hash: String = Tree.createTree(Some(deeper), None)
      if (father.isEmpty) {
        if (WrapperWithHashFinal.isEmpty) createAllTrees(rest, Some(List(Wrapper(deeper(0).path, hash, "Tree", oldPath.get))))
        else createAllTrees(rest, Some(Wrapper(deeper(0).path, hash, "Tree", oldPath.get) :: WrapperWithHashFinal.get))
      }
      else createAllTrees(Wrapper(father.get, hash, "Tree", oldPath.get) :: rest, WrapperWithHashFinal)
    }
  }

  /**
   * Method that retrieves the deepest directory
   * @param l : list on which we do action
   * @return a tuple4 containing the dispest path, the rest of the orthers paths, the parent path of the dispest direcoty and itself path
   */
  def getDeeperDirectory(l: List[Wrapper]): (List[Wrapper], List[Wrapper], Option[String], Option[String]) = {

    val pathMax = getPathMax(l,"",0)
    val rest: List[Wrapper] = l.filter(x => !x.path.equals(pathMax))
    val deepest: List[Wrapper]  = l.filter(x => x.path.equals(pathMax))
    val (fatherPath, oldPath) = getParentPath(pathMax)

    (deepest, rest, fatherPath, oldPath)
  }

  /**
   * Method that retrieves the path max in a given list
   * @param l : list that we look at
   * @param pathMax : longest path
   * @param max : Accumulator
   * @return the path max of e given list
   */
  @tailrec
  def getPathMax(l: List[Wrapper], pathMax: String, max: Int): String = {
    if(l.isEmpty) pathMax
    else {
      if (l.head.path.split("/").length >= max) getPathMax(l.tail,l.head.path,l.head.path.split("/").length)
      else getPathMax(l.tail,pathMax,max)
    }
  }

  /**
   * Method the retrieves the parent of a givent path
   * @param path : the path to be transformed
   * @return the parent path of the given path and itself
   */
  def getParentPath(path: String): (Option[String], Option[String]) = {
    val pathSplited: List[String] = path.split("/").toList
    if (pathSplited.length <= 1) (None, Some(path))
    else {
      val parentPath = pathSplited.init.map(x => x+File.separator).mkString.dropRight(1)
      (Some(parentPath), Some(path))
    }
  }

}
