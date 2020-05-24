package org.rm3l.macoui.services.clients.wireshark;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import org.rm3l.macoui.exceptions.MacOuiException;
import org.rm3l.macoui.services.clients.RemoteMacOuiServiceClient;
import org.rm3l.macoui.services.data.MacOui;

/**
 * Fetches data from https://gitlab.com/wireshark/wireshark/raw/master/manuf
 */
@ApplicationScoped
public class WiresharkOuiService implements RemoteMacOuiServiceClient {

  private static final String DATA_URL = "https://gitlab.com/wireshark/wireshark/raw/master/manuf";

  private HttpClient httpClient;

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
        throw new MacOuiException("Unexpected response status code from WiresharkOuiService: " +
            statusCode);
      }
      return httpResponse.body()
          .filter(Predicate.not(line -> line.startsWith("#")))
          .filter(Predicate.not(String::isBlank))
          .map(String::trim)
          .map(line -> line.split("\t", 3))
          .filter(lineDecomposed -> lineDecomposed.length >= 2)
          .map(lineDecomposed -> {
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
}
