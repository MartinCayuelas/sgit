package fr.cayuelas

import commands.Init_cmd.init
import commands.Add_cmd.add
import commands.Commit_cmd.commit
import commands.Branch_cmd.branch
import commands.Tag_cmd.tag
import commands.Status_cmd.status
import commands.Log_cmd.log
import fr.cayuelas.commands.Init_cmd
import fr.cayuelas.managers.IOManager

object Launcher extends App {

  dispatcher(args)

  def dispatcher(args: Array[String]): Unit =  {
    val isInSgitRepository: Boolean = Init_cmd.isInSgitRepository(System.getProperty("user.dir"))

    args match {
      //Init
      case Array("init", _) => IOManager.noArgumentsExpected()
      case Array("init") => init(System.getProperty("user.dir"))
      //ADD
      case Array("add", _*) => if (isInSgitRepository) add(args) else IOManager.notSgitReposiroty()
        //Status
      case Array("status", _) => IOManager.noArgumentsExpected()
      case Array("status") => if (isInSgitRepository) status() else IOManager.notSgitReposiroty()
      //Diff
      case Array("diff", _) => IOManager.noArgumentsExpected()
      case Array("diff") => if (isInSgitRepository) diff() else IOManager.notSgitReposiroty()
      //Commit
      case Array("commit", _) => IOManager.noArgumentsExpected()
      case Array("commit") => if (isInSgitRepository) commit() else IOManager.notSgitReposiroty()
      //Log
      case Array("log", _*) => if (isInSgitRepository) log(args) else IOManager.notSgitReposiroty()
      //Branch
      case Array("branch", _*)  => if (isInSgitRepository) branch(args) else IOManager.notSgitReposiroty()
      //Checkout
      case Array("checkout", _*)  => if (isInSgitRepository) checkout(args)else IOManager.notSgitReposiroty()
      //tag
      case Array("tag", _*)  => if (isInSgitRepository) tag(args) else IOManager.notSgitReposiroty()
      //Merge
      case Array("merge", _*) => if (isInSgitRepository) merge(args) else IOManager.notSgitReposiroty()
      //Rebase
      case Array("rebase", _*) => if (isInSgitRepository) rebase(args) else IOManager.notSgitReposiroty()
      //Default Case
      case _ => IOManager.notExistingCommand()
    }
  }


  def diff() : Unit ={
    println("Diff")
  }



  def checkout(args: Array[String]): Unit = {
    if (args.length == 2) println(s"Tipping on the branch ${args(1)}")
    else  IOManager.numberOfArgumentNotSupported(args(0))
  }

  def merge(args: Array[String]): Unit = {
    if (args.length == 2) println(s"merge")
    else  IOManager.numberOfArgumentNotSupported(args(0))
  }
  def rebase(args: Array[String]): Unit = {
    if ((args.length == 3) && args(1).equals("-i")) println(s"rebase -i")
    else if (args.length == 2)  println(s"rebase")
    else  IOManager.numberOfArgumentNotSupported(args(0))
  }

}
