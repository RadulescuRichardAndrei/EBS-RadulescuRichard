import kotlinx.coroutines.*
import java.util.concurrent.Executors

fun intiGenerateSub(iterations: Int, isCoroutine: Boolean, isThread: Boolean, poolsize: Int? ):  Unit = runBlocking{

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
        //println(subFactory.generateSubcription())
        subFactory.generateSubcription()
    }
    if (isCoroutine){
        generateSubCoroutine(iterations, poolsize!!, subFactory)
    } else if (isThread){
        generateSubThread(iterations, poolsize!!, subFactory)
    }
    else{
        repeat(iterations){
            subFactory.generateSubcription()
            // println(pubFactory.generatePublication())
        }
    }
}


suspend fun generateSubCoroutine(iterations: Int, poolsize: Int, pubFactory: SubscriptionFactory){
    val supervisorJob = SupervisorJob()
    val handler= CoroutineExceptionHandler{_, exception -> println("Coroutine Exception: $exception")}

    val dispatcher = Executors.newFixedThreadPool(poolsize).asCoroutineDispatcher()

    val scope = CoroutineScope(dispatcher + supervisorJob + handler)
    val coroutine ={
        // println(pubFactory.generatePublication())
        pubFactory.generateSubcription()

    }
    repeat(iterations){
        scope.launch {
            coroutine()
        }
    }
    scope.coroutineContext[Job]?.children?.forEach { it.join() }
    scope.coroutineContext[Job]?.invokeOnCompletion { supervisorJob.cancel()
    }
}

fun generateSubThread(iterations: Int, poolsize: Int,pubFactory: SubscriptionFactory){
    val executor = Executors.newFixedThreadPool(poolsize)
    val threads = mutableListOf<Thread>()
    repeat(iterations) {
        executor.submit {
            pubFactory.generateSubcription()
            //println(pubFactory.generatePublication())
        }
    }

    executor.shutdown()
}
