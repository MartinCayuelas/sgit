package fr.cayuelas.commands

import org.scalatest.{FunSpec, Matchers}


class CommandInitSpec  extends FunSpec with Matchers  {
/*
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
     Init_cmd.init()
      assert(new File(System.getProperty("user.dir") + "/.sgit").exists())
    }
    it("should verify if a .sgit folder already exists in parent folder") {
      Init_cmd.init()
      new File(System.getProperty("user.dir") + "/test").mkdir()
      assert(Init_cmd.isInSgitRepository(System.getProperty("user.dir") + "/test"))
    }
    it("should verify if a .sgit/objects folder exists") {
      Init_cmd.init()
      assert(new File(HelperPaths.objectsPath).exists())
    }
    it("should verify if a .sgit/objects/trees folder exists") {
      Init_cmd.init()
      assert(new File(HelperPaths.objectsPath + File.separator+"trees").exists())
    }
    it("should verify if a .sgit/objects/blobs folder exists") {
      Init_cmd.init()
      assert(new File(HelperPaths.objectsPath + File.separator+"blobs").exists())
    }
    it("should verify if a .sgit/objects/commits folder exists") {
      Init_cmd.init()
      assert(new File(HelperPaths.objectsPath + File.separator+"commits").exists())
    }
  }*/



}
