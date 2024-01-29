package com.example.appgithubapi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appgithubapi.ui.theme.AppgithubapiTheme
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppgithubapiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var textName by remember { mutableStateOf("") }
    var githubUserData by remember { mutableStateOf<GithubUserData?>(null) }

    LaunchedEffect(textName) {
        // githubUserData = getGithubUser(textName)
        githubUserData = mockUserData
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "GitHub api",
            fontSize = 20.sp,
            modifier = modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = textName,
            onValueChange = { textName = it },
            placeholder = { Text("Name")},
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (githubUserData == null){
            Text(
                text = if (textName.length==0){""}else{"$textName não encontrado"},
                modifier = modifier.align(Alignment.CenterHorizontally)
            )
        }else{
            Card(
                modifier=Modifier
                    .fillMaxSize()
            ) {
                Row {
                    AsyncImage(
                        model = githubUserData?.avatar_url,
                        contentDescription = "Translated description of what the image contains",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column {
                        Text(text = "${githubUserData?.login}",fontSize = 20.sp)
                        Text(text = "${githubUserData?.name}")
                        Text(text = "${githubUserData?.bio}")
                    }
                }
                Row {
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp),
                    ) {
                        Icon(imageVector = Icons.Default., contentDescription = "GitHub")
                    }
                }

            }
        }
    }

}

@Preview(name = "Android greeting")
@Composable
fun PreviewGreeting() {
    Greeting()
}

suspend fun getGithubUser(username:String): GithubUserData?= withContext(Dispatchers.IO) {
    val client = OkHttpClient()

    try {
        val url = "https://api.github.com/users/$username"
        val request: Request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string()
                val gson = Gson()
                Log.d("API_RESPONSE", "carrengando jerson")
                return@withContext gson.fromJson(jsonResponse, GithubUserData::class.java)
            } else {
                return@withContext null
            }
        }
    } catch (e: IOException) {
        return@withContext null
    }
}

val mockUserData = GithubUserData(
    login = "Kiro-tagama",
    id = "59925590",
    node_id = "MDQ6VXNlcjU5OTI1NTkw",
    avatar_url = "https://avatars.githubusercontent.com/u/59925590?v=4",
    gravatar_id = "",
    url = "https://api.github.com/users/Kiro-tagama",
    html_url = "https://github.com/Kiro-tagama",
    followers_url = "https://api.github.com/users/Kiro-tagama/followers",
    following_url = "https://api.github.com/users/Kiro-tagama/following{/other_user}",
    gists_url = "https://api.github.com/users/Kiro-tagama/gists{/gist_id}",
    starred_url = "https://api.github.com/users/Kiro-tagama/starred{/owner}{/repo}",
    subscriptions_url = "https://api.github.com/users/Kiro-tagama/subscriptions",
    organizations_url = "https://api.github.com/users/Kiro-tagama/orgs",
    repos_url = "https://api.github.com/users/Kiro-tagama/repos",
    events_url = "https://api.github.com/users/Kiro-tagama/events{/privacy}",
    received_events_url = "https://api.github.com/users/Kiro-tagama/received_events",
    type = "User",
    site_admin = false,
    name = "Rodrigo Lopes",
    company = null,
    blog = "https://rodrigol.netlify.app/",
    location = "São Paulo - SP",
    email = null,
    hireable = true,
    bio = "..",
    twitter_username = null,
    public_repos = "25",
    public_gists = "0",
    followers = "13",
    following = "31",
    created_at = "2020-01-15T15:03:43Z",
    updated_at = "2024-01-29T12:34:25Z"
)

data class GithubUserData(
    var login:String,
    var id:String,
    var node_id:String,
    var avatar_url:String,
    var gravatar_id:String,
    var url:String,
    var html_url:String,
    var followers_url:String,
    var following_url:String,
    var gists_url:String,
    var starred_url:String,
    var subscriptions_url:String,
    var organizations_url:String,
    var repos_url:String,
    var events_url:String,
    var received_events_url:String,
    var type:String,
    var site_admin:Boolean,
    var name:String,
    var company: String?,
    var blog:String,
    var location:String,
    var email:String?,
    var hireable:Boolean,
    var bio:String,
    var twitter_username:String?,
    var public_repos:String,
    var public_gists:String,
    var followers:String,
    var following:String,
    var created_at:String,
    var updated_at:String
)