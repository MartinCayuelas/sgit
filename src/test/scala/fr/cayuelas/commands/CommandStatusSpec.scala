package fr.cayuelas.commands

import java.io.File

import fr.cayuelas.helpers.{HelperPaths, HelperStatus}
import fr.cayuelas.managers.{FilesManager, IoManager, StageManager}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}


class CommandStatusSpec  extends FlatSpec with BeforeAndAfterEach {

  //init an .sgit repo and testFolder repo before each test
  override def beforeEach(): Unit = {
    Init_cmd.init(System.getProperty("user.dir"))
    new File( "testFolder").mkdir()

    FilesManager.createNewFile("testFolder" + File.separator + "hello")
    FilesManager.createNewFile("testFolder" + File.separator + "world")
    IoManager.writeInFile("testFolder" + File.separator + "hello","hello",append = false)
    IoManager.writeInFile("testFolder" + File.separator + "world","world",append = false)
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
  "The status command" should "display newFile in to Be validated if the file has never been committed and it is added" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    val line = IoManager.readInFileAsLine(StageManager.stageValidatedPath)
    print("StageValidated : "+line)
    //Then
    assert(line.length == 1)
    assert(line(0).startsWith("newFile"))
  }

  it should "display modified in to Be validated if the file has been committed once and it is added" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))
    IoManager.writeInFile(helloFilePath,"changes in the file", append = true)
    Add_cmd.add(Array("add",helloFilePath))
    val line = IoManager.readInFileAsLine(StageManager.stageValidatedPath)
    //Then
    assert(line.length == 1)
    assert(line(0).startsWith("modified"))
  }

  it should "display the right number of modifications not validated" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    IoManager.writeInFile(helloFilePath,"changes in the file", append = true)
    val modifiedFilesNotBeeingValidated = HelperStatus.getChangesThatWillNotBeValidated

    //Then
    assert(modifiedFilesNotBeeingValidated.length == 1)
  }

  it should "display the right number of files untracked" in {
    //Given
    val untrackedPath = HelperPaths.sgitPath+"testFolder"+File.separator
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
val stageToCommit = StageManager.readStageToCommit()
    println(stageToCommit)
    val untrackedFiles =  HelperStatus.getUntracked(untrackedPath)
    println(untrackedFiles)
    val line = IoManager.readInFileAsLine(StageManager.stageValidatedPath)
    //Then
    assert(line.length == 1)
    assert(untrackedFiles.length == 1)
  }

  it should "display only files untracked when there is no commit yet and no added files" in {
    //Given
    val sgitPath = HelperPaths.sgitPath+"testFolder"
    val untrackedFiles =  HelperStatus.getUntracked(sgitPath)
    val line = IoManager.readInFileAsLine(StageManager.stageValidatedPath)
    //Then
    assert(line.isEmpty)
    assert(untrackedFiles.length == 2)
  }

}
