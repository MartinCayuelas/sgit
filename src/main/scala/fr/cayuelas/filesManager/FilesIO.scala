package fr.cayuelas.filesManager


import java.io.{BufferedWriter, File, FileWriter}

object FilesIO {


  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param path           : file's path in which we want to write
   * @param content        : content to write in the file
   * @param append : To specify if the previous content of the file should be appended or not
   */
  def writeInFile(path: String, content: String, append: Boolean) = {
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file, append))
    bw.write(content)
    bw.close()
  }


}
