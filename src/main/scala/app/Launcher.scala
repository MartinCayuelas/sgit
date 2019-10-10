package app

import app.commands.Add_cmd.add
import app.commands.Init_cmd.{init,isInSgitRepository}
import app.commands.Branch_cmd.branch
import app.commands.Tag_cmd.tag
import app.commands.Commit_cmd.commit

object Launcher extends App {

  dispatcher(args)

  def dispatcher(args: Array[String]): Unit =  {

    args match {
      //Init
      case Array("init", _) => println("No argument(s) expected.")
      case Array("init") => init()
      //ADD
      case Array("add", _*) => if (isInSgitRepository(".")) add(args) else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      case Array("status", _) => println("No argument(s) expected.")
      case Array("status") => if (isInSgitRepository(".")) status() else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      //Diff
      case Array("diff", _) => println("No argument(s) expected.")
      case Array("diff") => if (isInSgitRepository(".")) diff() else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      //Commit
      case Array("commit", _) => println("No argument(s) expected.")
      case Array("commit") => if (isInSgitRepository("."))commit() else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      //Log
      case Array("log", _*) => if (isInSgitRepository(".")) log(args) else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      //Branch
      case Array("branch", _*)  => if (isInSgitRepository(".")) branch(args) else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      //Checkout
      case Array("checkout", _*)  => if (isInSgitRepository("."))checkout(args)else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      //tag
      case Array("tag", _*)  => if (isInSgitRepository(".")) tag(args) else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      //Merge
      case Array("merge", _*) => if (isInSgitRepository(".")) merge(args) else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      //Rebase
      case Array("rebase", _*) => if (isInSgitRepository(".")) rebase(args) else println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
      //Default Case
      case _ => println("This command doesn't exists")
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
