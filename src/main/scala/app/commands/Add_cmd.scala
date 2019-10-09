package app.commands

import java.io.File
import java.nio.file.Paths

import app.filesManager.FilesManager
import app.objects.Blob

object Add_cmd {
  /*
ADD -----------
 */
  def add(args: Array[String]) : Unit = {
    if (args.length == 1) println("You need to specify a folder or a file")
    else if(args.length == 2 && args(1).equals(".")) {
      val path = Paths.get("").toFile
      addRoutine(path)
    }
    else {
      args.filter(_ != "add").map(arg =>{
        val file =  Paths.get(arg).toFile
        addRoutine(file)
      })
    }
  }

  def addRoutine(f: File) : Unit = {
    if(f.exists()){
      if(f.isFile){
        Blob.createBlob(f)
      }else{
        // recursionFiles(f, new Tree())
        recursionFilesBlob(f)
      }
    }else println(s"fatal: the path ${f.getName} does not correspond to any file")

  }

  def recursionFilesBlob(f: File): Unit = {
    val path = f.getPath
    val listOfFiles = FilesManager.getListOfAll(path)
    listOfFiles.map(elem =>{
      if(elem.isDirectory){
        recursionFilesBlob(elem)
      }else{

        Blob.createBlob(elem)
      }
    })



  }


}
