package app.objects

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Paths
import java.util.Calendar

import app.commands.Branch_cmd
import app.filesManager.{FilesIO, Logs, Stage}
import app.helpers.HelpersApp

case class Commit(var idCommit: String="", var parent: String="", var parentMerge: Option[String]=None, var tree:String="", commiter:String="MartinCayuelas", author: String="MartinCayuelas", var dateCommit:String= Calendar.getInstance().getTime().toString) {

  def set_idCommit(id: String): Unit = {
    this.idCommit = id
  }
  def set_parent(p: String): Unit = {
    this.parent = p
  }
  def set_parentMerge(p: String): Unit = {
    this.parentMerge = Some(p)
  }
  def set_tree(newTree: String):Unit = {
    this.tree = newTree

  }

  def create_id_commit(): String = {
    HelpersApp.convertToSha1(get_commitContent())
  }
  def get_commitContentInFileObject(): List[String] = {
    List(s"tree ${tree}\n",s"author ${author} -- ${dateCommit}\n")
  }
  def get_commitContent():String = {
    s"tree ${tree} author ${author} -- ${dateCommit}"
  }
  def get_commitContentInLog: String = {
    if (parent.length >0)  s"${parent} ${idCommit} ${author} ${dateCommit}\n"
    else   s"0000000000000000000000000000000000000000 ${idCommit} ${author} ${dateCommit}\n"

  }

  //Set in objects/objects/commits
  def saveCommitFile(idSha1: String): Unit = {
    val path = Paths.get(".sgit".concat(File.separator).concat("objects").concat(File.separator).concat("commits")).toAbsolutePath.toString

    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    new File(path + File.separator +  folder).mkdir()
    new File(path + File.separator +  folder + File.separator + nameFile).createNewFile()
    get_commitContentInFileObject().map(line => FilesIO.writeCommit(path + File.separator +  folder + File.separator + nameFile,line))
  }

  //get in refs/heads/branch
  def get_last_commitInRefs(): String = {
    val path = Paths.get(".sgit".concat(File.separator).concat("refs").concat(File.separator).concat("heads").concat(File.separator).concat(Branch_cmd.getCurrentBranch)).toAbsolutePath.toString
    val file = new File(path)
    val source = scala.io.Source.fromFile(file)
    val content = try source.mkString finally source.close()

    content
  }

  //Set in refs/heads/branch
  def set_commitInRefs(): Unit = {
    val path = Paths.get(".sgit".concat(File.separator).concat("refs").concat(File.separator).concat("heads").concat(File.separator).concat(Branch_cmd.getCurrentBranch)).toAbsolutePath.toString
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(idCommit)
    bw.close()
  }



}
object Commit{
  def apply(idCommit: String, parent: String, parentMerge: Option[String], tree: String, commiter: String, author: String, dateCommit: String): Commit = new Commit(idCommit, parent, parentMerge,tree, commiter, author, dateCommit)
  def commit(hashTreeFinal: String): Unit = {
    val commit = new Commit()
    commit.set_parent(commit.get_last_commitInRefs())
    commit.set_tree(hashTreeFinal)
    commit.set_idCommit(commit.create_id_commit())
    Stage.clearStage()
    commit.saveCommitFile(commit.idCommit)
    commit.set_commitInRefs()
    Logs.writeInLogs(commit.get_commitContentInLog)
  }
}