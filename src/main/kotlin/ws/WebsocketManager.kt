package ws

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

typealias ClientSessions = ConcurrentHashMap<String, DefaultWebSocketServerSession>

class WebsocketManager() {
    private val clients = ClientSessions()

    fun addClient(clientId: String, session: DefaultWebSocketServerSession) {
        clients[clientId] = session
    }

    fun removeClient(clientId: String): Boolean {
        return clients.remove(clientId) != null
    }

    fun getAllClients(): ClientSessions {
        return clients
    }

    fun getSessionFromId(clientId: String): DefaultWebSocketServerSession? {
        return clients[clientId]
    }

    suspend fun handleMessage(session: DefaultWebSocketServerSession, message: WsMessage) {
        when (message.type) {
            "ping" -> {
                session.send(Frame.Text(Json.encodeToString(WsMessage(type = "ping", payload = null))))
            }
        }
    }
}