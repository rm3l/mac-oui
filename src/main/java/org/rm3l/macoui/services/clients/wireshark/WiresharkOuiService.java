// The MIT License (MIT)
//
// Copyright (c) 2020 Armel Soro
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package org.rm3l.macoui.services.clients.wireshark;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.rm3l.macoui.exceptions.MacOuiException;
import org.rm3l.macoui.services.clients.RemoteMacOuiServiceClient;
import org.rm3l.macoui.services.data.MacOui;

/**
 * Fetches data from <a href="https://gitlab.com/wireshark/wireshark/-/raw/v4.0.8/manuf">Wireshark
 * list</a>
 */
@ApplicationScoped
public class WiresharkOuiService implements RemoteMacOuiServiceClient {

  static final String DATA_URL = "https://gitlab.com/wireshark/wireshark/-/raw/v4.0.8/manuf";

  HttpClient httpClient;

  @PostConstruct
  void init() {
    this.httpClient = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build();
  }

  @Override
  @NotNull
  public Set<MacOui> fetchData() {
    final var httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(DATA_URL)).build();
    try {
      final var httpResponse = this.httpClient.send(httpRequest, BodyHandlers.ofLines());
      final var statusCode = httpResponse.statusCode();
      if (statusCode < 200 || statusCode > 299) {
        throw new MacOuiException(
            "Unexpected response status code from WiresharkOuiService: " + statusCode);
      }
      return httpResponse
          .body()
          .filter(Predicate.not(line -> line.startsWith("#")))
          .filter(Predicate.not(String::isBlank))
          .map(String::trim)
          .map(line -> line.split("\t", 3))
          .filter(lineDecomposed -> lineDecomposed.length >= 2)
          .map(
              lineDecomposed -> {
                final var prefix = lineDecomposed[0];
                String manufacturerAndComment = null;
                if (lineDecomposed.length == 3) {
                  manufacturerAndComment = lineDecomposed[2];
                } else if (lineDecomposed.length == 2) {
                  manufacturerAndComment = lineDecomposed[1];
                }

                final var macOui = new MacOui();
                macOui.setPrefix(prefix.trim());

                if (manufacturerAndComment != null) {
                  final var manufacturerAndCommentDecomposed = manufacturerAndComment.split("#", 2);
                  if (manufacturerAndCommentDecomposed.length == 2) {
                    macOui.setManufacturer(manufacturerAndCommentDecomposed[0].trim());
                    macOui.setComment(manufacturerAndCommentDecomposed[1].trim());
                  } else if (manufacturerAndCommentDecomposed.length == 1) {
                    macOui.setManufacturer(manufacturerAndCommentDecomposed[0].trim());
                  }
                }
                return macOui;
              })
          .collect(Collectors.toSet());
    } catch (IOException | InterruptedException e) {
      throw new MacOuiException(e);
    }
  }

  @ApplicationScoped
  @Liveness
  public static class HealthProbe implements HealthCheck {

    WiresharkOuiService wiresharkOuiService;

    HttpClient healthHttpClient;

    public HealthProbe(WiresharkOuiService wiresharkOuiService) {
      this.wiresharkOuiService = wiresharkOuiService;
    }

    @PostConstruct
    void init() {
      this.healthHttpClient = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build();
    }

    @Override
    public HealthCheckResponse call() {
      final var responseBuilder = HealthCheckResponse.named("wireshark");
      try {
        final var headRequest =
            HttpRequest.newBuilder()
                .method("HEAD", BodyPublishers.noBody())
                .uri(URI.create(DATA_URL))
                .timeout(Duration.ofSeconds(5))
                .build();
        final var statusCode =
            healthHttpClient.send(headRequest, BodyHandlers.discarding()).statusCode();
        if (statusCode < 200 || statusCode > 299) {
          responseBuilder.down().withData("status_code", statusCode);
        } else {
          responseBuilder.up();
        }
      } catch (final Exception e) {
        responseBuilder.down().withData("error", e.getMessage());
      }
      return responseBuilder.build();
    }
  }
}
