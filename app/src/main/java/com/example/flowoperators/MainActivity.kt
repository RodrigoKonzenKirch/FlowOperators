package com.example.flowoperators

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.flowoperators.ui.theme.FlowOperatorsTheme
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val numbersFlow = listOf(1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1).asFlow()
        val otherNumbersFlow = listOf(100, 200, 300, 400, 500).asFlow()

        val lifecycleScope = lifecycleScope
        val numbersFlowCollected = mutableListOf<Int>()
        val otherNumbersFlowCollected = mutableListOf<Int>()
        val numbersAfterFlowOperatorFilterNot = mutableListOf<Int>()
        val numbersAfterFlowOperatorFlowMap = mutableListOf<Int>()
        val numbersAfterFlowOperatorDistinctUntilChanged = mutableListOf<Int>()
        val numbersAfterFlowOperatorDrop = mutableListOf<Int>()
        val numbersAfterFlowOperatorTake = mutableListOf<Int>()
        val numbersAfterFlowOperatorZip = mutableListOf<Int>()

        lifecycleScope.launch {

            numbersFlow.toList(numbersFlowCollected)
            numbersFlow.toList(otherNumbersFlowCollected)

            numbersFlow
                .filterNot { it == 4 }
                .collect{
                    numbersAfterFlowOperatorFilterNot.add(it)
                }

            numbersFlow
                .map {number ->
                    var result = number
                    if (number > 3){
                        result = number * 5
                    }
                    result
                }
                .collect{
                    numbersAfterFlowOperatorFlowMap.add(it)
                }

            numbersFlow
                .distinctUntilChanged()
                .collect{
                    numbersAfterFlowOperatorDistinctUntilChanged.add(it)
                }

            numbersFlow
                .drop(3)
                .collect{
                    numbersAfterFlowOperatorDrop.add(it)
                }

            numbersFlow
                .take(5)
                .collect{
                    numbersAfterFlowOperatorTake.add(it)
                }

            numbersFlow
                .zip(otherNumbersFlow){ numberFlow1, numberFlow2 ->
                    numberFlow1 + numberFlow2
                }
                .collect{
                    numbersAfterFlowOperatorZip.add(it)
                }
        }

        setContent {
            FlowOperatorsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        numbersFlowCollected,
                        otherNumbersFlowCollected,
                        numbersAfterFlowOperatorFilterNot,
                        numbersAfterFlowOperatorFlowMap,
                        numbersAfterFlowOperatorDistinctUntilChanged,
                        numbersAfterFlowOperatorDrop,
                        numbersAfterFlowOperatorTake,
                        numbersAfterFlowOperatorZip,

                        )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    numbersFlowCollected: List<Int>,
    otherNumbersFlowCollected: List<Int>,
    numbersAfterFlowOperatorFilterNot: List<Int>,
    numbersAfterFlowOperatorFlowMap: List<Int>,
    numbersAfterFlowOperatorDistinctUntilChanged: List<Int>,
    numbersAfterFlowOperatorDrop: List<Int>,
    numbersAfterFlowOperatorTake: List<Int>,
    numbersAfterFlowOperatorCombine: List<Int>,
    modifier: Modifier = Modifier
) {

    val scroll = rememberScrollState(0)
    Column {
        TitleScreen(numbersFlowCollected, otherNumbersFlowCollected, modifier)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.verticalScroll(scroll)
        ) {
            OperatorItem(
                title = "Applied Flow operator filterNot (filtered number 4)",
                content = numbersAfterFlowOperatorFilterNot.toString(),
                modifier = modifier
            )

            OperatorItem(
                title = "Applied Flow operator flowMap (numbers bigger than 3 are multiplied by 5)",
                content = numbersAfterFlowOperatorFlowMap.toString(),
                modifier = modifier
            )

            OperatorItem(
                title = "Applied Flow operator distinctUntilChanged",
                content = numbersAfterFlowOperatorDistinctUntilChanged.toString(),
                modifier = modifier
            )

            OperatorItem(
                title = "Applied Flow operator drop(3)",
                content = numbersAfterFlowOperatorDrop.toString(),
                modifier = modifier
            )
            OperatorItem(
                title = "Applied Flow operator take(5)",
                content = numbersAfterFlowOperatorTake.toString(),
                modifier = modifier
            )

            OperatorItem(
                title = "Applied Flow operator zip (sum numbers coming from numbersFlow and otherNumbersFlow)",
                content = numbersAfterFlowOperatorCombine.toString(),
                modifier = modifier
            )

        }
    }
}

@Composable
fun TitleScreen(
    numbersFlowCollected: List<Int>,
    otherNumbersFlowCollected: List<Int>,
    modifier: Modifier
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Text(text = "NumbersFlow collected without applying any operator:")
        Text(text = numbersFlowCollected.toString())

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "OtherNumbersFlow collected without applying any operator:")
        Text(text = otherNumbersFlowCollected.toString())

        Divider(modifier = Modifier.padding(8.dp), thickness = Dp.Hairline)
    }
}

@Composable
fun OperatorItem(
    title: String = "",
    content: String = "",
    modifier: Modifier
){

    Card(
        modifier = modifier.padding(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = title)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = content)
        }
    }
}

