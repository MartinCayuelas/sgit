package fr.cayuelas.commands
import org.scalatest.{FunSpec, Matchers}


class CommandAddSpec  extends FunSpec with Matchers {
/*
  override def withFixture(test: NoArgTest): Outcome = {
    // Shared setup (run at beginning of each test)
    try test()
    finally {
      val sgit = new File(".sgit")
      if (sgit.exists()) {
        delete(new File(".sgit"))
      }
    }
  }

  def delete(file: File): Unit = {
    if (file.isDirectory) {
      file.listFiles().map(x => x.delete())
    }
    file.delete()
  }

  describe("Add_cmd") {
      it("should be as many blobs in objects as there are files passed in parameters") {
        //Given
        initSgitRepository(System.getProperty("user.dir"))
        var path: String = System.getProperty("user.dir")
        new File(path + "f1.txt").createNewFile()
       IOManager.writeInFile(path + "f1.txt","f1 with content",append = false)
        var listToTest: Array[String] = Array(Paths.get(System.getProperty("user.dir")+"/t1.txt").toAbsolutePath.toString)
        //When
        Add_cmd.add(listToTest)
        var listOfBlobsInObject = FilesManager.getListOfContentInDirectory(path + "/.sgit/objects/blobs")
        //Then
        assert(listToTest.length == listOfBlobsInObject.length)
      }

  }
*/

}
