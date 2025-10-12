package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInitialThemeState(requireContext())
    }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.dual_color4))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        Text(
            "Настройки",
            color = colorResource(id = R.color.dual_color),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                "Тёмная тема",
                color = colorResource(id = R.color.dual_color),
                modifier = Modifier.padding(start = 20.dp)
            )

            CustomSwitch2(
                checked = uiState?.isDarkThemeEnabled ?: false,
                onCheckedChange = { checked ->
                    viewModel.onDarkThemeSwitched(checked)
                }
            )

        }

        Button(
            onClick = { viewModel.onShareAppClicked() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.dual_color4),
                contentColor = Color.Black
            ),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Поделиться приложением",
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = colorResource(id = R.color.dual_color)
                )
                Icon(
                    painter = painterResource(id = R.drawable.share),
                    contentDescription = "Share icon",
                    modifier = Modifier.align(Alignment.CenterEnd),
                    tint = colorResource(id = R.color.dual_color2)
                )
            }
        }

        Button(
            onClick = { viewModel.onShareAppClicked() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.dual_color4),
                contentColor = Color.Black
            ),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Написать в поддержку",
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = colorResource(id = R.color.dual_color)
                )
                Icon(
                    painter = painterResource(id = R.drawable.support),
                    contentDescription = "Share icon",
                    modifier = Modifier.align(Alignment.CenterEnd),
                    tint = colorResource(id = R.color.dual_color2)
                )
            }
        }

        Button(
            onClick = { viewModel.onShareAppClicked() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.dual_color4),
                contentColor = Color.Black
            ),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Пользовательское соглашение",
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = colorResource(id = R.color.dual_color)
                )
                Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = "Share icon",
                    modifier = Modifier.align(Alignment.CenterEnd),
                    tint = colorResource(id = R.color.dual_color2)
                )
            }
        }
    }
}


@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val trackWidth = 50.dp
    val trackHeight = 24.dp
    val thumbDiameter = 20.dp

    Box(
        modifier = Modifier
            .size(width = trackWidth, height = trackHeight)
            .clickable { onCheckedChange(!checked) }
            .background(
                color = if (checked) Color.Green else Color.Gray,
                shape = RoundedCornerShape(percent = 50)
            )
    ) {
        Box(
            modifier = Modifier
                .size(thumbDiameter)
                .offset(
                    x = if (checked) (trackWidth - thumbDiameter) else 0.dp,
                    y = (trackHeight - thumbDiameter) / 2
                )
                .background(Color.White, CircleShape)
                .shadow(2.dp, CircleShape)
        )
    }
}

@Composable
fun CustomSwitch2(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val trackWidth = 40.dp
    val trackHeight = 16.dp
    val thumbDiameter = 22.dp

    Box(
        modifier = Modifier
            .size(width = trackWidth, height = thumbDiameter)
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(width = trackWidth, height = trackHeight)
                .align(Alignment.Center)
                .background(


                    color = if (checked) colorResource(id = R.color.track_checked) else colorResource(
                        id = R.color.track_unchecked
                    ),
                    shape = RoundedCornerShape(percent = 50)
                )
        )

        Box(
            modifier = Modifier
                .size(thumbDiameter)
                .align(Alignment.CenterStart)
                .offset(
                    x = if (checked) (trackWidth - thumbDiameter) else 0.dp
                )
                .background(
                    color = if (checked) colorResource(id = R.color.thumb_checked) else colorResource(
                        id = R.color.grey
                    ),
                    shape = CircleShape

                ),

        )
    }
}

