
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class ChatWebSocketHandler {

	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
		String userName = user.getUpgradeRequest().getQueryString();

		Boolean contains = Chat.userUsernameMap.containsValue(userName);
		if (contains) {
			Chat.sendError(user, "The user name: " + userName + " is already in use!");
			user.close();
		}
		else {
			Chat.userUsernameMap.put(user, userName);
			Chat.broadcastUserlist();
			Chat.broadcastMessage("Server", userName + " connected!");
		}
	}

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {
		String userName = Chat.userUsernameMap.get(user);
		if(userName != null) {
			Chat.userUsernameMap.remove(user);
			Chat.broadcastUserlist();
			Chat.broadcastMessage("Server", userName + " disconnected!");
		}
	}

	@OnWebSocketMessage
	public void onMessage(Session user, String message) {
		Chat.broadcastMessage(Chat.userUsernameMap.get(user), message);
	}
}