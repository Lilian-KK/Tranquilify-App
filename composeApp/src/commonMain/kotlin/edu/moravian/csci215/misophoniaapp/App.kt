package edu.moravian.csci215.misophoniaapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import edu.moravian.csci215.misophoniaapp.screens.*
import edu.moravian.csci215.misophoniaapp.screens.headphones.HeadphonesConnection
import edu.moravian.csci215.misophoniaapp.screens.headphones.HeadphonesConnectionScreen
import edu.moravian.csci215.misophoniaapp.screens.headphones.HeadphonesNearby
import edu.moravian.csci215.misophoniaapp.screens.headphones.HeadphonesNearbyScreen
import edu.moravian.csci215.misophoniaapp.screens.headphones.HeadphonesSelect
import edu.moravian.csci215.misophoniaapp.screens.headphones.HeadphonesSelectScreen
import edu.moravian.csci215.misophoniaapp.screens.headphones.HeadphonesSettings
import edu.moravian.csci215.misophoniaapp.screens.headphones.HeadphonesSettingsScreen
import edu.moravian.csci215.misophoniaapp.headphones_data.HeadphonesRepository
import edu.moravian.csci215.misophoniaapp.screens.login.*
import edu.moravian.csci215.misophoniaapp.screens.registration.*
import edu.moravian.csci215.misophoniaapp.server_data.*
import edu.moravian.csci215.misophoniaapp.survey_data.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import misophoniaapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import responseValidator

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App(repository: SurveyRepository, tokenStorage: TokenStorage) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val headphonesRepository = remember { HeadphonesRepository() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoaded by remember {mutableStateOf<Boolean>(false)}
    var isLoggedIn by remember {mutableStateOf<Boolean>(false)}

    val httpClient = remember {
        HttpClient(CIO) {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTP
                    //nora note: this host is only for the android studio emulator!! change the ip for other devices
                    host = "10.0.2.2"
                    port = 8000
                }
                contentType(ContentType.Application.Json)
            }
            expectSuccess = true
            HttpResponseValidator { handleResponseExceptionWithRequest(::responseValidator) }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 15_000
            }
            install(Auth) {
                bearer {
                    loadTokens { tokenStorage.loadTokens() }
                    refreshTokens {
                        val refreshToken = oldTokens?.refreshToken ?: return@refreshTokens null

                        try {
                            val response = refresh(client, refreshToken)
                            tokenStorage.storeTokens(response.access_token, response.refresh_token)
                            BearerTokens(response.access_token, response.refresh_token)
                        } catch (exception: Exception) {
                            tokenStorage.wipeTokens()
                            null
                        }
                    }
                    sendWithoutRequest { request ->
                        //todo: confirm with server-side which endpoints do not require a provided bearer token
                        val endpointsToSkip = listOf("/login", "/health", "/refresh", "/user")
                        // Do NOT send the header if targeting the auth/refresh endpoint
                        //!request.url.toString().contains("/login")
                        !endpointsToSkip.any { request.url.toString().contains(it) }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        isLoggedIn = tokenStorage.refreshToken.first() != null && tokenStorage.accessToken.first() != null
        println("isLoggedIn:" + tokenStorage.refreshToken.first())
        isLoaded = true
    }

    if (!isLoaded) {
        LoadingScreen()
        return
    }

    val currentScreen = navBackStackEntry?.destination?.route
    println("currentscreen:" + currentScreen)
    val screensWithBottomBar = listOf(AppSettings.toString(), HeadphonesSelect.toString(), "HeadphonesSettings", Hub.toString(), SurveyHistory.toString(), WifiNetworks.toString())
    println(screensWithBottomBar)
    val hasBottomBar = screensWithBottomBar.any { currentScreen?.contains(it) == true || currentScreen?.contains("ViewSurvey") == true}

    MaterialTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (hasBottomBar) {
                    BottomAppBar(
                        actions = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton({ navController.navigate(Hub) }, Modifier.size(48.dp)) {
                                    Icon(
                                        painterResource(if (currentScreen?.contains(Hub.toString()) == true) Res.drawable.ic_home_selected else Res.drawable.ic_home),
                                        stringResource(Res.string.home_button),
                                        Modifier.size(48.dp),
                                        Color.Unspecified,
                                    )
                                }
                                IconButton({ navController.navigate(HeadphonesSelect) }, Modifier.size(48.dp)) {
                                    Icon(
                                        painterResource(if (currentScreen?.contains("Headphones") == true) Res.drawable.ic_headphones_selected else Res.drawable.ic_headphones),
                                        stringResource(Res.string.hp_settings_button),
                                        Modifier.size(48.dp),
                                        Color.Unspecified,
                                    )
                                }
                                IconButton({ navController.navigate(SurveyHistory) }, Modifier.size(48.dp)) {
                                    Icon(
                                        painterResource(Res.drawable.ic_clipboard),
                                        stringResource(Res.string.survey_history_button),
                                        Modifier.size(48.dp),
                                        Color.Unspecified,
                                    )
                                }
                                IconButton({ navController.navigate(AppSettings) }, Modifier.size(48.dp)) {
                                    Icon(
                                        painterResource(Res.drawable.ic_settings),
                                        stringResource(Res.string.app_settings_button),
                                        Modifier.size(48.dp),
                                        Color.Unspecified,
                                    )
                                }
                            }
                        },
                        // #9263AA at 75%, from the Figma BottomBar
                        containerColor = AppColors.Button.copy(alpha = 0.75f),
                        contentPadding = PaddingValues(0.dp),
                    )
                }
            },
        ) { innerPadding ->
            NavHost(
                navController,
                // keep content above the bottom bar on the screens that show it
                modifier = Modifier.padding(bottom = if (hasBottomBar) innerPadding.calculateBottomPadding() else 0.dp),
                startDestination =
                    if (isLoggedIn == true) Hub else Setup
            ) {
                composable<Setup> {
                    SetupScreen(
                        onLogin = { navController.navigate(BaseLogin) },
                        onCreateAccount = { navController.navigate(CreateAccount) },
                    )
                }
                composable<BaseLogin> {
                    BaseLoginScreen(
                        showSnackbar = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        toCodeLogin = { username: String -> navController.navigate(CodeLogin(username)) },
                        toPasswordLogin = { username: String -> navController.navigate(PasswordLogin(username)) },
                        goBack = { navController.navigateUp() }
                    )
                }
                composable<CodeLogin> { navBackStackEntry ->
                    val username = navBackStackEntry.toRoute<CodeLogin>().username
                    CodeLoginScreen(
                        username = username,
                        goBack = { navController.navigateUp() },
                        storeTokens = { accessToken: String, refreshToken: String ->
                            tokenStorage.storeTokens(accessToken, refreshToken)
                        },
                        showSnackbar = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        toHub = { navController.navigate(Hub) },
                        sendCode = { username: String ->
                            sendLoginCode(httpClient, username)
                        },
                        onLogin = { username: String, login_method: String, login_value: String ->
                            logIn(httpClient, username, login_method, login_value)
                        }
                    )
                }
                composable<PasswordLogin> { navBackStackEntry ->
                    val username = navBackStackEntry.toRoute<PasswordLogin>().username
                    PasswordLoginScreen(
                        username = username,
                        goBack = { navController.navigateUp() },
                        storeTokens = { accessToken: String, refreshToken: String ->
                            tokenStorage.storeTokens(accessToken, refreshToken)
                        },
                        showSnackbar = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        toHub = { navController.navigate(Hub) },
                        toForgotPassword = { username: String -> navController.navigate(ForgotPassword(username)) },
                        onLogin = { username: String, login_method: String, login_value: String ->
                            logIn(httpClient, username, login_method, login_value)
                        }
                    )
                }
                composable<ForgotPassword> { navBackStackEntry ->
                    val username = navBackStackEntry.toRoute<ForgotPassword>().username
                    ForgotPasswordScreen(
                        username = username,
                        goBack = { navController.navigateUp() },
                        resetPassword = { username: String, code: String, newPassword: String ->
                            resetPassword(httpClient, username, code, newPassword)
                        },
                        showSnackbar = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        sendCode = {username: String -> sendLoginCode(httpClient, username)},
                        storeTokens = { accessToken: String, refreshToken: String ->
                            tokenStorage.storeTokens(accessToken, refreshToken)
                        },
                        toHub = { navController.navigate(Hub) },
                        onLogin = { username: String, login_method: String, login_value: String ->
                            logIn(httpClient, username, login_method, login_value)
                        }
                    )
                }
                composable<CreateAccount> {
                    CreateAccountScreen(
                        showSnackbar = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        goBack = { navController.navigateUp() },
                        sendCode = { phoneNumber: String ->
                            sendRegisterCode(httpClient, phoneNumber)
                        },
                        toVerifyPhoneNumber = { phoneNumber: String, username: String, password: String ->
                            navController.navigate(VerifyPhoneNumber(phoneNumber, username, password))
                        }
                    )
                }
                composable<VerifyPhoneNumber> { navBackStackEntry ->
                    val phoneNumber = navBackStackEntry.toRoute<VerifyPhoneNumber>().phoneNumber
                    val username = navBackStackEntry.toRoute<VerifyPhoneNumber>().username
                    val password = navBackStackEntry.toRoute<VerifyPhoneNumber>().password
                    VerifyPhoneNumberScreen(
                        username = username,
                        phoneNumber = phoneNumber,
                        showSnackbar = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        goBack = { navController.navigateUp() },
                        toHub = { navController.navigate(Hub)},
                        createAccount = { code: String ->
                            createAccount(httpClient, phoneNumber, code, username, password) },
                        sendCode = { phoneNumber: String ->
                            sendRegisterCode(httpClient, phoneNumber)
                        },
                    )
                }
                composable<Hub> {
                    HubScreen(
                        // todo: pass the paired headphones' battery level once headphone pairing exists
                        hasHeadphones = headphonesRepository.headphones.isNotEmpty(),
                        headphoneBatteryPercent = headphonesRepository.headphones.firstOrNull()?.batteryPercent,
                        onPairHeadphones = { navController.navigate(HeadphonesSelect) },
                        onSurveySelected = { surveyType ->
                            navController.navigate(SurveyCompanion(surveyType))
                        },
                    )
                }
                composable<HeadphonesSelect> {
                    HeadphonesSelectScreen(
                        headphones = headphonesRepository.headphones,
                        onHeadphonesSelected = { headphones ->
                            navController.navigate(HeadphonesSettings(headphones.id))
                        },
                        // todo: go through BLE pairing (HeadphonesNearby) once it exists
                        onAddHeadphones = { headphonesRepository.add() },
                    )
                }
                composable<HeadphonesSettings> { navBackStackEntry ->
                    val headphonesId = navBackStackEntry.toRoute<HeadphonesSettings>().headphonesId
                    HeadphonesSettingsScreen(
                        headphones = headphonesRepository.get(headphonesId),
                        onRename = { newName -> headphonesRepository.rename(headphonesId, newName) },
                        onWifiSettings = { navController.navigate(WifiNetworks) },
                        onDelete = {
                            headphonesRepository.delete(headphonesId)
                            navController.popBackStack()
                        },
                        goBack = { navController.popBackStack() },
                    )
                }
                composable<SurveyCompanion> { navBackStackEntry ->
                    val surveyType = navBackStackEntry.toRoute<SurveyCompanion>().surveyType
                    SurveyScreen(repository, surveyType) {
                        navController.navigate(SurveyHistory)
                    }
                }
                composable<SurveyHistory> {
                    SurveyHistoryScreen(repository) { surveyId: Long, surveyType: SurveyType ->
                        navController.navigate(ViewSurvey(surveyId, surveyType))
                    }
                }
                composable<ViewSurvey> { navBackStackEntry ->
                    val surveyId = navBackStackEntry.toRoute<ViewSurvey>().surveyId
                    val surveyType = navBackStackEntry.toRoute<ViewSurvey>().surveyType
                    ViewSurveyScreen(surveyId, surveyType, repository)
                }
                composable<AppSettings> {
                    AppSettingsScreen(
                        showSnackbar = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        clearTokens = { tokenStorage.wipeTokens() },
                        logOut = { logout(httpClient, tokenStorage.refreshToken.first().toString()) }, //keep an eye on this one make sure its storing properly
                        toSetup = { navController.navigate(Setup) },
                        changePassword = { currentPassword: String, newPassword: String ->
                            changePassword(httpClient, currentPassword, newPassword)
                        },
                        changePhoneNumber = { newPhoneNumber: String, code: String ->
                            changePhoneNumber(httpClient, newPhoneNumber, code)
                        }
                    )
                }
                composable<WifiNetworks> {
                    WifiNetworksScreen()
                }
                composable<HeadphonesConnection> {
                    HeadphonesConnectionScreen()
                }
                composable<HeadphonesNearby> {
                    HeadphonesNearbyScreen()
                }
            }
        }
    }
}