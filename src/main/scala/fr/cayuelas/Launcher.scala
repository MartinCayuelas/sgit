package fr.cayuelas

import commands.Init_cmd.init
import commands.Add_cmd.add
import commands.Commit_cmd.commit
import commands.Branch_cmd.branch
import commands.Tag_cmd.tag
import fr.cayuelas.commands.Init_cmd
import fr.cayuelas.managers.IOManager

object Launcher extends App {

  dispatcher(args)

  def dispatcher(args: Array[String]): Unit =  {
    val isInSgitRepository: Boolean = Init_cmd.isInSgitRepository(System.getProperty("user.dir"))

    args match {
      //Init
      case Array("init", _) => IOManager.noArgumentsExpected()
      case Array("init") => init()
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



  def status() : Unit ={
    println("Status")
  }
  def diff() : Unit ={
    println("Diff")
  }


  def log(args: Array[String]) : Unit ={
    if (args.length == 1) println("log")
    else if(args.length == 2) {
      args match {
        case Array(_,"-p") => println("log -p")
        case Array(_,"--stat") => println("log --stat")
        case _ => println("Argument not supported.")
      }
    }
    else println("Too many arguments")
  }


  def checkout(args: Array[String]): Unit = {
    if (args.length == 2) println(s"Tipping on the branch ${args(1)}")
    else  println(s"Number of arguments not supported for the command '${args(0)}'.")
  }

  def merge(args: Array[String]): Unit = {
    if (args.length == 2) println(s"merge")
    else  println(s"Number of arguments not supported for the command '${args(0)}'.")
  }
  def rebase(args: Array[String]): Unit = {
    if ((args.length == 3) && args(1).equals("-i")) println(s"rebase -i")
    else if (args.length == 2)  println(s"rebase")
    else  println(s"Number of arguments not supported for the command '${args(0)}'.")
  }

}
