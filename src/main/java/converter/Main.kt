package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.math.pow

const val EXIT = "/exit"
const val BACK = "/back"

fun fractAnyToDec(value: String, baseValue: Int): String {
    val letters = ('a'..'z').toList()
    var sum = BigDecimal(0.0)

    for (i in value.indices) {
        val pow = baseValue.toDouble().pow(-(i + 1))
        val mult = if (value[i].isLetter()) {
            letters.indexOf(value[i]) + 10
        } else {
            value[i].digitToInt()
        }

        sum = sum.plus(BigDecimal(mult).multiply(BigDecimal(pow)))
    }
    val result = if (sum.toString().indexOf('.') != -1) { sum.toString().split(".")[1] }
    else { sum.toString() }

    return result
}

fun fractDecToAny(value: String, baseValue: Int): String {
    fun parse(value: BigDecimal): List<String> {
        return value.toString().split(".")
    }

    val letters = ('a'..'z').toList()

    val base = BigDecimal(baseValue)
    var result = ""
    var digit = BigDecimal(value)
    var counter = 0
    val accuracy = 8

    while (counter < accuracy) {

        val midResult = digit * base
        digit = ("0.${parse(midResult)[1]}").toBigDecimal()

        val substituteDigit = parse(midResult)[0].toInt()
        result += if (substituteDigit >= 10) {
            letters[substituteDigit - 10].toString()
        } else {
            substituteDigit
        }
        counter++
    }
    return result
}

fun main() {
    var command = ""
    do {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        command = readLine()!!
        if (command == EXIT) {
            break
        } else {
            val twoBases = command.split(" ").map { it.toInt() }
            do {
                println("Enter number in base ${twoBases[0]} to convert to base ${twoBases[1]} (To go back type /back)")
                command = readLine()!!
                if (command == BACK) {
                    break
                } else {
                    val digitToConvert = command
                    val sbase = twoBases[0]
                    val tbase = twoBases[1]
                    var result = ""
                    val integerAndFraction = digitToConvert.split(".")

                    result = if (sbase == 10) {
                        if (integerAndFraction.size > 1) {
                            val integer = decToAny(integerAndFraction[0].toBigInteger(), tbase.toBigInteger())
                            val fraction = fractDecToAny(integerAndFraction[1],tbase)
                            val middleDigit = BigDecimal("$fraction") .setScale(5, RoundingMode.HALF_UP)

                            "${integer}.${middleDigit.toString().take(5)}"
                        } else {
                            decToAny(digitToConvert.toBigInteger(), tbase.toBigInteger())
                        }

                    } else {
                        if (integerAndFraction.size > 1) {
                            val middleResultInteger = anyToDec(integerAndFraction[0], sbase.toBigInteger())
                            val integer = decToAny(middleResultInteger, tbase.toBigInteger())

                            val middleResultFraction = fractAnyToDec(integerAndFraction[1], sbase)
                            val middleValue = BigDecimal("0.${middleResultFraction}") .setScale(5, RoundingMode.HALF_UP)

                            val fraction = fractDecToAny(middleValue.toString(), tbase)
                            "${integer}.${fraction.take(5)}"

                        } else {
                            val middleResult = anyToDec(digitToConvert, sbase.toBigInteger())
                            decToAny(middleResult, tbase.toBigInteger())
                        }
                    }
                    println("Conversion result: ${result.trimStart('0')}")
                }

            } while (command != BACK)
        }
    } while (command != EXIT)
}

fun decToAny(value: BigInteger, base: BigInteger): String {
    val letters = ('a'..'z').toList()

    fun BigInteger.select() = if (this.toInt() >= 10) {
        letters[this.toInt() - 10].toString()
    } else this.toString()

    val resultDig = mutableListOf<String>()
    var step = value

    do {
        val dig = step.mod(base)
        resultDig.add(dig.select())
        step /= base
    } while (step >= base)

    resultDig.add(step.select())
    resultDig.reverse()

    return resultDig.joinToString("")
}

fun anyToDec(sourceNumber: String, sourceBase: BigInteger): BigInteger {
    val hexDigits = ('a'..'z').toList()

    val incomLenght = sourceNumber.length
    var sum = BigInteger("0")

    for (i in 0 until incomLenght) {
        val power = sourceBase.pow(i)
        val char = sourceNumber[incomLenght - 1 - i]
        val value = if (char.isLetter()) {
            (hexDigits.indexOf(char) + 10).toBigInteger()
        } else {
            char.digitToInt().toBigInteger()
        }
        val digFromStr = value.multiply(power)
        sum = sum.plus(digFromStr)
    }
    return sum
}