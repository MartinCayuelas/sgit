package fr.cayuelas.commands

import java.io.File

import fr.cayuelas.helpers.HelperDiff.{createMatrix, getDeltas}
import fr.cayuelas.helpers.{HelperCommit, HelperDiff, HelperPaths}
import fr.cayuelas.managers.{FilesManager, IOManager}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

class CommandDiffSpec  extends FlatSpec with BeforeAndAfterEach {

  //init an .sgit repo and testFolder repo before each test
  override def beforeEach(): Unit = {
    Init_cmd.init(System.getProperty("user.dir"))
    new File( "testFolder").mkdir()

    FilesManager.createNewFile("testFolder" + File.separator + "hello")
    FilesManager.createNewFile("testFolder" + File.separator + "world")
    IOManager.writeInFile("testFolder" + File.separator + "hello","hello",false)
    IOManager.writeInFile("testFolder" + File.separator + "world","world\nis big",false)
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

  it should "be equals when the files are same" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    val newContent = IOManager.readInFileAsLine(helloFilePath)
    val oldContent = IOManager.readInFileAsLine(helloFilePath)

    val matrix = createMatrix(oldContent, newContent, 0, 0, Map())
    val deltas = getDeltas(oldContent, newContent, oldContent.length - 1, newContent.length - 1, matrix, List())

    //Then
    assert(deltas.isEmpty)
  }

  it should "be only inserting lines when the file is new" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    val newContent = IOManager.readInFileAsLine(helloFilePath)
    val oldContent = List("")

    val matrix = createMatrix(oldContent, newContent, 0, 0, Map())
    val deltas = getDeltas(oldContent, newContent, oldContent.length - 1, newContent.length - 1, matrix, List())

    //Then
    assert(deltas.count(x => x.startsWith("+")) == newContent.length)
  }

  it should "be only deleted lines when the content of a file is deleted" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    val newContent = List("")
    val oldContent = IOManager.readInFileAsLine(helloFilePath)

    val matrix = createMatrix(oldContent, newContent, 0, 0, Map())
    val deltas = getDeltas(oldContent, newContent, oldContent.length - 1, newContent.length - 1, matrix, List())

    //Then
    assert(deltas.count(x => x.startsWith("-")) == oldContent.length)
  }

  it should "be the good number of insertions and deletions when modification of a file" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    val worldFilePath = sgitPath + File.separator + "testFolder" + File.separator + "world"
    val newContent = IOManager.readInFileAsLine(helloFilePath)
    val oldContent = IOManager.readInFileAsLine(worldFilePath)

    val matrix = createMatrix(oldContent, newContent, 0, 0, Map())
    val deltas = getDeltas(oldContent, newContent, oldContent.length - 1, newContent.length - 1, matrix, List())

    //Then
    assert(deltas.count(x => x.startsWith("-")) == 2)
    assert(deltas.count(x => x.startsWith("+")) == 1)
    assert(deltas.count(x => x.startsWith("+")) !== deltas.count(x => x.startsWith("-")))
  }

  it should "be the right number of insertion and deletion when committing" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    val worldFilePath = sgitPath + File.separator + "testFolder" + File.separator + "world"

    Add_cmd.add(Array("add",helloFilePath,worldFilePath))
    Commit_cmd.commit(Array("commit"))

    IOManager.writeInFile("testFolder" + File.separator + "hello","HELLO",append = false)
    IOManager.writeInFile("testFolder" + File.separator + "world","HELP",append = false)
    Add_cmd.add(Array("add",helloFilePath,worldFilePath))

    val lastCommit = HelperCommit.getLastCommitInRefs()
    val (inserted,deleted) = HelperDiff.diffWhenCommitting(lastCommit)

    //Then
    assert(inserted == 2)
    assert(deleted== 3)
    assert(inserted !== deleted)
  }


}
