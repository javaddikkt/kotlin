val Int.milliseconds: Time
    get() = Time(this / 1000L, this % 1000)

val Int.seconds: Time
    get() = Time(this.toLong(), 0)

val Int.minutes: Time
    get() = Time(this * 60L, 0)

val Int.hours: Time
    get() = Time(this * 3600L, 0)

operator fun Time.plus(other: Time): Time {
    return toTime(toMils(this.seconds, this.milliseconds) + toMils(other.seconds, other.milliseconds))
}

operator fun Time.minus(other: Time): Time {
    require(this.seconds > other.seconds || (this.seconds == other.seconds && this.milliseconds >= other.milliseconds))
    return toTime(toMils(this.seconds, this.milliseconds) - toMils(other.seconds, other.milliseconds))
}

operator fun Time.times(times: Int): Time {
    return toTime(toMils(this.seconds, this.milliseconds) * times)
}

fun toMils(secs: Long, mils: Int): Long {
    return secs * 1000 + mils.toLong()
}

fun toTime(mils: Long): Time {
    return Time(mils / 1000, (mils % 1000).toInt())
}
