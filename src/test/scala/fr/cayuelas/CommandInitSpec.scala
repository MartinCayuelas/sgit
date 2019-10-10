package fr.cayuelas

import java.io.File

import fr.cayuelas.commands.Init_cmd.initSgitRepository
import org.scalatest.{FlatSpec, Matchers}


class CommandInitSpec  extends FlatSpec with Matchers  {
  

  override def withFixture(test: NoArgTest) = {
    // Shared setup (run at beginning of each test)
    try test()
    finally {
      val sgit = new File(".sgit")
      if (sgit.exists()) {
        clear(new File(".sgit"))
      }
    }
  }

  def clear(file: File): Unit = {
    if (file.isDirectory) {
      file.listFiles().map(_.delete)
    }
    file.delete()
  }

  "Init_cmd" should "create a .sgit folder with folders and all the structure" in {
    initSgitRepository(System.getProperty("user.dir"))
    assert(new File(".sgit").exists())
    assert(new File(s".sgit${File.separator}HEAD").exists())
    assert(new File(s".sgit${File.separator}tags").exists())
    assert(new File(s".sgit${File.separator}trees").exists())
    assert(new File(s".sgit${File.separator}blobs").exists())
    assert(new File(s".sgit${File.separator}branches").exists())
  }
/*
  it should "check if a sgit folder if existing already" in {
    assert(isInRepository("."))
  }
*/

}
