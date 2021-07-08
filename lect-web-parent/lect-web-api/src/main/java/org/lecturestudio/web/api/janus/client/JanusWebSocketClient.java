/*
 * Copyright (C) 2021 TU Darmstadt, Department of Computer Science,
 * Embedded Systems and Applications Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lecturestudio.web.api.janus.client;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Builder;
import java.net.http.WebSocket.Listener;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.lecturestudio.core.ExecutableBase;
import org.lecturestudio.core.ExecutableException;
import org.lecturestudio.core.recording.LectureRecorder;
import org.lecturestudio.web.api.data.bind.JsonConfigProvider;
import org.lecturestudio.web.api.janus.JanusHandler;
import org.lecturestudio.web.api.janus.JanusMessageTransmitter;
import org.lecturestudio.web.api.janus.json.JanusMessageFactory;
import org.lecturestudio.web.api.janus.message.JanusMessageType;
import org.lecturestudio.web.api.janus.message.JanusMessage;
import org.lecturestudio.web.api.net.OwnTrustManager;
import org.lecturestudio.web.api.service.ServiceParameters;
import org.lecturestudio.web.api.stream.config.WebRtcConfiguration;

/**
 * Janus WebSocket signaling client implementation. This client sends, receives,
 * decodes and encodes Janus JSON-formatted signaling messages.
 *
 * @author Alex Andres
 */
public class JanusWebSocketClient extends ExecutableBase implements JanusMessageTransmitter {

	private final ServiceParameters serviceParameters;

	private final WebRtcConfiguration webRtcConfig;

	private final LectureRecorder lectureRecorder;

	private WebSocket webSocket;

	private Jsonb jsonb;

	private JanusHandler handler;


	public JanusWebSocketClient(ServiceParameters parameters,
			WebRtcConfiguration webRtcConfig, LectureRecorder lectureRecorder) {
		this.serviceParameters = parameters;
		this.webRtcConfig = webRtcConfig;
		this.lectureRecorder = lectureRecorder;
	}

	@Override
	public void sendMessage(JanusMessage message) {
		logTraceMessage("WebSocket ->: {0}", jsonb.toJson(message));

		webSocket.sendText(jsonb.toJson(message), true)
				.exceptionally(throwable -> {
					logException(throwable, "Send Janus message failed");
					return null;
				});
	}

	@Override
	protected void initInternal() throws ExecutableException {
		jsonb = new JsonConfigProvider().getContext(null);

		handler = new JanusHandler(this, webRtcConfig, lectureRecorder);
		handler.init();
	}

	@Override
	protected void startInternal() throws ExecutableException {
		HttpClient httpClient = HttpClient.newBuilder()
				.sslContext(createSSLContext())
				.build();

		Builder webSocketBuilder = httpClient.newWebSocketBuilder();
		webSocketBuilder.subprotocols("janus-protocol");
		webSocketBuilder.connectTimeout(Duration.of(10, ChronoUnit.SECONDS));

		webSocket = webSocketBuilder
				.buildAsync(URI.create(serviceParameters.getUrl()),
						new WebSocketListener())
				.join();

		handler.start();
	}

	@Override
	protected void stopInternal() throws ExecutableException {
		handler.stop();

		webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "disconnect").join();
	}

	@Override
	protected void destroyInternal() throws ExecutableException {
		handler.destroy();
	}

	private static SSLContext createSSLContext() {
		SSLContext sslContext;

		try {
			X509TrustManager trustManager = new OwnTrustManager("keystore.jks",
					"mypassword");

			sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(null, new TrustManager[] { trustManager }, null);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		return sslContext;
	}



	private class WebSocketListener implements Listener {

		/** Accumulating message buffer. */
		private StringBuffer buffer = new StringBuffer();


		@Override
		public void onError(WebSocket webSocket, Throwable error) {
			logException(error, "WebSocket error");
		}

		@Override
		public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
			logTraceMessage("WebSocket <-: {0}", data);

			webSocket.request(1);

			buffer.append(data);

			if (last) {
				StringReader reader = new StringReader(buffer.toString());
				JsonObject body = Json.createReader(reader).readObject();

				try {
					JanusMessageType type = JanusMessageType.fromString(body.getString("janus"));
					JanusMessage message = JanusMessageFactory.createMessage(jsonb, body, type);

					handler.handleMessage(message);
				}
				catch (NoSuchElementException e) {
					logException(e, "Non existing Janus event type received");
				}
				catch (Exception e) {
					logException(e, "Process Janus message failed");
				}

				buffer = new StringBuffer();
			}

			return null;
		}
	}
}
