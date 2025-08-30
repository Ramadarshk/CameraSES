package max.project.camerases.util


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import max.project.camerases.R


@Composable
fun BottomBar(modifier: Modifier, shutterClick: () -> Unit={}) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .height(100.dp)
            .background(
                color = Color.Black.copy(alpha = .5f),
                shape = androidx.compose.foundation.shape.CircleShape
            ),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = shutterClick,
            modifier = Modifier.size(100.dp)
        ){
            Icon(painterResource(R.drawable.union), contentDescription = "Shutter",Modifier
                .size(90.dp)
                .padding(5.dp),
                tint = Color.White) }
    }
}

@Composable
fun TopBar(modifier: Modifier = Modifier,onBack:()->Unit={}){
    Row(
        modifier = modifier
            .padding(16.dp)
            .background(
                color = Color.Black.copy(alpha = .5f),
                shape = androidx.compose.foundation.shape.CircleShape
            ),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = onBack,
            modifier = Modifier
        ){
            Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Shutter",Modifier
                .size(90.dp)
                .padding(5.dp)
            ,tint = Color.White
            )
        }
    }
}
