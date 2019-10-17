package fr.cayuelas.commands
import java.io.File

import fr.cayuelas.helpers.{HelperBranch, HelperCommit, HelperPaths}
import fr.cayuelas.managers.{FilesManager, IOManager, LogsManager, StageManager}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}


class CommandCommitSpec  extends FlatSpec with BeforeAndAfterEach {

  //init an .sgit repo and testFolder repo before each test
  override def beforeEach(): Unit = {
    Init_cmd.init(System.getProperty("user.dir"))
    new File( "testFolder").mkdir()

    FilesManager.createNewFile("testFolder" + File.separator + "hello")
    FilesManager.createNewFile("testFolder" + File.separator + "world")
    IOManager.writeInFile("testFolder" + File.separator + "hello","HELLO",false)
    IOManager.writeInFile("testFolder" + File.separator + "world","HELP",false)
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

  "the commit command" should "no do commit action if there is nothing to commit" in {
    //Given and /When
    Commit_cmd.commit(Array("commit"))
    val contentStage = IOManager.readInFileAsLine(StageManager.currentStagePath) // Commited files go to stage
    val contentObjectsCommit = FilesManager.getListOfContentInDirectory(HelperPaths.objectsPath+File.separator+"commits")

    //Then
    assert(contentStage.isEmpty)
    assert(contentObjectsCommit.isEmpty)
  }

  "the stage" should "no be empty after a commit" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))

    val contentStage = IOManager.readInFileAsLine(StageManager.currentStagePath) // Commit files go to stage
    //Then
    assert(contentStage.nonEmpty)
  }

  "the stage" should "be different after 2 commits" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    val worldFilePath= sgitPath + File.separator + "testFolder" + File.separator + "world"
    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    val stage =  IOManager.readInFileAsLine(StageManager.currentStagePath)

    Add_cmd.add(Array("add",worldFilePath))

    Commit_cmd.commit(Array("commit"))
    val stage2 =  IOManager.readInFileAsLine(StageManager.currentStagePath)

    assert(stage != stage2)
  }

  "the objects/commits repository" should "no be empty after a commit" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))


    val contentObjectsCommit = FilesManager.getListOfContentInDirectory(HelperPaths.objectsPath+File.separator+"commits")

    //Then
    assert(contentObjectsCommit.nonEmpty)
  }

  it should "be composed of a folder and a file with the id of a tree" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))

    val contentObjectsCommit = FilesManager.getListOfContentInDirectory(HelperPaths.objectsPath+File.separator+"commits")
    val filesInFolderOfTheCommit = FilesManager.getListOfContentInDirectory(HelperPaths.objectsPath+File.separator+"commits"+File.separator+contentObjectsCommit.head.getName)
    val contentInfile = IOManager.readInFileAsLine(filesInFolderOfTheCommit.head.getAbsolutePath)
    //Then
    assert(contentObjectsCommit.size == 1)
    assert(filesInFolderOfTheCommit.size == 1)
    assert(contentInfile.nonEmpty)
  }


  "the stageCommit" should "be cleared after a commit" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))

    val contentStageCommit = IOManager.readInFileAsLine(StageManager.stageToCommitPath)
    //Then
    assert(contentStageCommit.isEmpty)
  }

  it should "be a log created with the good hash" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit","-m","commit1"))
    val commitId = HelperCommit.getLastCommitInRefs()
    val logs = LogsManager.getLogsForBranch(HelperBranch.getCurrentBranch)
    val hashCommit = logs(0).split(" ")(1)
    //Then
    assert(logs.length == 1)
    assert(commitId == hashCommit)
  }



}
