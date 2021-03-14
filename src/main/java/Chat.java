
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import static spark.Spark.*;

public class Chat {
	static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		port(3000);
		staticFileLocation("/public");
		webSocket("/chat", ChatWebSocketHandler.class);
		init();
	}

	public static void broadcastMessage(String sender, String message) {
		String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

		userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
			try {
				session.getRemote().sendString(String
						.valueOf(new JSONObject()
								.put("method", "message")
								.put("user", sender)
								.put("text", message)
								.put("timeStamp", timeStamp)
								));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void broadcastUserlist() {
		userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
			try {
				session.getRemote().sendString(String
						.valueOf(new JSONObject()
								.put("method", "userList")
								.put("userlist", userUsernameMap.values())
								));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public static void sendError(Session session, String message) {
		try {
			session.getRemote().sendString(String
					.valueOf(new JSONObject()
							.put("method", "error")
							.put("text", message)
							));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}