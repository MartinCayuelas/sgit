package fr.cayuelas.commands

import java.io.File
import java.nio.file.Paths

import fr.cayuelas.filesManager.FilesManager
import fr.cayuelas.objects.Blob

object Add_cmd {
  /*
ADD -----------
 */
  def add(args: Array[String]) : Unit = {
    if (args.length == 1) println("You need to specify a folder or a file")
   /* else if(args.length == 2 && args(1).equals(".")) {
      val file =  Paths.get(".sgit").toFile
      addRoutine(file)
    }*/
    else {
      args.filter(_ != "add").map(arg =>{
        val file =  Paths.get(arg).toFile
        addRoutine(file)
      })
    }
  }

  def addRoutine(f: File) : Unit = {
      if(f.isFile) Blob.createBlob(f)
      else if (f.isDirectory){
        recursionAddBlob(f)
      }
    else println(s"fatal: the path ${f.getName} does not correspond to any file")
  }

  def recursionAddBlob(f: File): Unit = {
    val path = f.getPath
    val listOfFiles = FilesManager.getListOfContentInDirectory(path)
    listOfFiles.map(elem =>{
      if(elem.isDirectory) recursionAddBlob(elem)
      else Blob.createBlob(elem)
    })
  }

}
