package year2024.day9

import getInputResourceLines

data class File(
    val length: Int,
    val id: Int,
    val blockIndices: MutableSet<Int>
)

const val debugPrint = false

fun main() {
    val inputRaw = getInputResourceLines(2024, 9).first()

    part1(inputRaw)
    part2(inputRaw)
}

private fun part1(inputRaw: String) {
    val (files, blocks) = buildFsFilesAndBlocks(inputRaw)

    if (debugPrint) printBlocks(blocks)

    var emptyBlockIndex = 0
    val nextEmptyBlock = {
        try {
            while (blocks[emptyBlockIndex] !== null) {
                emptyBlockIndex++
            }

            emptyBlockIndex
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    // Rearrange blocks to build contiguous data without any empty spaces
    for (i in blocks.indices.reversed()) {
        val file = blocks[i]
        if (file === null) {
            continue
        }

        val emptyBlock = nextEmptyBlock()
        // If no empty block is found, or if the empty block is after the file block, then we don't need to move anything
        if (emptyBlock == null || emptyBlock > i) {
            break
        }

        // Move the file block to the empty block
        blocks[emptyBlock] = file
        blocks[i] = null

        file.blockIndices.remove(i)
        file.blockIndices.add(emptyBlock)

        if (debugPrint) printBlocks(blocks)
    }

    printBlocks(blocks)

    // Time to calculate the checksum
    val checksum = blocks.indices.sumOf { i ->
        val fileId = blocks[i]?.id ?: 0
        i * fileId.toLong()
    }

    println("Part 1: $checksum")
}

data class EmptySpace(
    var length: Int,
    var startIndex: Int
)

private fun part2(inputRaw: String) {
    val (files, blocks) = buildFsFilesAndBlocks(inputRaw)

    if (debugPrint) printBlocks(blocks)

    val emptySpaces = mutableListOf<EmptySpace>()
    var emptySpaceStartIndex = -1

    for (i in blocks.indices) {
        if (blocks[i] == null) {
            if (emptySpaceStartIndex == -1) {
                emptySpaceStartIndex = i
            }
        } else {
            if (emptySpaceStartIndex != -1) {
                emptySpaces.add(EmptySpace(i - emptySpaceStartIndex, emptySpaceStartIndex))
                emptySpaceStartIndex = -1
            }
        }
    }

    if (emptySpaceStartIndex != -1) {
        emptySpaces.add(EmptySpace(blocks.size - emptySpaceStartIndex, emptySpaceStartIndex))
    }

    // Rearrange blocks to build contiguous data without any empty spaces
    // Only move files in their entirety
    for (i in files.indices.reversed()) {
        val file = files.elementAt(i)
        val firstEmptySpace = emptySpaces.firstOrNull { it.length >= file.length && it.startIndex <= file.blockIndices.first() }
        if (firstEmptySpace == null) {
            // No empty space large enough to fit the file, go to the next file
            continue
        }

        // Remove file from original blocks
        // We don't need to update the empty spaces list because we only move files right to left
        for (j in file.blockIndices) {
            blocks[j] = null
        }
        file.blockIndices.clear()
        for (j in firstEmptySpace.startIndex until firstEmptySpace.startIndex + file.length) {
            blocks[j] = file
            file.blockIndices.add(j)
        }

        // Add the remaining empty space back to the list
        if (firstEmptySpace.length > file.length) {
            // Update the fields of the empty space
            firstEmptySpace.length -= file.length
            firstEmptySpace.startIndex += file.length
        } else {
            emptySpaces.remove(firstEmptySpace)
        }

        if (debugPrint) printBlocks(blocks)
    }

    printBlocks(blocks)

    // Time to calculate the checksum
    val checksum = blocks.indices.sumOf { i ->
        val fileId = blocks[i]?.id ?: 0
        i * fileId.toLong()
    }

    println("Part 1: $checksum")
}

private fun buildFsFilesAndBlocks(inputRaw: String): Pair<MutableSet<File>, Array<File?>> {
    var nextFileId = 0
    var nextBlockIndex = 0
    val files = mutableSetOf<File>()

    for (i in inputRaw.indices) {
        val char = inputRaw[i]
        if (char.isDigit()) {
            val itemLength = char.digitToInt()
            // Even indexes are files, odd indexes are empty spaces
            val isFile = i % 2 == 0
            if (isFile) {
                val fileId = nextFileId++
                files.add(
                    File(
                        itemLength,
                        fileId,
                        (nextBlockIndex..<(nextBlockIndex + itemLength)).toMutableSet()
                    )
                )
            }
            nextBlockIndex += itemLength
        } else {
            throw IllegalArgumentException("Invalid digit: $char")
        }
    }

    val totalBlockCount = nextBlockIndex
    val blocks = Array<File?>(totalBlockCount) { null }

    for (file in files) {
        for (blockIndex in file.blockIndices) {
            blocks[blockIndex] = file
        }
    }
    return Pair(files, blocks)
}

fun printBlocks(blocks: Array<File?>) {
    for (block in blocks) {
        print(block?.id ?: '.')
    }
    println()
}


fun <T> MutableList<T>.removeFirstMatching(predicate: (T) -> Boolean): T? {
    val index = indexOfFirst(predicate)
    return if (index != -1) {
        removeAt(index)
    } else {
        null
    }
}
