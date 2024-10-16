package ru.application.homemedkit.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.google.zxing.BarcodeFormat
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.MedicineScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ScannerScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import ru.application.homemedkit.R
import ru.application.homemedkit.databaseController.MedicineDatabase
import ru.application.homemedkit.helpers.SNACKS
import ru.application.homemedkit.helpers.viewModelFactory
import ru.application.homemedkit.ui.theme.AppTheme
import ru.application.homemedkit.viewModels.ResponseState
import ru.application.homemedkit.viewModels.ScannerViewModel

@Destination<RootGraph>
@Composable
fun ScannerScreen(
    navigator: DestinationsNavigator,
    context: Context = LocalContext.current,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle
) {
    val dao = MedicineDatabase.getInstance(context).medicineDAO()
    val viewModel = viewModel<ScannerViewModel>(factory = viewModelFactory { ScannerViewModel(dao) })
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val response by viewModel.response.collectAsState(ResponseState.Default)

    var permissionGranted by remember { mutableStateOf(checkCameraPermission(context)) }
    var showRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) permissionGranted = true
            else showRationale = true
        }
    )

    DisposableEffect(LocalLifecycleOwner.current) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionGranted = checkCameraPermission(context)
                showRationale = !permissionGranted
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    AppTheme {
        when (permissionGranted) {
            false -> {
                LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }
                if (showRationale) PermissionDialog(id = R.string.text_request_camera)
            }

            true -> {
                AndroidView(
                    factory = { context ->
                        val scannerView = CodeScannerView(context).apply {
                            frameCornersRadius = 48.dp.value.toInt()
                            frameThickness = 16.dp.value.toInt()
                            isMaskVisible = true
                        }

                        CodeScanner(context, scannerView).apply {
                            setErrorCallback { viewModel.alert = true }
                            setDecodeCallback { result ->
                                if (result.barcodeFormat == BarcodeFormat.DATA_MATRIX)
                                    viewModel.fetchData(context, result.text.substring(1))
                                else viewModel.show = true
                            }

                            startPreview()
                        }

                        scannerView
                    }
                )
            }
        }

        if (viewModel.show) {
            try {
                ResponseState.Errors.valueOf(response.toString()).ordinal
            } catch (e: IllegalArgumentException) {
                ResponseState.Errors.FETCH_ERROR.ordinal
            }.also { Snackbar(it) }

            LaunchedEffect(Unit) {
                delay(2000)
                viewModel.show = false
                navigator.navigate(ScannerScreenDestination)
            }
        }

        if (viewModel.alert)
            AddMedicineDialog(
                navController = navigator,
                cis = state.cis,
                onDismiss = { viewModel.alert = false; navigator.navigate(ScannerScreenDestination) }
            )

        when (response) {
            ResponseState.Default -> {}
            ResponseState.Loading -> LoadingDialog()
            ResponseState.Success -> navigator.navigate(MedicineScreenDestination(state.id))
            ResponseState.Duplicate -> navigator.navigate(
                MedicineScreenDestination(id = state.id, duplicate = true)
            )

            else -> when (response) {
                ResponseState.Errors.NO_NETWORK -> viewModel.alert = true
                else -> viewModel.show = true
            }
        }
    }
}

@Composable
fun Snackbar(id: Int = 0) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(4.dp)),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = LocalContext.current.getString(SNACKS[id]),
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun AddMedicineDialog(
    navController: DestinationsNavigator,
    cis: String,
    onDismiss: () -> Unit,
    context: Context = LocalContext.current,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton({ navController.navigate(MedicineScreenDestination(cis = cis)) }) {
                Text(
                    text = context.resources.getString(R.string.text_yes),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        dismissButton = {
            TextButton(onDismiss) {
                Text(
                    text = context.resources.getString(R.string.text_no),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        title = {
            Text(
                text = context.resources.getString(R.string.text_connection_error),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        text = {
            Text(
                text = context.resources.getString(R.string.manual_add),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        textContentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
fun LoadingDialog() {
    Dialog({}) {
        Box(Modifier.fillMaxSize(), Alignment.Center)
        { CircularProgressIndicator() }
    }
}

@Composable
fun PermissionDialog(context: Context = LocalContext.current, id: Int) {
    Dialog({ Intent(context, MainActivity::class.java).also(context::startActivity) }) {
        ElevatedCard {
            Text(
                text = context.getString(id),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            TextButton(
                onClick = {
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        .also(context::startActivity)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(text = context.getString(R.string.text_grant_permission)) }
        }
    }
}

private fun checkCameraPermission(
    context: Context,
    permission: String = Manifest.permission.CAMERA
): Boolean = checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED