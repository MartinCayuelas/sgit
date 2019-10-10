package fr.cayuelas.filesManager

import java.io.File

object FilesManager {

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def getListOfContentInDirectory(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory && !d.getName.equals(".sgit")) {
      d.listFiles.toList
    } else {
      List[File]()
    }
  }
}
