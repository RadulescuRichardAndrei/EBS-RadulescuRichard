import kotlin.random.Random

data class CriteriaTuple<T>(val propertyName: String, val propertyValue: T, val operation: ComparisonOperator ){
    override fun toString(): String {
        return "($propertyName ${operation.name.lowercase()} $propertyValue)"
    }
}

data class Subscription(var tuples: List<CriteriaTuple<*>>){
    override fun toString(): String {
        return "Subscription: $tuples)"
    }
}



data class ComparisonOperatorConfig(var equalProb: Double? = null,
                                    var notEqualProb: Double? = null,
                                    var lessThanProb: Double? = null,
                                    var lessThanOrEqualProb: Double? = null,
                                    var greaterThanProb: Double? = null,
                                    var greaterThanOrEqualProb: Double? = null
    ){
    init {
        var providedProbabilities = listOfNotNull(
            equalProb, notEqualProb, lessThanOrEqualProb, lessThanProb, greaterThanProb, greaterThanOrEqualProb
        ).sum()
        if (providedProbabilities == 0.0)
            equalProb = 1.0
        else {
            equalProb = equalProb?.div(providedProbabilities) ?: 0.0
            notEqualProb = notEqualProb?.div(providedProbabilities) ?: 0.0
            lessThanOrEqualProb = lessThanOrEqualProb?.div(providedProbabilities) ?: 0.0
            lessThanProb = lessThanProb?.div(providedProbabilities) ?: 0.0
            greaterThanProb = greaterThanProb?.div(providedProbabilities) ?: 0.0
            greaterThanOrEqualProb = greaterThanOrEqualProb?.div(providedProbabilities) ?: 0.0
        }

    }

}

fun randomComparisonOperator(conf: ComparisonOperatorConfig): ComparisonOperator{
    val probabilities= listOf(conf.equalProb, conf.notEqualProb, conf.lessThanOrEqualProb,
        conf.lessThanProb, conf.greaterThanProb, conf.greaterThanOrEqualProb)

    val randomSelectionValue= Random.nextDouble()

    var cummulativeProbability= 0.0

    for((index, probability) in probabilities.withIndex()){
        if (probability != null) {
            cummulativeProbability += probability
        }
        if ( randomSelectionValue <= cummulativeProbability){
            return when (index) {
                0 -> ComparisonOperator.EQUALS
                1 -> ComparisonOperator.NOT_EQUALS
                2 -> ComparisonOperator.LESS_THAN
                3 -> ComparisonOperator.LESS_THAN_OR_EQUAL
                4 -> ComparisonOperator.GREATER_THAN
                else -> ComparisonOperator.GREATER_THAN_OR_EQUAL
            }
        }

    }
    return ComparisonOperator.EQUALS
}


class CriteriaTupleConfig<T : Comparable<T>>(
    propertyName: String,
    type: Class<T>,
    valueRange: ClosedRange<T>? = null,
    valueSet: Set<T>? = null,
    val operatorConfig: ComparisonOperatorConfig
) : ValueTupleConfig<T>(propertyName, type, valueRange, valueSet)



class SubscriptionFactory(val configs: List<Pair<CriteriaTupleConfig<*>, Double>>){
    fun generateSubcription(): Subscription{
        var tuples= mutableListOf<CriteriaTuple<*>>()

        for (pair in configs){
            val (conf, prob) = pair
            if ( Random.nextDouble() <= prob){
                var value: Any?= null
                if (conf.valueRange != null)
                    value= conf.valueRange.random(conf.type)
                else if (conf.valueSet != null)
                    value= conf.valueSet.random()
                //here

                val tuple = CriteriaTuple(conf.propertyName, value, randomComparisonOperator(conf.operatorConfig))
                tuples.add(tuple)
            }


        }
        return Subscription(tuples)
    }
}
