document.addEventListener("DOMContentLoaded", function () {
	// Inject AI Widget HTML if not already present
	if (!document.getElementById("ai-chat-window")) {
		const widgetHtml = `
			<button id="ai-widget-launcher" class="ai-widget-launcher" style="background: linear-gradient(135deg, #0284c7, #0369a1);" title="Chat with MediPulse AI Clinical Copilot">
				<div class="pulse-ring"></div>
				<i class="fa-solid fa-heart-pulse"></i>
			</button>

			<div id="ai-chat-window" class="ai-chat-window">
				<div class="ai-chat-header" style="background: linear-gradient(135deg, #0369a1, #0284c7);">
					<div class="ai-header-info">
						<div class="ai-avatar"><i class="fa-solid fa-stethoscope"></i></div>
						<div>
							<div class="ai-title">MediPulse AI Clinical Copilot</div>
							<div class="ai-status">
								<span class="ai-status-dot"></span> Online & GxP Compliant
							</div>
						</div>
					</div>
					<button id="ai-close-btn" class="ai-close-btn"><i class="fa-solid fa-xmark"></i></button>
				</div>

				<div id="ai-chat-body" class="ai-chat-body">
					<!-- Initial greeting will be loaded here -->
				</div>

				<div class="ai-chat-footer">
					<input type="text" id="ai-chat-input" class="ai-chat-input" placeholder="Ask about batches, ROP, expiry, EOQ..." autocomplete="off">
					<button id="ai-send-btn" class="ai-send-btn" style="background: #0284c7;" title="Send message">
						<i class="fa-solid fa-paper-plane"></i>
					</button>
				</div>
			</div>
		`;
		document.body.insertAdjacentHTML("beforeend", widgetHtml);
	}

	const launcher = document.getElementById("ai-widget-launcher");
	const chatWindow = document.getElementById("ai-chat-window");
	const closeBtn = document.getElementById("ai-close-btn");
	const chatBody = document.getElementById("ai-chat-body");
	const chatInput = document.getElementById("ai-chat-input");
	const sendBtn = document.getElementById("ai-send-btn");

	let initialGreetingLoaded = false;

	// Toggle Chat
	function toggleChat() {
		const isOpen = chatWindow.classList.toggle("open");
		if (isOpen) {
			chatInput.focus();
			if (!initialGreetingLoaded) {
				sendAiQuery(""); // Trigger initial greeting
				initialGreetingLoaded = true;
			}
		}
	}

	launcher.addEventListener("click", toggleChat);
	closeBtn.addEventListener("click", toggleChat);

	// Send message
	function handleSendMessage() {
		const text = chatInput.value.trim();
		if (!text) return;

		appendUserMessage(text);
		chatInput.value = "";
		sendAiQuery(text);
	}

	sendBtn.addEventListener("click", handleSendMessage);
	chatInput.addEventListener("keydown", function (e) {
		if (e.key === "Enter") {
			handleSendMessage();
		}
	});

	function appendUserMessage(text) {
		const msgDiv = document.createElement("div");
		msgDiv.className = "ai-msg user";
		msgDiv.innerHTML = `<div class="ai-msg-bubble">${escapeHtml(text)}</div>`;
		chatBody.appendChild(msgDiv);
		scrollToBottom();
	}

	function showTypingIndicator() {
		const typingDiv = document.createElement("div");
		typingDiv.id = "ai-typing";
		typingDiv.className = "ai-msg bot";
		typingDiv.innerHTML = `
			<div class="ai-typing-indicator">
				<div class="ai-typing-dot"></div>
				<div class="ai-typing-dot"></div>
				<div class="ai-typing-dot"></div>
			</div>
		`;
		chatBody.appendChild(typingDiv);
		scrollToBottom();
	}

	function removeTypingIndicator() {
		const typing = document.getElementById("ai-typing");
		if (typing) typing.remove();
	}

	function sendAiQuery(messageText) {
		showTypingIndicator();

		fetch("/api/ai/chat", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ message: messageText })
		})
		.then(res => res.json())
		.then(data => {
			removeTypingIndicator();
			appendBotResponse(data.reply, data.suggestions);
		})
		.catch(err => {
			removeTypingIndicator();
			appendBotResponse("I encountered a connection hiccup with the cloud telemetry node. Please try again!", ["Recommend suites", "Cost for ERP"]);
		});
	}

	function appendBotResponse(markdownText, suggestions) {
		const msgDiv = document.createElement("div");
		msgDiv.className = "ai-msg bot";

		let formattedHtml = parseSimpleMarkdown(markdownText);

		let suggestionsHtml = "";
		if (suggestions && suggestions.length > 0) {
			suggestionsHtml = `<div class="ai-suggestions-container">` +
				suggestions.map(s => `<button class="ai-chip" onclick="handleChipClick('${escapeHtml(s)}')">${escapeHtml(s)}</button>`).join("") +
				`</div>`;
		}

		msgDiv.innerHTML = `<div class="ai-msg-bubble">${formattedHtml} ${suggestionsHtml}</div>`;
		chatBody.appendChild(msgDiv);
		scrollToBottom();
	}

	window.handleChipClick = function (chipText) {
		appendUserMessage(chipText);
		sendAiQuery(chipText);
	};

	function scrollToBottom() {
		chatBody.scrollTop = chatBody.scrollHeight;
	}

	function escapeHtml(str) {
		if (!str) return "";
		return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
	}

	function parseSimpleMarkdown(md) {
		if (!md) return "";
		let html = md;
		// Headers
		html = html.replace(/### (.*?)\n/g, '<h3>$1</h3>');
		html = html.replace(/## (.*?)\n/g, '<h2>$1</h2>');
		// Bold
		html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
		// Italic
		html = html.replace(/\*(.*?)\*/g, '<em>$1</em>');
		// Bullet items
		html = html.replace(/^- (.*?)\n/gm, '<li>$1</li>');
		html = html.replace(/(<li>.*?<\/li>)/s, '<ul>$1</ul>');
		// Line breaks
		html = html.replace(/\n\n/g, '<br><br>');
		html = html.replace(/\n/g, '<br>');
		return html;
	}
});
