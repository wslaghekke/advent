
fun getInputResourceLines(year: Int, day: Int): List<String> {
    return object {}.javaClass.getResourceAsStream("$year/day$day.txt")?.bufferedReader()?.readLines()
        ?: throw RuntimeException("Failed to find input resource for year $year, day $day")
}
