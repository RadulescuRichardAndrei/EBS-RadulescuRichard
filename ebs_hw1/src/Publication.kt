import java.util.Date

data class ValueTuple<T>(val propertyName: String, val propertyValue: T){
    override fun toString(): String {
        return "$propertyName=$propertyValue"
    }
}

data class Publication(var tuples: List<ValueTuple<*>>){
    override fun toString(): String {
        return "Publication(tuples=$tuples)"
    }
}


open class ValueTupleConfig<T : Comparable<T>>(val propertyName: String, val type: Class<T>,
                                               val valueRange: ClosedRange<T>? = null,
                                               val valueSet: Set<T>? = null){
    init {
        require(valueRange != null || valueSet != null) { "Either valueRange or valueSet must be provided" }
    }
}

class PublicationFactory(val configs: List<ValueTupleConfig<*>>){
    fun generatePublication(): Publication{
        val tuples= mutableListOf<ValueTuple<*>>()

        for (conf in configs){
            var value: Any? = null

            if (conf.valueRange != null ){
                if(conf.type == Date::class.java)
                    value = randomDate(conf.valueRange.start, conf.valueRange.endInclusive)
                else
                    value= conf.valueRange.random(conf.type)
            }
            else if (conf.valueSet != null)
                value=  conf.valueSet.random()
            val tuple = ValueTuple(conf.propertyName, value)
            tuples.add(tuple)
        }

        return Publication(tuples)
    }
}