package app.filesManager

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

  def getListOfAll(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory && !d.getName.equals(".git")&& !d.getName.equals(".sgit")&& !d.getName.equals(".idea")) {
      d.listFiles.toList
    } else {
      List[File]()
    }
  }



}