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
        case Array(_,"-p") => LogsManager.displayLogsOption(false)
        case Array(_,"--stat") => LogsManager.displayLogsOption(true)
        case _ => IOManager.argumentNotSupported()
      }
    }
    else IOManager.numberOfArgumentNotSupported(args(0))
  }
}
