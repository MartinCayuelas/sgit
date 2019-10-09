package app.objects

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Paths
import java.util.Calendar

import app.commands.Branch_cmd
import app.filesManager.{FilesIO, Logs, Stage}
import app.helpers.HelpersApp

case class Commit(var idCommit: String="", var parent: String="", var parentMerge: Option[String]=None, var tree:String="", commiter:String="MartinCayuelas", author: String="MartinCayuelas", var dateCommit:String= Calendar.getInstance().getTime().toString) {


  def get_idCommit(): String = {
    this.idCommit
  }
  def get_parent(): String = {
    this.parent
  }
  def get_parentMerge(): String = {
    this.parentMerge.get
  }
  def get_commiter(): String = {
    this.commiter
  }
  def get_author(): String = {
    this.author
  }
  def get_tree():String={
    this.tree

  }
  def get_dateCommit(): String = {
    this.dateCommit
  }

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
    List(s"tree ${get_tree()}\n",s"author ${get_author()} -- ${get_dateCommit()}\n")
  }
  def get_commitContent():String = {
    s"tree ${get_tree()} author ${get_author()} -- ${get_dateCommit()}"
  }
  def get_commitContentLog(parent: String, idCommit: String): String = {
    if (parent.length >0)  s"${parent} ${idCommit} ${get_author()} ${get_dateCommit()}\n"
    else   s"0000000000000000000000000000000000000000 ${idCommit} ${get_author()} ${get_dateCommit()}\n"

  }

  //Set in objects/objects/commits
  def saveCommitFile(idSha1: String): Unit = {
    val path = Paths.get(".sgit".concat(File.separator).concat("objects").concat(File.separator).concat("commits")).toAbsolutePath.toString

    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    new File(path + File.separator +  folder).mkdir()
    new File(path + File.separator +  folder + File.separator + nameFile).createNewFile()
    get_commitContentInFileObject().map(line => {
      FilesIO.writeCommit(path + File.separator +  folder + File.separator + nameFile,line)
    })
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
    bw.write(get_idCommit())
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
    Stage.clear_Stage()
    commit.saveCommitFile(commit.get_idCommit())
    commit.set_commitInRefs()
    Logs.writeInLogs(commit.get_commitContentLog(commit.get_parent(),commit.get_idCommit()))
  }
}