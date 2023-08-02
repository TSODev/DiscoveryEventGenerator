import api.ServiceApi.Companion.apiGetToken

import api.ServiceApi.Companion.apiSendEvent
import com.google.gson.Gson

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.xenomachina.argparser.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.AuthentificationResponse
import network.TokenHolder
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDateTime
import kotlin.system.exitProcess


private val logger = KotlinLogging.logger {}


class ParsedArgs(parser: ArgParser) {

    val validURL = "^(http(s):\\/\\/.)[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)(/)\$"

    val server by parser.storing(
        "-s", "--server",
        help = "URL API du serveur Discovery , (https et termine avec '/') \n " +
                "généralement https://server/api/v1.1/"
    )
        .addValidator {
        if (!value.matches(Regex(validURL)))
            throw InvalidArgumentException("URL du serveur invalide : $value \n" +
                "lancer le programme avec l'option -h pour de l'aide")
    }

    val unsafe by parser.flagging(
        "-x", "--unsecure",
        help = "do not verify SSL certificate checking process (useful with self signed certificate)"
    ).default(false)

    val username by parser.storing(
        "-u", "--username",
        help = "Login - Nom de l'utilisateur"
    )

    val password by parser.storing(
        "-p", "--password",
        help = "Login - Mot de passe"
    )

    val event by parser.storing(
        "-e", "--event",
        help = "nom de la source de l'évènement"
    )

    val type by parser.storing(
        "-t", "--type",
        help = "type de l'évènement"
    )


    val params by parser.storing(
        "-a", "--params",
        help = "Parametres additionels (string format JSON)"
    ).default("{\"detail1\":\"exemple\"}")

}
    fun main(args: Array<String>): Unit = mainBody {

        val prologue = "Discovery Event Generator  : "
        val epilogue = "TSODev pour Orange Business"

        try {
            ArgParser(args, ArgParser.Mode.GNU, DefaultHelpFormatter(prologue, epilogue)).parseInto(::ParsedArgs).run {


                logger.info("==============================================================================")
                logger.info(" Discovery Event Generator - TSO pour Orange Business - 08/23 - version 1.0.1 ")
                logger.info("==============================================================================")

                val gson = Gson()
                var jsonParams = JsonObject()
                try {
                    jsonParams = gson.fromJson(params, JsonObject::class.java)
                } catch (e: JsonSyntaxException) {
                    logger.error("Erreur JSON pour PARAMS : ${e.message}")
                    exitProcess(-5)
                }


                apiCallByCoroutines(
                    username,
                    password,
                    server,
                    event,
                    type,
                    jsonParams,
                    unsafe
                )

            }
        }
        catch (e: SystemExitException) {
            logger.error("Erreur d'argument dans la ligne de commande : ${e.message}")
            exitProcess(-4)
        }
    }

    private fun apiCallByCoroutines(
        username: String,
        password: String,
        server: String,
        event: String,
        type: String,
        params: JsonObject,
        unsafe: Boolean
    ) = runBlocking {
        launch { // launch new coroutine in the scope of runBlocking


                if (username.isNotEmpty() && password.isNotEmpty()) {
                    try {
                        apiGetToken(server, username, password, unsafe).let { token: String? ->
                            if (token != null) {
                                TokenHolder.saveToken(token)
                                val result = apiSendEvent(server, event, type, params, unsafe)
                                logger.info("Event ID: $result")
                            }
                        }
                    } catch (exception: HttpException) {
                        logger.error(exception) { "Erreur HTTP : $exception" }
                        exitProcess(-1)
                    }
                } else {
                    logger.error("USERNAME et PASSWORD ne doivent pas être vide !")
                    exitProcess(-3)
                }


        }
    }
