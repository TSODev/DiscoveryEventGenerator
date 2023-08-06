import api.ServiceApi.Companion.apiGetToken

import api.ServiceApi.Companion.apiSendEvent
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import com.google.gson.Gson

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import network.TokenHolder
import retrofit2.HttpException
import utils.logging.Level
import utils.logging.TLogger
import kotlin.system.exitProcess


private val logger = TLogger

fun main(args: Array<String>):Unit =   DiscoveryEventGenerator().versionOption("1.0.2").main(args)
class DiscoveryEventGenerator: CliktCommand(help = "Enregistre un Evenement dans BMC Discovery") {

    val validURL = "^(http(s):\\/\\/.)[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)(/)\$"
    //   val validURL_IP = "^http (s?):\\/\\/ ( (2 [0-5] {2}|1 [0-9] {2}| [0-9] {1,2})\\.) {3} (2 [0-5] {2}|1 [0-9] {2}| [0-9] {1,2}) (:\\d+)?(/)\$"

    val server: String by option(
        "-s", "--server",
        help = "URL API du serveur Discovery , (https et termine avec '/') \n " +
                "généralement https://server/api/v1.1/")
        .required()
        .validate {
            if (!it.matches(Regex(validURL)))
                throw UsageError("URL du serveur invalide : $it \n" +
                        "lancer le programme avec l'option -h pour de l'aide", server , -1)
        }

    val username: String by option(
        "-u", "--username",
        help = "Nom de l'utilisateur"
    ).prompt()

    val password: String by option(
        "-p", "--password",
        help = "Mot de Passe de l'utilisateur"
    ).prompt(hideInput = true)

    val unsafe by option( "-x", "--unsecure",
        help = "pas de vérification du certificat SSL\n" +
                "(permet l'utilisation de certificat auto signé)"
    ).flag(default = true)

    val event by option(
        "-e", "--event",
        help = "nom de la source de l'évènement"
    ).prompt()

    val type by option(
        "-t", "--type",
        help = "type de l'évènement"
    ).prompt()


    val params by option(
        "-a", "--params",
        help = "Parametres additionnels (string format JSON) \n" +
                " () \n" +
                "  \t()\n"
    ).default("{\"detail1\":\"exemple\"}")


    val debugLevel by option(
        "-d", "--debug",
        help = "Niveau du debug").choice(
        "off" to Level.OFF,
        "trace" to Level.TRACE,
        "debug" to Level.DEBUG,
        "info" to Level.INFO,
        "error" to Level.ERROR,
        "fatal" to Level.FATAL,
        "all" to Level.ALL
    ).default(Level.OFF)

    override fun run() {

                echo("==============================================================================")
                echo(" Discovery Event Generator - TSO pour Orange Business - 08/23 - version 1.0.2 ")
                echo("==============================================================================")

        logger.setRunLevel(debugLevel)
        logger.addDateToPrefix()
        logger.addTimeToPrefix()
        logger.addToPrefix("->")
//        logger.addDurationToPrefix()

                val gson = Gson()
                var jsonParams = JsonObject()
                try {
                    jsonParams = gson.fromJson(params, JsonObject::class.java)
                } catch (e: JsonSyntaxException) {
                    throw PrintMessage("Erreur JSON pour PARAMS : ${e.message}", -5, true)
    //                exitProcess(-5)
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
                        throw PrintMessage("Erreur : ${exception.message}", -1, true )
//                        exitProcess(-1)
                    }
                } else {
                    throw PrintMessage("USERNAME et PASSWORD ne doivent pas être vide !", -3, true)
//                    exitProcess(-3)
                }


        }
    }
