@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appgithubapi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appgithubapi.ui.theme.AppgithubapiTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var textName by remember { mutableStateOf("") }
    var githubUserData by remember { mutableStateOf<GithubUserData?>(null) }
    var githubUserDataFollowing by remember { mutableStateOf<List<GithubUserDataFollowing>?>(null) }

    val context = LocalContext.current

    LaunchedEffect(textName) {
        githubUserData = getGithubUser(textName)
        githubUserDataFollowing = getGithubUserFollowing(textName)
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
            label = { Text("Name") },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (githubUserData == null){
            Text(
                text = if (textName.isEmpty()){""}else{"$textName não encontrado"},
                modifier = modifier.align(Alignment.CenterHorizontally)
            )
        }else{
            Card(modifier=Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ){
                        UserInfo(githubUserData!!)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()){
                        Button(onClick = { openExternalLink(context,githubUserData!!.html_url) }) {
                            Text(text = "GitHub")
                        }
                        if (githubUserData!!.blog.length > 0){
                            Button(onClick = { openExternalLink(context,githubUserData!!.blog) }) {
                                Text(text = "Blog")
                            }
                        }
                        if (githubUserData!!.twitter_username != null){
                            Button(onClick = { openExternalLink(context,"https://twitter.com/${githubUserData!!.twitter_username}") }) {
                                Text(text = "Blog")
                            }
                        }
                    }
                    Column {
                        Text(text = "seguindo: ${githubUserData?.following}")
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow{
                            items(githubUserDataFollowing ?: emptyList()) { user ->
                                Card(
                                    modifier = Modifier.padding(0.dp),
                                    onClick = {
                                        openExternalLink(context,user.html_url)
                                    }) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        AsyncImage(
                                            model = user.avatar_url,
                                            contentDescription = "Translated description of what the image contains",
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    width = 2.dp,
                                                    shape = CircleShape,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = user.login)
                                    }
                                }
                            }
                        }
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

@Composable
fun UserInfo(githubUserData:GithubUserData, modifier: Modifier = Modifier){
    AsyncImage(
        model = githubUserData.avatar_url,
        contentDescription = "Translated description of what the image contains",
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            )
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = githubUserData.login,fontSize = 26.sp, fontWeight = FontWeight.Bold)
    Text(text = githubUserData.name, )
    Row() {
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Github Link", tint = MaterialTheme.colorScheme.primary)
        Text(text = githubUserData.location)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = githubUserData.bio)
    Spacer(modifier = Modifier.height(16.dp))
}



suspend fun getGithubUser(username: String): GithubUserData? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()

    try {
        val url = "https://api.github.com/users/$username"
        val request: Request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string()
                val gson = Gson()
                return@withContext gson.fromJson(jsonResponse, GithubUserData::class.java)
            } else {
                // Adiciona log para exibir o código de erro
                println("Erro na requisição: ${response.code}")
                return@withContext null
            }
        }
    } catch (e: Exception) {
        // Adiciona log para exibir a exceção
        e.printStackTrace()
        return@withContext null
    }
}

suspend fun getGithubUserFollowing(username: String): List<GithubUserDataFollowing>? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()

    try {
        val url = "https://api.github.com/users/$username/following"
        val request: Request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string()
                val gson = Gson()
                return@withContext gson.fromJson(jsonResponse, object : TypeToken<List<GithubUserDataFollowing>>() {}.type)
            } else {
                // Adiciona log para exibir o código de erro
                println("Erro na requisição: ${response.code}")
                return@withContext null
            }
        }
    } catch (e: Exception) {
        // Adiciona log para exibir a exceção
        e.printStackTrace()
        return@withContext null
    }
}

fun openExternalLink(context: Context, link: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    context.startActivity(intent)
}

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

data class GithubUserDataFollowing(
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
    var site_admin:Boolean
)