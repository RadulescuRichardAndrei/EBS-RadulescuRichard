import kotlinx.coroutines.*
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset
import java.util.Calendar
import java.util.Date
import java.util.concurrent.Executors
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.system.measureTimeMillis

private const val POOLSIZE = 4

fun plotGeneratePubication(function: (Int, Boolean, Boolean, Int?) -> Unit){
    var iterations= mutableListOf<Int>();
    var timeSimple= mutableListOf<Long>();
    var timeCoroutine= mutableListOf<Long>();
    var timeThread= mutableListOf<Long>();

    for(iteration: Int in 10_000..1_000_000 step 10_000){
        var timeInMillis= measureTimeMillis {
            function(iteration, false, false, null)
            //initGeneratePub(iteration, false, false, null)
        }
        var timeInMillis2= measureTimeMillis {
            function(iteration, true, false, POOLSIZE)
        }
        var timeInMillis3= measureTimeMillis {
            function(iteration, false, true, POOLSIZE)
        }
        iterations.add(iteration)
        timeSimple.add(timeInMillis)
        timeCoroutine.add(timeInMillis2)
        timeThread.add(timeInMillis3)

    }
    val dataset = DefaultCategoryDataset()

    // Add data to the dataset
    for (i in iterations.indices) {
        dataset.addValue(timeSimple[i].toDouble(), "No threads/coroutines", iterations[i].toString())
        dataset.addValue(timeCoroutine[i].toDouble(), "Coroutines", iterations[i].toString())
        dataset.addValue(timeThread[i].toDouble(), "Threads", iterations[i].toString())
    }

    // Create the chart
    val chart = ChartFactory.createLineChart(
        "Execution Times vs Iterations", // Chart title
        "Iterations", // X-Axis Label
        "Time (ms)", // Y-Axis Label
        dataset, // Dataset
        PlotOrientation.VERTICAL, // Plot orientation
        true, // Show legend
        true, // Use tooltips
        false // Use URLs
    )

    // Display the chart in a Swing frame
    SwingUtilities.invokeLater {
        val frame = JFrame("Line Chart Example")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val chartPanel = ChartPanel(chart)
        frame.contentPane.add(chartPanel)
        frame.setSize(800, 600)
        frame.isVisible = true
    }
}
fun main() {
    //plotGeneratePubication(::initGeneratePub)
    plotGeneratePubication(::intiGenerateSub)
}
