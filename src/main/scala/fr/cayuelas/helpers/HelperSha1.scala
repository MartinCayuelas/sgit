package fr.cayuelas.helpers


import java.security.MessageDigest

object HelperSha1 {
  /**
   * Function that return a new string converted given a string in parameter
   * @param string : the string that will be converted
   * @return a string digested by Sha1 Algorithm
   */
  def convertToSha1(string : String): String ={
    MessageDigest.getInstance("SHA-1").digest(string.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }

}
