package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.commands.Init_cmd
import fr.cayuelas.managers.{FilesManager, IOManager}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

class HelperPathsSpec   extends FlatSpec with BeforeAndAfterEach {

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

  "The helperPath" should "return the good path for .sgit repository when creating the .sgit folder" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    //then
    assert(sgitPath.equals(System.getProperty("user.dir")+File.separator))
  }

  it should "return the good path for .sgit/objects repository " in {
    //Given
    val objectsPath = HelperPaths.objectsPath
    //then
    assert(objectsPath.equals(System.getProperty("user.dir")+File.separator+".sgit"+File.separator+"objects"))
  }

  it should "return the good path for .sgit/refs/heads repository " in {
    //Given
    val branchPath = HelperPaths.branchesPath
    //then
    assert(branchPath.equals(System.getProperty("user.dir")+File.separator+".sgit"+File.separator+"refs"+ File.separator + "heads"))
  }

  it should "return the good path for .sgit/logs repository " in {
    //Given
    val logPath = HelperPaths.logsPath
    //then
    assert(logPath.equals(System.getProperty("user.dir")+File.separator+".sgit"+File.separator+"logs"))
  }



}
