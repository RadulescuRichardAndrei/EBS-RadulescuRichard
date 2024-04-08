fun generateSub(iterations: Int) {

    val operatorConfig = ComparisonOperatorConfig(
        0.4, 0.0, 0.1,
        0.1, 0.2, 0.2
    )
    val tupleConf1= CriteriaTupleConfig<String>(
        propertyName = "company",
        type = String::class.java,
        valueSet = companies.toSet(),
        operatorConfig = operatorConfig
    )
    val tupleConf2= CriteriaTupleConfig<Double>(
        propertyName = "value",
        type = Double::class.java,
        valueRange = 0.0..50.0,
        operatorConfig= operatorConfig
    )
    val tupleConf3= CriteriaTupleConfig<Double>(
        propertyName = "variation",
        type = Double::class.java,
        valueRange = 0.0..1.0,
        operatorConfig= operatorConfig
    )
    val subFactory = SubscriptionFactory(listOf(tupleConf1 to 0.9, tupleConf2 to 0.5, tupleConf3 to 0.5))

    repeat(iterations){
        println(subFactory.generateSubcription())
    }
}