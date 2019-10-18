package fr.cayuelas.commands

import java.io.File

import fr.cayuelas.managers.{FilesManager, IOManager}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}


class CommandStatusSpec  extends FlatSpec with BeforeAndAfterEach {

  //init an .sgit repo and testFolder repo before each test
  override def beforeEach(): Unit = {
    Init_cmd.init(System.getProperty("user.dir"))
    new File( "testFolder").mkdir()

    FilesManager.createNewFile("testFolder" + File.separator + "hello")
    FilesManager.createNewFile("testFolder" + File.separator + "world")
    IOManager.writeInFile("testFolder" + File.separator + "hello","hello",false)
    IOManager.writeInFile("testFolder" + File.separator + "world","world",false)
  }

  //delete all files created in the .sgit directory after each test
  override def afterEach(): Unit = {
    val sgit = new File(".sgit")
    if (sgit.exists()) {
      delete(new File(".sgit"))
    }
    val testFolder = new File("testFolder")
    if (testFolder.exists()) {
      delete(new File("testFolder"))
    }

  }

  def delete(file: File): Unit = {
    if (file.isDirectory) {
      file.listFiles().foreach(delete)
    }
    file.delete()
  }




}
