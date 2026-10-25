// Duy Truong Moto Luxury E-Commerce JS & WebSocket Client

let stompClient = null;
const currentUserId = window.currentUserId || null;
const currentUserName = window.currentUserName || "Khách hàng";

function connectWebSocket() {
    if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
        return;
    }
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Tắt log verbose trên production

    stompClient.connect({}, function (frame) {
        // Lắng nghe tin nhắn chat công khai từ chuyên viên Showroom
        stompClient.subscribe('/topic/public', function (chatResponse) {
            const message = JSON.parse(chatResponse.body);
            displayIncomingMessage(message);
        });
    }, function (error) {
        console.warn("WebSocket reconnection needed:", error);
    });
}

function sendChatMessage() {
    const input = document.getElementById("chatMessageInput");
    if (!input || !input.value.trim()) return;

    if (!stompClient || !stompClient.connected) {
        alert("Đang thiết lập kết nối tới Showroom, vui lòng thử lại sau 2 giây!");
        return;
    }

    const chatPayload = {
        senderId: currentUserId ? currentUserId : 0,
        senderName: currentUserName,
        storeId: 1, // Default Showroom Quận 1
        message: input.value.trim()
    };

    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatPayload));
    input.value = "";
}

function displayIncomingMessage(message) {
    const chatContainer = document.getElementById("chatMessagesBox");
    if (!chatContainer) return;

    const isMe = (currentUserId && message.senderId === currentUserId) || (!currentUserId && message.senderId === 0 && message.senderName === currentUserName);
    const msgDiv = document.createElement("div");
    msgDiv.className = `d-flex mb-3 ${isMe ? 'justify-content-end' : 'justify-content-start'}`;

    msgDiv.innerHTML = `
        <div class="p-3 rounded-4 ${isMe ? 'bg-gold text-dark' : 'bg-dark text-light border border-secondary'}" style="max-width: 80%; font-size: 0.9rem;">
            <div class="fw-bold mb-1" style="font-size: 0.75rem;">${message.senderName}</div>
            <div>${message.message}</div>
        </div>
    `;

    chatContainer.appendChild(msgDiv);
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

function formatVnd(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

document.addEventListener("DOMContentLoaded", function () {
    connectWebSocket();

    const sendBtn = document.getElementById("btnSendChat");
    if (sendBtn) {
        sendBtn.addEventListener("click", sendChatMessage);
    }

    const input = document.getElementById("chatMessageInput");
    if (input) {
        input.addEventListener("keypress", function (e) {
            if (e.key === "Enter") {
                sendChatMessage();
            }
        });
    }
});
