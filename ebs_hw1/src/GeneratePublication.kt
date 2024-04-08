import kotlinx.coroutines.*
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.Executors

val companies = arrayOf("Google", "Facebook", "Amazon")

private const val ITERATIONS = 1_000_000
private const val POOLSIZE = 4

fun initGeneratePub(iterations: Int, isCoroutine: Boolean, isThread: Boolean, poolsize: Int? ): Unit = runBlocking{
    val tupleConf1= ValueTupleConfig<String>(
        propertyName = "company",
        type = String::class.java,
        valueSet = companies.toSet())
    val tupleConf2= ValueTupleConfig<Double>(
        propertyName = "value",
        type = Double::class.java,
        valueRange = 0.0..50.0)
    val tupleConf3= ValueTupleConfig<Double>(
        propertyName = "drop",
        type = Double::class.java,
        valueRange = 0.0..1.0)
    val tupleConf4= ValueTupleConfig<Double>(
        propertyName = "variation",
        type = Double::class.java,
        valueRange = 1.0..20.0)
    val startDate= Calendar.getInstance().apply {
        set(Calendar.YEAR, 2, 1,0,0,0)
    }
    val endDate= Calendar.getInstance().apply {
        set(Calendar.YEAR, 10, 1,0,0,0)
    }
    val tupleConf5= ValueTupleConfig<Date>(
        propertyName = "date",
        type = Date::class.java,
        valueRange = startDate.time..endDate.time)


    val pubFactory = PublicationFactory(listOf(tupleConf1, tupleConf2, tupleConf3, tupleConf4, tupleConf5))
    if (isCoroutine){
        generatePubCoroutine(iterations, poolsize!!, pubFactory)
    } else if (isThread){
        generatePubThread(iterations, poolsize!!, pubFactory)
    }
    else{
        repeat(iterations){
            pubFactory.generatePublication()
            // println(pubFactory.generatePublication())
        }
    }
}
suspend fun generatePubCoroutine(iterations: Int, poolsize: Int, pubFactory: PublicationFactory){
    val supervisorJob = SupervisorJob()
    val handler= CoroutineExceptionHandler{_, exception -> println("Coroutine Exception: $exception")}

    val dispatcher = Executors.newFixedThreadPool(poolsize).asCoroutineDispatcher()

    val scope = CoroutineScope(dispatcher + supervisorJob + handler)
    val coroutine ={
        // println(pubFactory.generatePublication())
        pubFactory.generatePublication()

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
fun generatePubThread(iterations: Int, poolsize: Int,pubFactory: PublicationFactory){
    val executor = Executors.newFixedThreadPool(poolsize)
    val threads = mutableListOf<Thread>()
    repeat(iterations) {
        executor.submit {
            pubFactory.generatePublication()
            //println(pubFactory.generatePublication())
        }
    }

    executor.shutdown()
}
