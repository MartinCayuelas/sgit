package fr.cayuelas.commands

import fr.cayuelas.managers.{IOManager, LogsManager}

object Log_cmd {

  /**
   * Main Function that dispatchs action for the command Log
   * @param args : Contains the argument of thecommand to dispatch the good action
   */
  def log(args: Array[String]) : Unit ={
    if (args.length == 1) LogsManager.displayLogs()
    else if(args.length == 2) {
      args match {
        case Array(_,"-p") => LogsManager.displayLogOptionP()
        case Array(_,"--stat") => LogsManager.displayLogOptionStat()
        case _ => IOManager.argumentNotSupported()
      }
    }
    else IOManager.numberOfArgumentNotSupported(args(0))
  }
}
