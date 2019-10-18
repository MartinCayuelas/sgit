package fr.cayuelas.commands

import java.io.File

import fr.cayuelas.helpers.{HelperBranch, HelperCommit, HelperPaths}
import fr.cayuelas.managers.{FilesManager, IOManager}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

class CommandBranchSpec   extends FlatSpec with BeforeAndAfterEach {

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

  "The branch command" should "create a file in .sgit/refs/heads" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    HelperBranch.createBranch("testBranch")
    assert(new File(HelperPaths.branchesPath+File.separator+"testBranch").exists())
    assert(FilesManager.getListOfFiles(HelperPaths.branchesPath).length == 2)
  }
  it should "create a file in logs" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    HelperBranch.createBranch("testBranch")
    assert(new File(HelperPaths.logsPath+File.separator+"testBranch").exists())
  }

  it should "create a file in stages folder" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    HelperBranch.createBranch("testBranch")
    assert(new File(HelperPaths.stagePath+File.separator+"testBranch").exists())
  }


  it should "copy the content of the current stage branch to the new file stage branch" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    HelperBranch.createBranch("testBranch")
    val stageMaster =IOManager.readInFileAsLine(HelperPaths.stagePath+File.separator+"master")
    val testBranchStage =IOManager.readInFileAsLine(HelperPaths.stagePath+File.separator+"testBranch")
    assert(stageMaster == testBranchStage)
  }

  it should "be the right content in the file created" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    HelperBranch.createBranch("testBranch")
    assert(new File(HelperPaths.branchesPath+File.separator+"testBranch").exists())
    val contentNewBranchFile = IOManager.readInFile(HelperPaths.branchesPath+File.separator+"testBranch")
    assert(contentNewBranchFile == HelperCommit.getLastCommitInRefs())
  }

  it should "not create a new branch if already exists" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))
    HelperBranch.createBranch("master")
    //Then
    assert(new File(HelperPaths.branchesPath+File.separator+"master").exists())
    assert(FilesManager.getListOfFiles(HelperPaths.branchesPath).length == 1)
  }

  it should "not create a new branch if there is no commit" in {
    HelperBranch.createBranch("testBranch")
    assert(!new File(HelperPaths.branchesPath+File.separator+"testBranch").exists())

  }





}
