package app.materialtok

import android.os.Build
import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private data class Region(val name: String, val code: String, val flag: String)
private val regions = listOf(
    Region("Ukraine", "UA", "🇺🇦"), Region("Netherlands", "NL", "🇳🇱"),
    Region("United States", "US", "🇺🇸"), Region("United Kingdom", "GB", "🇬🇧"),
    Region("Germany", "DE", "🇩🇪"), Region("France", "FR", "🇫🇷"),
    Region("Spain", "ES", "🇪🇸"), Region("Italy", "IT", "🇮🇹"),
    Region("Sweden", "SE", "🇸🇪"), Region("Poland", "PL", "🇵🇱")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTokApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialTokApp() {
    val context = LocalContext.current
    val dark = isSystemInDarkTheme()
    val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (dark) darkColorScheme() else lightColorScheme()

    val prefs = remember {
        context.getSharedPreferences("materialtok_preferences", Context.MODE_PRIVATE)
    }
    var selected by remember { mutableStateOf(prefs.getString("region", "NL") ?: "NL") }
    var enabled by remember { mutableStateOf(prefs.getBoolean("region_enabled", false)) }
    var pickerOpen by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = colors) {
        Scaffold(topBar = {
            TopAppBar(title = { Text("MaterialTok") })
        }) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text("MaterialTok Settings", style = MaterialTheme.typography.headlineSmall)
                    Text("A fresh Material You settings prototype.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item {
                    var dynamic by remember { mutableStateOf(true) }
                    SettingCard(
                        Icons.Rounded.ColorLens,
                        "Dynamic colors",
                        "Use your Android wallpaper colors",
                        trailing = { Switch(checked = dynamic, onCheckedChange = { dynamic = it }) }
                    )
                }
                item {
                    SettingCard(Icons.Rounded.SimCard, "SIM information", "SIM details are not modified by this app")
                }
                item {
                    val regionName = regions.firstOrNull { it.code == selected }?.name ?: "Netherlands"
                    SettingCard(
                        Icons.Rounded.Language,
                        "Region preferences",
                        if (enabled) "Preferred content region: $regionName" else "Choose a preferred content region",
                        onClick = { pickerOpen = true }
                    )
                }
                item {
                    SettingCard(Icons.Rounded.Info, "About MaterialTok", "Version 1.1.0 · Android prototype")
                }
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "This is a locally saved content preference only. It does not change your SIM, device location or IP address, and cannot guarantee a different TikTok feed. MaterialTok is independent and is not affiliated with TikTok.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        if (pickerOpen) {
            AlertDialog(
                onDismissRequest = { pickerOpen = false },
                title = { Text("Region preference") },
                text = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Enable preference", style = MaterialTheme.typography.titleSmall)
                                Text("Save a preferred content region", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = enabled,
                                onCheckedChange = {
                                    enabled = it
                                    prefs.edit().putBoolean("region_enabled", it).apply()
                                }
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Country", style = MaterialTheme.typography.titleSmall)
                        regions.forEach { region ->
                            Row(
                                Modifier.fillMaxWidth().clickable {
                                    selected = region.code
                                    enabled = true
                                    prefs.edit().putString("region", region.code)
                                        .putBoolean("region_enabled", true).apply()
                                }.padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(region.flag)
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(region.name)
                                    Text(region.code, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                RadioButton(selected == region.code, onClick = {
                                    selected = region.code
                                    enabled = true
                                    prefs.edit().putString("region", region.code)
                                        .putBoolean("region_enabled", true).apply()
                                })
                            }
                        }
                        Text(
                            "Russia is excluded. This preference does not spoof SIM or network identifiers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { pickerOpen = false }) { Text("Done") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        enabled = false
                        prefs.edit().putBoolean("region_enabled", false).apply()
                        pickerOpen = false
                    }) { Text("Disable") }
                }
            )
        }
    }
}

@Composable
private fun SettingCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            trailing?.invoke()
        }
    }
}
