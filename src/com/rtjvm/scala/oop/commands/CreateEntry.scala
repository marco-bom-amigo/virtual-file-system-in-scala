package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

abstract class CreateEntry(name: String) extends Command {

  override def apply(state: State): State = {

    val wd = state.wd
    if (wd.hasEntry(name)) state.setMessage("Entry" + name + "already exists!")
    else if (name.contains(Directory.SEPARATOR)) {
      state.setMessage(name + "must not contains separators!")
    } else if (checkIllegal(name)) {
      state.setMessage(name + ": illegal entry name!")
    } else {
      doCreateEntry(state, name)
    }

  }

  def checkIllegal(name: String): Boolean = {
    name.contains(".")
  }

  def doCreateEntry(state: State, name: String): State = {
    def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
      if(path.isEmpty) currentDirectory.addEntry(newEntry)
      else {
        val oldEntry = currentDirectory.findEntry(path.head).asDirectory
        currentDirectory.replaceEntry(oldEntry.name, updateStructure(oldEntry, path.tail, newEntry))
      }
    }
    val wd = state.wd
    val allDirsInPath = wd.getAllFoldersInPath
    val newEntry: DirEntry = createSpecificEntry(state = state)
    val newRoot = updateStructure(state.root, allDirsInPath, newEntry)
    val newWD = newRoot.findDescendant(allDirsInPath)
    State(newRoot, newWD)
  }

  def createSpecificEntry(state: State): DirEntry

}
