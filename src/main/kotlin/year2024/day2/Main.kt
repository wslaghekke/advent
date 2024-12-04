package year2024.day2

import getInputResourceLines
import kotlin.math.abs

enum class ReportStatus {
    INCREASE, DECREASE, UNKNOWN, UNSAFE
}

fun main() {
    val inputRaw = getInputResourceLines(2024, 2)

    val reports = inputRaw.map { line ->
        line.split(" ").map { it.toInt() }
    }

    val safeReportCount = reports.count { report -> isSafeReport(report) }

    println("Safe report count: $safeReportCount")

    val singleFaultToleratingSafeReportCount = reports.count { report ->
        val safeReport = isSafeReport(report)
        if (safeReport) {
            true
        } else {
            // Try to fix the report by removing one value
            for (i in report.indices) {
                val fixedReport = report.toMutableList()
                fixedReport.removeAt(i)
                if (isSafeReport(fixedReport)) {
                    return@count true
                }
            }

            false
        }

    }

    println("Single fault-tolerating safe report count: $singleFaultToleratingSafeReportCount")
}

fun isSafeReport(report: List<Int>): Boolean {
    var status = ReportStatus.UNKNOWN;
    var lastValue: Int? = null;
    for (value in report) {
        if (lastValue != null) {
            status = determineStatus(status, lastValue, value)
            if (status == ReportStatus.UNSAFE) {
                break
            }
        }

        lastValue = value
    }

    if (status == ReportStatus.UNKNOWN) {
        throw Exception("Report is too short, unable to determine if it is safe")
    }

    return status === ReportStatus.INCREASE || status === ReportStatus.DECREASE
}

fun determineStatus(lastStatus: ReportStatus, lastValue: Int, currentValue: Int): ReportStatus {
    val diff = abs(currentValue - lastValue)
    if (diff < 1 || diff > 3) {
        // Found a value that is too far from the last value
        //println("Found a value that is too far from the last value: $value, $lastValue")
        return ReportStatus.UNSAFE
    }

    if (currentValue > lastValue) {
        return when (lastStatus) {
            ReportStatus.UNKNOWN, ReportStatus.INCREASE -> ReportStatus.INCREASE
            ReportStatus.DECREASE, ReportStatus.UNSAFE -> {
                // Found a decrease followed by an increase
                //println("Found a decrease followed by an increase: $value, $lastValue")
                ReportStatus.UNSAFE
            }
        }
    } else if (currentValue < lastValue) {
        return when (lastStatus) {
            ReportStatus.UNKNOWN, ReportStatus.DECREASE -> ReportStatus.DECREASE
            ReportStatus.INCREASE, ReportStatus.UNSAFE -> {
                // Found an increase followed by a decrease
                //println("Found an increase followed by a decrease: $value, $lastValue")
                ReportStatus.UNSAFE
            }
        }
    } else {
        // Found a value that is the same as the last value
        //println("Found a value that is the same as the last value: $value, $lastValue")
        return ReportStatus.UNSAFE
    }
}
