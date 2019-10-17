package fr.cayuelas.objects


import java.util.Calendar

case class Commit(idCommit: String="", parent: String="", parentMerge: Option[String]=None, tree:String="", message: String="" ,commiter:String="MartinCayuelas", author: String="MartinCayuelas", dateCommit:String= Calendar.getInstance().getTime.toString) {


}
object Commit{
  def apply(idCommit: String, parent: String, parentMerge: Option[String], tree: String, commiter: String, author: String, dateCommit: String): Commit = new Commit(idCommit, parent, parentMerge,tree, commiter, author, dateCommit)
}