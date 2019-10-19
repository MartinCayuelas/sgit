package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.managers.{FilesManager, IoManager}
import fr.cayuelas.objects.Blob

object HelperAdd {

  /**
   *Function that transform each param in File and call a function which process the creating blobs processus
   * @param f : File which will be precocessed
   * If f is a File, f is transformed in Blob else if it's a directory we apply a recursive call else the file doesn't exists
   */
  def addRoutine(f: File) : Unit = {
    if(f.isFile) Blob.createBlob(f)
    else if (f.isDirectory)recursionAddBlob(f)
    else IoManager.printFatalAdd(f)
  }

  /**
   *Recursive function that call
   * @param f : File which will be precocessed (it's a Directory)
   * For each File in the folder, if it's a file then creating blob else recusive call to continue the process et get all the files in subdirectories
   */
  def recursionAddBlob(f: File): Unit = {
    val path = f.getPath
    val listOfFiles = FilesManager.getListOfContentInDirectory(path)
    listOfFiles.map(elem =>{
      if(elem.isDirectory) recursionAddBlob(elem)
      else Blob.createBlob(elem)
    })
  }
}
