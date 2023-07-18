package com.example.androweather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*


import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.androweather.dataclasses.DataModel
import com.example.androweather.ui.theme.Blue
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager


@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainCard(dayslist : MutableState<List<DataModel>>,
             currentDay: MutableState<DataModel>,
             onClickSync: () -> Unit,
             onClickSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth(),
            backgroundColor = Blue)
        {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentDay.value.time,
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 8.dp
                        ),
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.White
                    )

                    AsyncImage(
                        model = "https:" + currentDay.value.icon,
                        contentDescription = "im2",
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                end = 8.dp
                            )
                            .size(35.dp)
                    )
                    IconButton(
                        onClick = {
                            onClickSearch.invoke()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_search_24),
                            contentDescription = "im3",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = {
                            onClickSync.invoke()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_cloud_sync_24),
                            contentDescription = "im4",
                            tint = Color.White
                        )
                    }
                }
                Text(
                    text = currentDay.value.city,
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White
                )
                Text(
                    text = if (currentDay.value.currentTemp.isNotEmpty())
                        currentDay.value.currentTemp.toFloat().toInt().toString() + "ºC"
                    else currentDay.value.maxTemp.toFloat().toInt().toString() +
                            "ºC/${currentDay.value.minTemp.toFloat().toInt()}ºC",
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.White
                )
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )


                HorizontalPager(
                    count = 1,
                    modifier = Modifier.weight(1.0f)
                ) {

                LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(
                            dayslist.value
                        ) { _, item ->
                            ListItem(item, currentDay)
                        }
                    }


                }
            }
        }
    }
}
//@Preview(showBackground = true)
//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun TabLayout(){
//    val tabList = listOf("По часам", "По дням")
//    val pagerState = rememberPagerState()
//    val tabIndex = pagerState.currentPage
//    val coroutineScope = rememberCoroutineScope()
//
//    Column(
//        modifier = Modifier
//            .padding(
//                start = 5.dp,
//                end = 5.dp
//            )
//            .clip(RoundedCornerShape(5.dp))
//    ) {
//        TabRow(
//            selectedTabIndex = tabIndex,
//            indicator = { pos ->
//                TabRowDefaults.Indicator(
//                    Modifier.pagerTabIndicatorOffset(pagerState, pos)
//                )
//            },
//            backgroundColor = Blue,
//            contentColor = Color.White
//        ) {
//
//            tabList.forEachIndexed{index, text ->
//                Tab(
//                    selected = false,
//                    onClick = {
//                        coroutineScope.launch {
//                            pagerState.animateScrollToPage(index)
//                        }
//                    },
//                    text = {
//                        Text(text = text)
//                    }
//                )
//            }
//        }
//
//    }
//}