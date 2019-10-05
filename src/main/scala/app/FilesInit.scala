package app

import java.io.File
import java.nio.file.{Files, Paths}

case class FilesInit() {

  def initSgitRepository() : Unit = {
    val listFolders = List("objects", "branches", "config", "refs/heads", "refs/tags")
    val listFiles = List("HEAD","STAGE_AREA")
    val path = Paths.get("").toAbsolutePath().toString()
    val sgitPath = path + File.separator +  ".sgit"
    val sgitRepository = new File(".sgit")

    if(Files.notExists(Paths.get(".sgit"))){
      sgitRepository.mkdir()
      listFolders.map( folder => new File(sgitPath + File.separator +  folder).mkdir())
      listFiles.map(file => new File(sgitPath + File.separator + file).createNewFile())
      println(s"Empty Git repository initialized in ${path}/.sgit/")
    } else {
      println(s"Sgit repository already exists.")
    }
  }

}
