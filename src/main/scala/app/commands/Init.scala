package app.commands

import app.FilesIO

object Init {

  /*
INIT -----------
 */
  def init() : Unit = {
    FilesIO.initSgitRepository()
  }
  /*
  --------------
   */

}
