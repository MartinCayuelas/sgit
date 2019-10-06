package app

import java.io.File
import java.nio.file.{Files, Paths}

object FileIO {
  /*
   * initSgitRepository:
   * Method that create all the necessary folders for sgit
   * Inform the user with a warning message if sommething went wrong (i.e .sgit already exists) or Inform the user that all is successful
   */

  def initSgitRepository() : Unit = {
    val listFolders = List("objects", "objects/blobs", "objects/trees","objects/commits ", "refs/heads", "refs/tags","logs")
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


  def createBlob(): String = {

  }

  def addBlob(): Unit {

  }

  /*

   */

  def writeHead(): Unit = {

  }
}
