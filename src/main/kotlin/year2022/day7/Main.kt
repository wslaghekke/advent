package year2022.day7

import getInputResourceLines

fun main() {
    val inputRaw = getInputResourceLines(2022, 7)
    val totalDiskSize = 70000000;
    val neededFreeSpace = 30000000;

    val fsFolders = parseElfShellOutput(inputRaw)
    val root = fsFolders.first()

    val smallerFolderTotalSize = fsFolders.filter { it.size <= 100000 }.sumOf { it.size }
    println("Sum of smaller dir sizes: $smallerFolderTotalSize")

    val currentFreeSpace = totalDiskSize - root.size
    val deleteToFreeSpace = neededFreeSpace - currentFreeSpace


    val smallestDeleteCandidate = fsFolders.filter { it.size >= deleteToFreeSpace }.minByOrNull { it.size }

    println("Size of smallest delete candidate: ${smallestDeleteCandidate!!.size}")
}

interface ElfFSItem {
    val name: String;
    val size: Int
}
data class ElfDirectory(
    override val name: String,
    val items: MutableList<ElfFSItem> = mutableListOf()
): ElfFSItem {
    override val size: Int
        get() = items.sumOf { it.size }
}
data class ElfFile(
    override val name: String,
    override val size: Int
): ElfFSItem

val fileSizeRegex = Regex("(\\d+) (.+)")

fun parseElfShellOutput(lines: List<String>): MutableList<ElfDirectory> {
    val cwd = mutableListOf<ElfDirectory>()
    val dirs = mutableListOf<ElfDirectory>()

    for (line in lines) {
        when {
            line == "$ cd .." -> cwd.removeLast()
            line.startsWith("$ cd") -> {
                val folderName = line.removePrefix("$ cd ")
                val folder = ElfDirectory(folderName)
                cwd.lastOrNull()?.items?.add(folder)
                cwd.add(folder)
                dirs.add(folder)
            }
            line.startsWith("dir") -> {
                // Ignore
            }
            else -> {
                fileSizeRegex.matchEntire(line)?.destructured?.let { (fileSize, fileName) ->
                    val currentDir = cwd.lastOrNull() ?: throw RuntimeException("File outside folder")
                    currentDir.items.add(ElfFile(fileName, fileSize.toInt()))
                }

            }
        }
    }

    return dirs;

}
