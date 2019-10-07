package app

import app.commands.Add.add
import app.commands.Init.init
import app.commands.Branch.branch
import app.commands.Tag.tag

object Launcher extends App {

  dispatcher(args)

  def dispatcher(args: Array[String]): Unit =  {

    args match {
      //Init
      case Array("init", _) => println("No argument(s) expected.")
      case Array("init") => init()
        //ADD
      case Array("add", _) => add(args)
      case Array("status", _) => println("No argument(s) expected.")
      case Array("status") => status()
      //Diff
      case Array("diff", _) => println("No argument(s) expected.")
      case Array("diff") => diff()
      //Commit
      case Array("commit", _) => println("No argument(s) expected.")
      case Array("commit") => commit()
      //Log
      case Array("log", _*) => log(args)
      //Branch
      case Array("branch", _*)  => branch(args)
      //Checkout
      case Array("checkout", _*)  => checkout(args)
      //tag
      case Array("tag", _*)  => tag(args)
      //Merge
      case Array("merge", _*) => merge(args)
      //Rebase
      case Array("rebase", _*) => rebase(args)
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
  def commit() : Unit ={
    println("Commit")
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
    if ((args.length == 3) && (args(1).equals("-i"))) println(s"rebase -i")
    else if (args.length == 2)  println(s"rebase")
    else  println(s"Number of arguments not supported for the command '${args(0)}'.")
  }

}
