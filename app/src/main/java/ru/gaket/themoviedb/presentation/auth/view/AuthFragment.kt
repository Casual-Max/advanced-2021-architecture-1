@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package ru.gaket.themoviedb.presentation.auth.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.Navigator
import ru.gaket.themoviedb.databinding.AuthFragmentBinding
import ru.gaket.themoviedb.domain.auth.LogInError
import ru.gaket.themoviedb.presentation.auth.viewmodel.AuthState
import ru.gaket.themoviedb.presentation.auth.viewmodel.AuthViewModel
import ru.gaket.themoviedb.util.showErrorResId
import ru.gaket.themoviedb.util.showSystemMessage
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.auth_fragment) {

    private val viewModel: AuthViewModel by viewModels()

    private val binding by viewBinding(AuthFragmentBinding::bind)

    @Inject
    lateinit var navigator: Navigator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent { Auth() }
    }

    //    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.emailInput.clearErrorOnAnyInput()
//        binding.passwordInput.clearErrorOnAnyInput()
//
//        binding.authBtn.setOnClickListener {
//            viewModel.auth(
//                email = binding.emailInput.getTrimmedText(),
//                password = binding.passwordInput.getTrimmedText()
//            )
//        }
//
//        viewModel.authState.observe(viewLifecycleOwner, ::handleState)
//    }

    private fun handleState(state: AuthState) {
        val isAuthorizing = (state is AuthState.Authorizing)

        binding.authBtn.isEnabled = !isAuthorizing
        binding.authLoader.isVisible = isAuthorizing

        when (state) {
            AuthState.Empty,
            AuthState.Authorizing,
            -> Unit

            AuthState.Authorized -> navigator.back()
            is AuthState.InputError -> handleInputError(state)
            is AuthState.AuthError -> showSystemMessage(text = getString(state.logInError.messageResId))
        }
    }

    private fun handleInputError(error: AuthState.InputError) = when (error) {
        AuthState.InputError.Email -> binding.emailInput.showErrorResId(R.string.email_input_error)
        AuthState.InputError.Password -> binding.passwordInput.showErrorResId(R.string.password_input_error)
    }

    companion object {

        fun newInstance(): AuthFragment = AuthFragment()
    }
}

@get:StringRes
private val LogInError.messageResId: Int
    get() = when (this) {
        LogInError.InvalidUserCredentials -> R.string.invalid_user_credentials
        LogInError.Unknown -> R.string.unknown_error
    }

@Preview
@Composable
fun Auth() {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.gradient_first),
                            colorResource(id = R.color.gradient_second),
                            colorResource(id = R.color.gradient_third)
                        )
                    )
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            AuthTitle()
            Spacer(modifier = Modifier.height(20.dp))
            AuthImage()
            Spacer(modifier = Modifier.height(20.dp))
            AuthEmailField()
            Spacer(modifier = Modifier.height(10.dp))
            AuthPasswordField()
            Spacer(modifier = Modifier.height(20.dp))
            AuthAction()
        }
    }
}

@Composable
fun AuthTitle() {
    Text(
        text = "Welcome back!",
        color = colorResource(id = R.color.colorAuthTitle),
        fontWeight = FontWeight.ExtraBold,
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
fun AuthImage() {
    Image(
        painter = painterResource(id = R.drawable.auth_logo), contentDescription = "logo"
    )
}

@Composable
fun AuthEmailField() {
    var email by rememberSaveable { mutableStateOf("") }

    TextField(
        value = email,
        onValueChange = { email = it },
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        placeholder = { Text(text = "E-mail", color = Color.Gray) },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.White
        )
    )
}

@Composable
fun AuthPasswordField() {
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(
            color = if (showPassword) Color.Black else colorResource(id = R.color.colorAuthTitle)
        ),
        placeholder = { Text(text = "Password", color = Color.Gray) },
        trailingIcon = {
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                    imageVector = if (showPassword) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = if (showPassword) "showPassword" else "hidePassword",
                    tint = if (showPassword) {
                        colorResource(id = R.color.colorAuthTitle)
                    } else {
                        Color.Gray
                    }
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            containerColor = Color.White
        ),
    )
}

@Composable
fun AuthAction() {
    Button(
        onClick = { },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .heightIn(56.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = colorResource(id = R.color.colorAuthTitle)
        )
    ) {
        Text(
            text = "LOG IN",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}