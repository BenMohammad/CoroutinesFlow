package com.benmohammad.coroutinesflow.ui.add

import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benmohammad.coroutinesflow.domain.entity.User
import com.benmohammad.coroutinesflow.domain.usecase.AddUserUseCase
import com.benmohammad.coroutinesflow.flatMapFirst
import com.benmohammad.coroutinesflow.ui.add.AddContract.*
import com.benmohammad.coroutinesflow.withLatestFrom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class AddVM(private val addUser: AddUserUseCase): ViewModel() {

    private val _eventChannel = BroadcastChannel<SingleEvent>(capacity = Channel.BUFFERED)
    private val _intentChannel = BroadcastChannel<ViewIntent>(capacity = Channel.BUFFERED)

    val viewState: StateFlow<ViewState>
    val singleEvent: Flow<SingleEvent>

    suspend fun processIntent(intent: ViewIntent) = _intentChannel.send(intent)

    init {
        val initialVS = ViewState.initial()

        viewState = MutableStateFlow(initialVS)
        singleEvent = _eventChannel.asFlow()

        _intentChannel
            .asFlow()
            .toPartialStateChangesFlow()
            .sendSingleEvent()
            .scan(initialVS) {state, change -> change.reduce(state)}
            .onEach {viewState.value = it}
            .catch{}
            .launchIn(viewModelScope)
    }

    private fun Flow<PartialStateChange>.sendSingleEvent(): Flow<PartialStateChange> {
        return onEach { change ->
            val event = when(change) {
                is PartialStateChange.ErrorsChanged -> return@onEach
                PartialStateChange.AddUser.Loading -> return@onEach
                is PartialStateChange.AddUser.AddUserSuccess -> SingleEvent.AddUserSuccess(change.user)
                is PartialStateChange.AddUser.AddUserFailure -> SingleEvent.AddUserFailure(
                    change.user,
                    change.throwable
                )
            }
            _eventChannel.send(event)
        }
    }

    private fun Flow<ViewIntent>.toPartialStateChangesFlow(): Flow<PartialStateChange> {
        val emailErrors = filterIsInstance<ViewIntent.EmailChanged>()
            .map { it.email }
            .map { validateEmail(it) to it }

        val firstNameErrors = filterIsInstance<ViewIntent.FirstNameChanged>()
            .map { it.firstName }
            .map { validateFirstName(it) to it }

        val lastNameErrors = filterIsInstance<ViewIntent.LastNameChanged>()
            .map { it.lastName }
            .map { validateLastName(it) to it }

        val userFormFlow =
            combine(emailErrors, firstNameErrors, lastNameErrors) { email, firstName, lastName ->
                UserForm(
                    errors = email.first + firstName.first + lastName.first,
                    user = User(
                        firstName = firstName.second ?: "",
                        email = email.second ?: "",
                        lastName = lastName.second ?: "",
                        id = "",
                        avatar = ""
                    )
                )
            }

        val addUserChanges = filterIsInstance<ViewIntent.Submit>()
            .withLatestFrom(userFormFlow) {_, userForm -> userForm}
            .filter { it.errors.isEmpty() }
            .map { it.user }
            .flatMapFirst { user ->
                flow { emit(addUser(user)) }
                .map {
                @Suppress("USELESS_CAST")
                PartialStateChange.AddUser.AddUserSuccess(user) as PartialStateChange.AddUser
            }
                .onStart { emit(PartialStateChange.AddUser.Loading) }
                .catch { emit(PartialStateChange.AddUser.AddUserFailure(user, it)) }
            }

        return merge(
            userFormFlow
                .map { it.errors }
                .map { PartialStateChange.ErrorsChanged(it) },
            addUserChanges

        )
    }

    private companion object {
        const val MIN_LENGTH_FIRST_NAME = 3
        const val MIN_LENGTH_LAST_NAME = 3

        private data class UserForm(
            val errors: Set<ValidationError>,
            val user: User
        )

        fun validateFirstName(firstName: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()
            if(firstName == null || firstName.length < MIN_LENGTH_FIRST_NAME) {
                errors += ValidationError.TOO_SHORT_FIRST_NAME
            }

            return errors
        }


        fun validateLastName(lastName: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

            if(lastName == null || lastName.length < MIN_LENGTH_LAST_NAME) {
                errors += ValidationError.TOO_SHORT_LAST_NAME
            }

            return errors
        }

        fun validateEmail(email: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

            if(email == null || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                errors += ValidationError.INVALID_EMAIL_ADDRESS
            }

            return errors
        }
    }
}