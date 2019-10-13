package fr.cayuelas.objects


import java.io.File
import java.util.Calendar

import fr.cayuelas.commands.Branch_cmd
import fr.cayuelas.helpers.{HelperPaths, HelperSha1}
import fr.cayuelas.managers.{FilesManager, IOManager, LogsManager, StageManager}

case class Commit(idCommit: String="", parent: String="", parentMerge: Option[String]=None, tree:String="", commiter:String="MartinCayuelas", author: String="MartinCayuelas", dateCommit:String= Calendar.getInstance().getTime().toString) {

  def currentRefs : String = HelperPaths.branchesPath + File.separator + Branch_cmd.getCurrentBranch

  def create_id_commit(): String = {
    HelperSha1.convertToSha1(get_commitContent())
  }
  def get_commitContentInFileObject(): List[String] = {
    List(s"Tree ${tree}\n",s"author ${author} -- ${dateCommit}\n")
  }
  def get_commitContent():String = {
    s"Tree ${tree} author ${author} -- ${dateCommit}"
  }
  def get_commitContentInLog: String = {
    if (parent.length >0)  s"${parent} ${idCommit} ${author} ${dateCommit}\n"
    else   s"0000000000000000000000000000000000000000 ${idCommit} ${author} ${dateCommit}\n"
  }
  //Set in objects/objects/commits
  def saveCommitFile(idSha1: String): Unit = {
    val path = HelperPaths.objectsPath + File.separator + "commits"
    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    FilesManager.createNewFolder(path + File.separator +  folder)
    FilesManager.createNewFile(path + File.separator +  folder + File.separator + nameFile)

    get_commitContentInFileObject().map(line => IOManager.writeInFile(path + File.separator +  folder + File.separator + nameFile,line,true)) //WriteInCommitFile
  }

  //get in refs/heads/branch
  def get_last_commitInRefs(): String = {
    IOManager.readInFile(currentRefs)
  }

  //Set in refs/heads/branch
  def set_commitInRefs(): Unit = {
    IOManager.writeInFile(currentRefs,idCommit,false) //WriteInRefs
  }

}
object Commit{
  def apply(idCommit: String, parent: String, parentMerge: Option[String], tree: String, commiter: String, author: String, dateCommit: String): Commit = new Commit(idCommit, parent, parentMerge,tree, commiter, author, dateCommit)
  def commit(hashTreeFinal: String): Unit = {
    val commit = new Commit()
    val commitCopy = commit.copy(parent = commit.get_last_commitInRefs(),tree = hashTreeFinal,idCommit = commit.create_id_commit())
    commitCopy.saveCommitFile(commitCopy.idCommit)
    commitCopy.set_commitInRefs()

    val currentStageCommit = StageManager.readStageCommit()
    currentStageCommit.map(line => {
      StageManager.deleteLineInStageIfFileAlreadyExists(line.split(" ")(2),StageManager.currentStagePath)
      IOManager.writeInFile(StageManager.currentStagePath,line,append = true)
    }) //WriteInStage

    StageManager.clearStage(StageManager.stageCommitPath)
    StageManager.clearStage(StageManager.stageValidatedPath)
    IOManager.writeInFile(LogsManager.getCurrentPathLogs,commitCopy.get_commitContentInLog,true)//WriteInLogs
  }
}