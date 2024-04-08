enum class ComparisonOperator {
    EQUALS,
    NOT_EQUALS,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL
}

fun <T : Comparable<T>> performComparison(value1: T, value2: T, operator: ComparisonOperator): Boolean {
    return when (operator) {
        ComparisonOperator.EQUALS -> value1 == value2
        ComparisonOperator.NOT_EQUALS -> value1 != value2
        ComparisonOperator.LESS_THAN -> value1 < value2
        ComparisonOperator.LESS_THAN_OR_EQUAL -> value1 <= value2
        ComparisonOperator.GREATER_THAN -> value1 > value2
        ComparisonOperator.GREATER_THAN_OR_EQUAL -> value1 >= value2
    }
}

