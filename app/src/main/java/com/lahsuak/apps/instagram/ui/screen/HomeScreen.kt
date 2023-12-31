package com.lahsuak.apps.instagram.ui.screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lahsuak.apps.instagram.R
import com.lahsuak.apps.instagram.models.ApiFailure
import com.lahsuak.apps.instagram.models.BaseState
import com.lahsuak.apps.instagram.ui.components.CenterCircularProgressBar
import com.lahsuak.apps.instagram.ui.components.CircularImage
import com.lahsuak.apps.instagram.ui.components.PostItem
import com.lahsuak.apps.instagram.ui.navigation.NavigationItem
import com.lahsuak.apps.instagram.ui.screen.viewmodel.HomeViewModel
import com.lahsuak.apps.instagram.ui.theme.JetPackComposeBasicTheme
import com.lahsuak.apps.instagram.ui.theme.LIGHT_BLUE
import com.lahsuak.apps.instagram.util.AppConstants.MY_USER_ID
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    navController: NavController,
) {

    val userState = homeViewModel.users.collectAsState()
    val storiesState =
        homeViewModel.stories.collectAsState()
    val postsState =
        homeViewModel.posts.collectAsState()
    val bottomSheet = rememberModalBottomSheetState()
    var isBottomSheetOpened by remember {
        mutableStateOf(false)
    }
    if (isBottomSheetOpened) {
        ModalBottomSheet(
            sheetState = bottomSheet,
            onDismissRequest = {
                isBottomSheetOpened = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Options",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Unfollow",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Go to profile",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "share")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
    when (val state = userState.value) {
        is BaseState.Failed -> {
            when (state.error) {
                is ApiFailure.Unknown -> {
                    Button(onClick = {
                        homeViewModel.getUsers()
                        homeViewModel.getPosts()
                        homeViewModel.getStories()
                    }) {
                        Text("${state.error.error}\nRetry")
                    }
                }
            }
        }

        BaseState.Loading -> {
            CenterCircularProgressBar()
        }

        is BaseState.Success -> {
            val user = state.data.find {
                it.id == MY_USER_ID
            }
            if (user != null) {
                val followings =
                    homeViewModel.getUsersByIds(state.data, user.followingIds + user.id)

                Column(modifier = modifier) {
                    TopAppBar(title = {
                        Text(
                            "Instagram Compose",
                            fontFamily = FontFamily.Cursive,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }, actions = {
                        Row {
                            IconButton(onClick = {
                                navController.navigate(NavigationItem.Notification.route)

                            }) {
                                Icon(
                                    painterResource(id = R.drawable.ic_favorite_border),
                                    contentDescription = null,
                                    Modifier.padding(8.dp)
                                )
                            }
                            IconButton(onClick = {
                                navController.navigate(NavigationItem.ChatList.route)

                            }) {
                                Icon(
                                    painterResource(id = R.drawable.ic_chat),
                                    contentDescription = null,
                                    Modifier.padding(8.dp)
                                )
                            }
                        }
                    })
                    LazyColumn {
                        item {
                            LazyRow(Modifier.padding(vertical = 8.dp)) {
                                item {
                                    Box(
                                        contentAlignment = Alignment.BottomEnd
                                    ) {
                                        CircularImage(
                                            imageUrl = user.profileImage,
                                            isBorderVisible = false,
                                            isNameVisible = true,
                                            name = "Your story",
                                            modifier = Modifier
                                                .padding(start = 16.dp, end = 8.dp)
                                                .clickable {
                                                    navController.navigate(NavigationItem.CreatePost.route)
                                                },
                                        )
                                        Icon(
                                            tint = LIGHT_BLUE,
                                            imageVector = Icons.Default.AddCircle,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(bottom = 18.dp, end = 4.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                when (val s = storiesState.value) {
                                    is BaseState.Failed -> {
                                        // no-op
                                    }

                                    BaseState.Loading -> {
                                        // no-op
                                    }

                                    is BaseState.Success -> {
                                        val stories = s.data.filter { story ->
                                            user.followingIds.any {
                                                story.userId == it
                                            }
                                        }.distinctBy { it.userId }
                                        items(stories) { story ->
                                            val usr = followings.find {
                                                story.userId == it.id
                                            }
                                            if (usr != null) {
                                                CircularImage(
                                                    imageUrl = usr.profileImage,
                                                    isBorderVisible = true,
                                                    isNameVisible = true,
                                                    name = usr.name,
                                                    isAnimated = true,
                                                    modifier = Modifier
                                                        .padding(horizontal = 8.dp)
                                                        .clickable {
                                                            navController.navigate(
                                                                "${NavigationItem.ViewStory.route}/${story.id}/${story.userId}"
                                                            )
                                                        },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        when (val s = postsState.value) {
                            is BaseState.Failed -> {}
                            BaseState.Loading -> {}
                            is BaseState.Success -> {
                                val posts = s.data.filter { post ->
                                    (user.followingIds + MY_USER_ID).any {
                                        post.userId == it
                                    }
                                }
                                items(posts) { post ->
                                    val usr = followings.find {
                                        post.userId == it.id
                                    }
                                    if (usr != null) {
                                        PostItem(post, usr, onImageClick = {
                                            navController.navigate(
                                                "${NavigationItem.Profile.route}/${usr.id}"
                                            )
                                        }) {
                                            isBottomSheetOpened = !isBottomSheetOpened
                                            // open bottom sheet with more options
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                CenterCircularProgressBar()
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DefaultPreview() {
    JetPackComposeBasicTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            val homeViewModel: HomeViewModel = viewModel()

            HomeScreen(
                homeViewModel = homeViewModel,
                navController = rememberNavController()
            )
        }
    }
}