package fr.cayuelas.commands

import java.io.File

import fr.cayuelas.helpers.HelperDiff.{createMatrix, getDeltas}
import fr.cayuelas.helpers.{HelperBlob, HelperCommit, HelperDiff, HelperPaths}
import fr.cayuelas.managers.LogsManager.retrieveChanges
import fr.cayuelas.managers.{FilesManager, IoManager, LogsManager}
import fr.cayuelas.objects.Wrapper
import org.scalatest.{BeforeAndAfterEach, FlatSpec}


class CommandLogSpec  extends FlatSpec with BeforeAndAfterEach {

  //init an .sgit repo and testFolder repo before each test
  override def beforeEach(): Unit = {
    Init_cmd.init(System.getProperty("user.dir"))
    new File( "testFolder").mkdir()

    FilesManager.createNewFile("testFolder" + File.separator + "hello")
    FilesManager.createNewFile("testFolder" + File.separator + "world")
    IoManager.writeInFile("testFolder" + File.separator + "hello","hello",false)
    IoManager.writeInFile("testFolder" + File.separator + "world","world",false)
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

  "the log command" should "display all the logs" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))

    val numberOfLogs = IoManager.readInFileAsLine(HelperPaths.logsPath+File.separator+"master")
    assert(numberOfLogs.length == 1)

  }

  it should "be the right number of files changed, total insertions and total deletions diplayed for a commit when log --stat" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))

    val logs = IoManager.readInFileAsLine(HelperPaths.logsPath+File.separator+"master")

    val (parentCommit,currentCommit): (String,String) =  (logs.head.split(" ")(0),logs.head.split(" ")(1))
    val (inserted,deleted)  = HelperDiff.diffBetweenTwoCommits(currentCommit,parentCommit,true)
    val filesChanged = retrieveChanges(currentCommit,parentCommit)

    assert(filesChanged == 1)
    assert(inserted == 1)
    assert(deleted == 0)
  }


  it should "be the right number  of insertions and  deletions diplayed for a file when log --stat" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))

    val listBlobCommit: List[Wrapper] = HelperCommit.getAllBlobsFromCommit(HelperCommit.getLastCommitInRefs())
    val contentBlobCurrent = HelperBlob.readContentInBlob(listBlobCommit.head.hash)
    val resFunc = LogsManager.displayStatsLog(List(), contentBlobCurrent, listBlobCommit.head.path)
    val resInsertedDeleted = Some((resFunc._1,resFunc._2))

    assert(resInsertedDeleted.get._1 == 1)
    assert(resInsertedDeleted.get._2 == 0)
  }

  it should "be the right deltas diplayed when log -p" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))

    val listBlobCommit: List[Wrapper] = HelperCommit.getAllBlobsFromCommit(HelperCommit.getLastCommitInRefs())
    val contentBlobCurrent = HelperBlob.readContentInBlob(listBlobCommit.head.hash)

    val deltas = getDeltas(List(""), contentBlobCurrent, 0, contentBlobCurrent.length - 1, createMatrix(List(""), contentBlobCurrent, 0, 0, Map()), List())

    assert(deltas.count(x=> x.startsWith("+")) == 1)
  }


}
