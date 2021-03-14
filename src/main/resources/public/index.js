const sendBtn = document.getElementById("sendBtn");
const messageInput = document.getElementById("messageInput");
const connectBtn = document.getElementById("connect");

let webSocket = undefined;
let connected = false;

connectBtn.addEventListener("click", () => {
    let userName = document.getElementById("userName").value;
    if (!connected) {
        if (userName.length > 0) {
            webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat?" + userName);

            connected = true;
            connectBtn.innerText = "Disconnect";

            webSocket.onmessage = (msg) => {
                updateChat(msg);
            }
            webSocket.onclose = () => {
                alert("WebSocket connection closed");
                connected = false;
                document.getElementById("listOfUsers").innerHTML = "";
                document.getElementById("chatMessages").innerHTML = "";
                connectBtn.innerText = "Connect";
            }
        }
    } else {
        webSocket.close();
    }
});

sendBtn.addEventListener("click", () => {
    if (webSocket) {
        sendMessage(messageInput.value);
    }
});

messageInput.addEventListener("keypress", (e) => {
    if (webSocket) {
        if (e.key === "Enter") {
            sendMessage(messageInput.value);
        }
    }
});

function sendMessage(message) {
    if (message !== "" && webSocket) {
        webSocket.send(message);
        messageInput.value = "";
    }
}

function updateChat(msg) {
    let message = JSON.parse(msg.data);

    if (message.method === "message") {
        let chatMessages = document.getElementById("chatMessages");
        html_content = `
        <li class="list-group-item d-flex  align-items-center pb-3">
            <span class="badge badge-primary badge-pill mr-2 ">`+ message.user + `</span>
            ` + message.text + `
            <span class="badge badge-pill badge-light timeStamp">` + message.timeStamp + `</span>
        </li>`
        chatMessages.innerHTML += html_content;
        chatMessages.scrollIntoView(false);
    }
    else if (message.method === "userList") {
        let userList = document.getElementById("listOfUsers");
        userList.innerHTML = "";

        message.userlist.forEach((user) => {
            html_content = `
            <li class="list-group-item">
                `+ user + `
            </li>`
            userList.innerHTML += html_content;
        });
    }
    else if (message.method === "error") {
        alert(message.text);
    }
}

