package fr.cayuelas.commands

import fr.cayuelas.helpers.HelperStatus
object Status_cmd {

  /**
   * Main function that dispatch the action
   */
  def status() : Unit ={
    HelperStatus.printChangesThatWillBeValidated()
    HelperStatus.printChangesThatWillNotBeValidated()
    HelperStatus.printUntrackedFiles()
  }
}