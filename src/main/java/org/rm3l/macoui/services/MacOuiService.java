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
package org.rm3l.macoui.services;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.rm3l.macoui.services.clients.RemoteMacOuiServiceClient;
import org.rm3l.macoui.services.data.MacOui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MacOuiService {

  private final Logger logger = LoggerFactory.getLogger(MacOuiService.class);

  Instance<RemoteMacOuiServiceClient> remoteMacOuiServiceClients;

  private final Map<String, MacOui> database;

  public MacOuiService(Instance<RemoteMacOuiServiceClient> remoteMacOuiServiceClients) {
    this.database = Collections.synchronizedMap(new HashMap<>());
    this.remoteMacOuiServiceClients = remoteMacOuiServiceClients;
  }

  // This is to make sure the database is filled at least once at startup
  void startup(@Observes StartupEvent startupEvent) {
    this.scheduleDatabaseUpdate();
  }

  @SuppressWarnings({"unchecked", "unused"})
  @Scheduled(cron = "{mac-oui.database.updateFrequency:0 1 1 ? * SUN *}")
  void scheduleDatabaseUpdate() {
    final var start = System.nanoTime();
    logger.info(
        "Updating local database using {} RemoteMacOuiServiceClients...",
        remoteMacOuiServiceClients.stream().count());
    try {
      remoteMacOuiServiceClients.stream()
          .parallel()
          .peek(remoteMacOuiServiceClient -> logger.debug("Using {}", remoteMacOuiServiceClient))
          .map(
              remoteMacOuiServiceClient ->
                  new Object[] {remoteMacOuiServiceClient, remoteMacOuiServiceClient.fetchData()})
          .forEach(
              serviceAndMacOuiSet -> {
                final var remoteMacOuiServiceClient = serviceAndMacOuiSet[0];
                if (serviceAndMacOuiSet[1] == null) {
                  return;
                }
                final var macOuiSet = (Set<MacOui>) serviceAndMacOuiSet[1];
                logger.debug("{} returned {} records", remoteMacOuiServiceClient, macOuiSet.size());
                if (macOuiSet.isEmpty()) {
                  return;
                }
                synchronized (database) {
                  database.putAll(
                      macOuiSet.stream()
                          .filter(Objects::nonNull)
                          .filter(
                              macOui -> macOui.getPrefix() != null && !macOui.getPrefix().isBlank())
                          .collect(
                              Collectors.toMap(
                                  macOui -> sanitizeMac(macOui.getPrefix()).toLowerCase(),
                                  Function.identity())));
                }
              });
    } catch (final Exception e) {
      logger.warn(
          "An error occurred while attempting to update local database: {}. Will try again later.",
          e.getMessage(),
          e);
    } finally {
      logger.info(
          "... done updating local database in {} ms",
          TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
    }
  }

  public Optional<MacOui> lookup(@NotNull final String filter) {
    final var filterSanitized = sanitizeMac(filter);
    if (filterSanitized.length() < 6) {
      throw new IllegalArgumentException("Invalid MAC filter data: '" + filter + "'");
    }
    return Optional.ofNullable(database.get(filterSanitized.substring(0, 6).toLowerCase()));
  }

  private String sanitizeMac(@NotNull final String mac) {
    return mac.replaceAll("-", "").replaceAll(":", "");
  }

  @ApplicationScoped
  @Readiness
  public static class HealthProbe implements HealthCheck {

    private static final String HEALTH_CHECK_MAC_ADDR_PREFIX = "00-00-00";
    final Logger logger = LoggerFactory.getLogger(HealthProbe.class);

    MacOuiService macOuiService;

    HealthProbe(MacOuiService macOuiService) {
      this.macOuiService = macOuiService;
    }

    @Override
    public HealthCheckResponse call() {
      if (logger.isDebugEnabled()) {
        logger.debug("Readiness check on {}", this);
      }
      final var healthCheckResponseBuilder = HealthCheckResponse.named("mac-oui-lookup");
      final var lookup = macOuiService.lookup(HEALTH_CHECK_MAC_ADDR_PREFIX);
      if (lookup.isPresent()) {
        healthCheckResponseBuilder.up();
      } else {
        healthCheckResponseBuilder
            .down()
            .withData(
                "error", "Could not find any data for MAC prefix: " + HEALTH_CHECK_MAC_ADDR_PREFIX);
      }
      return healthCheckResponseBuilder.build();
    }
  }
}
