package app.android.serv.rest

import android.util.Log
import app.android.serv.R
import app.android.serv.rest.RetrofitException
import org.greenrobot.eventbus.EventBus

/**
 * Created by kombo on 03/01/2018.
 */

object ErrorHandler {

    private fun handleException(retrofitException: RetrofitException) {

        try {
            val kind = retrofitException.kind
            val code = if (retrofitException.response != null) retrofitException.response.code() else 0
            val message: String

            when (kind) {
                RetrofitException.Kind.HTTP -> if (code != 0) {
                    when (code) {
                        400 //bad request
                        -> message = accessStringResource(R.string.something_went_wrong)
                        401 //unauthorized
                        -> message = accessStringResource(R.string.access_denied)
                        403 //forbidden
                        -> message = accessStringResource(R.string.something_went_wrong)
                        404 //not found
                        -> message = accessStringResource(R.string.not_found)
                        405 //method not allowed
                        -> message = accessStringResource(R.string.error_on_service_access)
                        406 //Not acceptable
                        -> message = accessStringResource(R.string.error_on_service_access)
                        408 //Request timeout
                        -> message = accessStringResource(R.string.error_on_service_access)
                        409 //Conflict
                        -> message = accessStringResource(R.string.error_on_service_access)
                        410 //Gone
                        -> message = accessStringResource(R.string.error_on_service_access)
                        411 //Length Required
                        -> message = accessStringResource(R.string.error_on_service_access)
                        412 //Precondition failed
                        -> message = accessStringResource(R.string.error_on_service_access)
                        413 //Payload Too Large
                        -> message = accessStringResource(R.string.error_on_service_access)
                        414 //Request-URI Too Long
                        -> message = accessStringResource(R.string.error_on_service_access)
                        415 //Unsupported Media Type
                        -> message = accessStringResource(R.string.error_on_service_access)
                        416 //Requested Range Not Satisfiable
                        -> message = accessStringResource(R.string.error_on_service_access)
                        417 //Expectation Failed
                        -> message = accessStringResource(R.string.error_on_service_access)
                        418 //I'm a Tea Pot
                        -> message = accessStringResource(R.string.error_on_service_access)
                        421 //Misdirected Request
                        -> message = accessStringResource(R.string.error_on_service_access)
                        422 //Unprocessable entity
                        -> message = accessStringResource(R.string.unprocessable_entity)
                        423 //Locked
                        -> message = accessStringResource(R.string.error_on_service_access)
                        424 //Failed Dependency
                        -> message = accessStringResource(R.string.error_on_service_access)
                        426 //Upgrade required
                        -> message = accessStringResource(R.string.error_on_service_access)
                        428 //Precondition Required
                        -> message = accessStringResource(R.string.error_on_service_access)
                        429 //Too Many Requests
                        -> message = accessStringResource(R.string.error_on_service_access)
                        431 //Request Header Fields Too Large
                        -> message = accessStringResource(R.string.error_on_service_access)
                        444 //Connect Closed Without Response
                        -> message = accessStringResource(R.string.error_on_service_access)
                        451 //Unavailable for Legal Reasons
                        -> message = accessStringResource(R.string.error_on_service_access)
                        499 //Client Closed Request
                        -> message = accessStringResource(R.string.error_on_service_access)
                        500 //internal server error
                        -> message = accessStringResource(R.string.error_on_service_access)
                        501 //Not implemented
                        -> message = accessStringResource(R.string.error_on_service_access)
                        502 //bad gateway
                        -> message = accessStringResource(R.string.service_could_not_complete_request)
                        503 //service unavailable
                        -> message = accessStringResource(R.string.service_not_available)
                        504 //Gateway Timeout
                        -> message = accessStringResource(R.string.error_on_service_access)
                        505 //HTTP Version Not Supported
                        -> message = accessStringResource(R.string.error_on_service_access)
                        506 //Variant also negotiates
                        -> message = accessStringResource(R.string.error_on_service_access)
                        507 //Insufficient Storage
                        -> message = accessStringResource(R.string.error_on_service_access)
                        508 //Loop detected
                        -> message = accessStringResource(R.string.error_on_service_access)
                        510 //Not extended
                        -> message = accessStringResource(R.string.error_on_service_access)
                        599 //Network connect timeout error
                        -> message = accessStringResource(R.string.error_on_service_access)

                        else -> message = accessStringResource(R.string.unexpected_error)
                    }
                } else
                    message = accessStringResource(R.string.unexpected_error)

                RetrofitException.Kind.NETWORK -> message = accessStringResource(R.string.network_error)
                RetrofitException.Kind.UNEXPECTED -> message = accessStringResource(R.string.unexpected_error)

                else -> message = accessStringResource(R.string.unexpected_error)
            }

            Log.e(ErrorHandler::class.java.simpleName, message)
            EventBus.getDefault().post(ErrorEvent(message))
        } catch (e: Exception) {
            Log.e(ErrorHandler::class.java.simpleName, e.localizedMessage, e)
        }
    }

    private fun accessStringResource(resourceName: Int): String {
        return Wanda.INSTANCE.resources.getString(resourceName)
    }

    /**
     * Checks if the throwable can safely be cast into a RetrofitException object which is then handled by the handleException method above.
     * If it can't be cast, a generic message is shown to the user.
     */
    fun showError(throwable: Throwable){
        val retrofitException = throwable as? RetrofitException

        when {
            retrofitException != null -> handleException(retrofitException)
            else -> EventBus.getDefault().post(ErrorEvent(accessStringResource(R.string.unexpected_error)))
        }
    }
}
