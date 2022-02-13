package matugr.authorization_request.external.option

/**
 * Client has the following options to dictate whether or not, and how, the state in the
 * authorization request.
 *
 * See README: State
 */
sealed class StateOption {
    data class Input(val state: String): StateOption()
    object Generate: StateOption()
    object Exclude: StateOption()
}