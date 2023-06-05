package com.example.paylasimmvvm.util

object TimeAgo {
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS


    fun getTimeAgo(time: Long): String? {
        var time1 = time
        if (time1 < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time1 *= 1000
        }
        val now = System.currentTimeMillis()
        if (time1 > now || time1 <= 0) {
            return null
        }
        val diff = now - time1
        return if (diff < MINUTE_MILLIS) {
            "AZ ÖNCE"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "1 DAKİKA ÖNCE"
        } else if (diff < 50 * MINUTE_MILLIS) {
            ( diff/ MINUTE_MILLIS).toString()+ " DAKİKA ÖNCE"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "1 SAAT ÖNCE"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + " SAAT ÖNCE"
        } else if (diff < 48 * HOUR_MILLIS) {
            "DÜN"
        } else {
            (diff/DAY_MILLIS).toString() + " GÜN ÖNCE"
        }
    }

    fun getTimeAgoForComments(time: Long): String {
        var time1 = time
        if (time1 < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time1 *= 1000
        }
        val now = System.currentTimeMillis()
        if (time1 > now || time1 <= 0) {
            return "şimdi"
        }
        val diff = now - time1
        return if (diff < 2 * MINUTE_MILLIS) {
            "1d"
        } else if (diff < 50 * MINUTE_MILLIS) {
            (diff / MINUTE_MILLIS) .toString() + "d"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "1s"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + "s"
        } else if (diff < 48 * HOUR_MILLIS) {
            "dün"
        } else {

            (diff / DAY_MILLIS).toString() + "g"
        }
    }
}