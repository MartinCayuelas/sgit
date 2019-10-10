package fr.cayuelas.commands

import java.io.File

import fr.cayuelas.commands.Init_cmd.{initSgitRepository, isInSgitRepository}
import org.scalatest.{FunSpec, Matchers, Outcome}


class CommandInitSpec  extends FunSpec with Matchers  {

  override def withFixture(test: NoArgTest): Outcome = {
    // Shared setup (run at beginning of each test)
    try test()
    finally {
      val sgit = new File(".sgit")
      if (sgit.exists()) {
        delete(new File(".sgit"))
      }
      val test = new File("test")
      if (test.exists()) {
        delete(new File("test"))
      }
    }
  }

  def delete(file: File): Unit = {
    if (file.isDirectory) {
      file.listFiles().map(x => x.delete())
    }
    file.delete()
  }

  describe("Init_cmd.initSgitRepository") {
    it("should create a .sgit folder with folders and all the structure") {
      initSgitRepository(System.getProperty("user.dir"))
      assert(new File(System.getProperty("user.dir") + "/.sgit").exists())
    }
    it("should verify if a .sgit folder already exists in parent folder") {
      initSgitRepository(System.getProperty("user.dir"))
      new File(System.getProperty("user.dir") + "/test").mkdir()
      assert(isInSgitRepository(System.getProperty("user.dir") + "/test"))
    }
    it("should verify if a .sgit/objects folder exists") {
      initSgitRepository(System.getProperty("user.dir"))
      assert(new File(System.getProperty("user.dir") + "/.sgit/objects").exists())
    }
    it("should verify if a .sgit/objects/trees folder exists") {
      initSgitRepository(System.getProperty("user.dir"))
      assert(new File(System.getProperty("user.dir") + "/.sgit/objects/trees").exists())
    }
    it("should verify if a .sgit/objects/blobs folder exists") {
      initSgitRepository(System.getProperty("user.dir"))
      assert(new File(System.getProperty("user.dir") + "/.sgit/objects/blobs").exists())
    }
    it("should verify if a .sgit/objects/commits folder exists") {
      initSgitRepository(System.getProperty("user.dir"))
      assert(new File(System.getProperty("user.dir") + "/.sgit/objects/commits").exists())
    }
  }



}
