package com.example.foodordering.screens.itemsList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodordering.R
import com.example.foodordering.domain.FoodModel

@Composable
fun ItemsList(
    items: List<FoodModel>,
    onItemsClick: (FoodModel) -> Unit,
    onAddToCart: (FoodModel) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        itemsIndexed(items) { _, item ->
            Items(
                item = item,
                onItemsClick = { onItemsClick(item) },
                onAddToCart = { onAddToCart(item) }
            )
        }
    }
}

@Preview
@Composable
fun ItemsListPreview() {
    val items = listOf(
        FoodModel(
            Title = "Pizza",
            Price = 12.5,
            Star = 4.5
        ),
        FoodModel(
            Title = "Burger",
            Price = 8.0,
            Star = 4.2
        ),
        FoodModel(
            Title = "Salad",
            Price = 7.0,
            Star = 4.8
        )
    )
    ItemsList(items = items, onItemsClick = {}, onAddToCart = {})
}

@Composable
fun Items(
    item: FoodModel,
    onItemsClick: () -> Unit,
    onAddToCart: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = colorResource(id = R.color.black3),
                shape = RoundedCornerShape(10.dp)
            )
            .wrapContentHeight()
            .clickable { onItemsClick() }
    ) {
        FoodImage(item = item)
        FoodDetail(item = item, onAddToCart = onAddToCart)
    }
}

@Composable
fun FoodImage(item: FoodModel) {
    AsyncImage(
        model = item.ImagePath,
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = colorResource(id = R.color.white),
                shape = RoundedCornerShape(10.dp)
            ),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun RowScope.FoodDetail(
    item: FoodModel,
    onAddToCart: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(start = 8.dp)
            .weight(1f)
            .fillMaxWidth()
    ) {
        Text(
            text = item.Title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.white),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
        )
        TimingRow(item.TimeValue)
        RatingBarRow(item.Star)
        PriceRow(
            price = item.Price,
            onAddToCart = onAddToCart
        )
    }
}

@Composable
fun PriceRow(
    price: Double,
    onAddToCart: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
    ) {
        Text(
            text = "$$price",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "+ Add",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(
                    color = colorResource(id = R.color.green),
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable { onAddToCart() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun RatingBarRow(star: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.star),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
        )
        Text(
            text = star.toString(),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TimingRow(timeValue: Int) {
    Row(
        modifier = Modifier
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.time),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
        )
        Text(
            text = "$timeValue min",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}