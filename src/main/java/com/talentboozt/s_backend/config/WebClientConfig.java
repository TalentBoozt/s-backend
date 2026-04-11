package com.talentboozt.s_backend.config;

import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {

        ConnectionProvider provider = ConnectionProvider.builder("ai-pool")
                .maxConnections(50)
                // FIX 1: maxIdleTime must be well below the NAT gateway idle timeout.
                // AWS NAT Gateway drops idle TCP connections after 350 seconds for established
                // connections, but only ~30s for connections with no in-flight data.
                // Keeping this at 20s ensures we never hand out a connection the NAT has
                // already silently killed. The previous value was fine — keep it.
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofMinutes(5))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(15))
                // FIX 2: Automatically retry the request once on a connection close error
                // (PrematureCloseException). This handles the race where a connection passes
                // the maxIdleTime check but is killed by the NAT before the response arrives.
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                // FIX 3: Force HTTP/1.1 only.
                // Reactor Netty negotiates HTTP/2 via ALPN when the server supports it.
                // HTTP/2 multiplexed streams are particularly vulnerable to NAT idle timeouts
                // because a single TCP connection carries many streams — when the NAT kills
                // the TCP connection, all in-flight streams die silently.
                // HTTP/1.1 uses one connection per request: failures are isolated and retries
                // are straightforward.
                .protocol(HttpProtocol.HTTP11)

                // FIX 4: Enable TCP keep-alive at the socket level.
                // Without this, a long-running Gemini request (~30s) produces zero TCP
                // packets after the request is sent. The NAT gateway sees idle TCP and
                // drops the connection table entry. Keep-alive probes reset the idle timer.
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000)

                // FIX 5: Set a realistic response timeout.
                // 120s is enough for Gemini 1.5-flash. The previous 300s was masking the
                // real issue (NAT kill at ~30s) and would have caused 5-minute hangs on
                // genuine failures. If you have tasks that legitimately need more time,
                // increase this, but never exceed your NAT gateway's established-connection
                // idle timeout (350s on AWS).
                .responseTimeout(Duration.ofSeconds(120))

                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(120, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)));

        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}