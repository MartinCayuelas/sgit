package fr.cayuelas.commands


import java.io.File

import fr.cayuelas.managers.IOManager
import org.scalatest.{BeforeAndAfterEach, FlatSpec}


class CommandInitSpec extends FlatSpec with BeforeAndAfterEach {



  //init an sgit repo before each test
  override def beforeEach(): Unit = {
    Init_cmd.init(System.getProperty("user.dir"))
  }

  //delete all files created in the .sgit directory after each test
  override def afterEach(): Unit = {
    val sgit = new File(".sgit")
    if (sgit.exists()) {
      delete(new File(".sgit"))
    }
    def delete(file: File): Unit = {
      if (file.isDirectory) {
        file.listFiles().foreach(delete)
      }
      file.delete()
    }
  }

  "The init command" should "create the .sgit directory with the good one structure" in {
    assert(new File(".sgit").exists())
    assert(new File(".sgit" + File.separator + "objects").exists())
    assert(new File(".sgit" + File.separator + "objects"+ File.separator +"trees").exists())
    assert(new File(".sgit" + File.separator + "objects"+ File.separator +"commits").exists())
    assert(new File(".sgit" + File.separator + "objects"+ File.separator +"blobs").exists())
    assert(new File(".sgit" + File.separator + "logs").exists())
    assert(new File(".sgit" + File.separator + "logs" +File.separator +"master").exists())
    assert(new File(".sgit" + File.separator + "stages").exists())
    assert(new File(".sgit" + File.separator + "HEAD").exists())
  }

  it should "put the right content in the HEAD file" in {
    val pathHead = ".sgit" + File.separator + "HEAD"
    val content = IOManager.readInFileAsLine(pathHead).mkString
    assert(content == "ref: refs/heads/master")
  }

  it should "check if current directory is already initialized with .sgit" in {
    assert(Init_cmd.isInSgitRepository(System.getProperty("user.dir")))
  }

}
