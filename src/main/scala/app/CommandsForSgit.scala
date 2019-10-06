package app

import java.io.File
import java.nio.file.Paths

object CommandsForSgit {


  /*
  INIT -----------
   */
  def init() : Unit = {
    FilesIO.initSgitRepository()
  }
  /*
  --------------
   */


  /*
 ADD -----------
  */
  def add(args: Array[String]) : Unit = {
    if (args.length == 1) println("You need to specify a folder or a file")
    else if(args.length == 2 && args(1).equals(".")) {
      val path = Paths.get("").toFile()
      addRoutine(path)
    }
    else {
      args.filter(_ != "add").map(arg =>{
        val file =  Paths.get(arg).toFile
        println(file.getName)
        addRoutine(file)
      })
    }


  }

  def addRoutine(f: File) : Unit = {
    if(f.isFile){

      FilesIO.createBlob(f)
    }else{
      recursionFiles(f, new Tree())
    }
  }


  def recursionFiles(f: File, currentTree: Tree): Unit = {
    val path = f.getCanonicalPath
    val listOfFiles = FilesManager.getListOfAll(path)

    listOfFiles.map(elem =>{
      if(elem.isDirectory){
        val newTree = new Tree()

        recursionFiles(elem, newTree)

        //AddTree
        val newContent = s"Tree ${newTree.get_idTree()} ${elem.getName}\n"
        currentTree.set_contentTree(currentTree.addContentTree(newContent))

      }else{
        val blob = FilesIO.createBlob(elem)
        currentTree.set_contentTree(currentTree.addContentTree(blob))

      }
    })

    // Tree part
    currentTree.createId()
    FilesIO.addTree(currentTree.get_idTree(),currentTree.get_contentTree())

  }
  /*
  --------------
   */

}