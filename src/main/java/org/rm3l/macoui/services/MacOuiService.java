package org.rm3l.macoui.services;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.validation.constraints.NotNull;
import org.rm3l.macoui.services.clients.RemoteMacOuiServiceClient;
import org.rm3l.macoui.services.data.MacOui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MacOuiService {

  private final Logger logger = LoggerFactory.getLogger(MacOuiService.class);

  Instance<RemoteMacOuiServiceClient> remoteMacOuiServiceClients;

  private final Set<MacOui> database;

  public MacOuiService(Instance<RemoteMacOuiServiceClient> remoteMacOuiServiceClients) {
    this.database = Collections.synchronizedSet(new HashSet<>());
    this.remoteMacOuiServiceClients = remoteMacOuiServiceClients;
  }

  void startup(@Observes StartupEvent startupEvent) {
    this.scheduleDatabaseUpdate();
  }

  @Scheduled(cron = "{mac-oui.database.updateFrequency}")
  void scheduleDatabaseUpdate() {
    final var start = System.nanoTime();
    logger.info("Updating local database...");
    remoteMacOuiServiceClients.stream()
        .peek(remoteMacOuiServiceClient -> logger.debug("Using {}", remoteMacOuiServiceClient))
        .map(RemoteMacOuiServiceClient::fetchData)
        .filter(Predicate.not(Collection::isEmpty))
        .forEach(
            macOuiSet -> {
              synchronized (database) {
                database.addAll(macOuiSet);
              }
            });
    logger.info(
        "... done updating local database in {} ms",
        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
  }

  public Optional<MacOui> lookup(@NotNull final String filter) {
    final var filterSanitized = sanitizeMac(filter);
    if (filterSanitized.length() < 6) {
      throw new IllegalArgumentException("Invalid MAC filter data: '" + filter + "'");
    }
    final var filterPrefix = filterSanitized.substring(0, 6);
    return database.stream()
        .filter(Objects::nonNull)
        .filter(
            macOui -> {
              if (filter.equalsIgnoreCase(macOui.getPrefix())) {
                return true;
              }

              if (macOui.getPrefix() != null) {
                return sanitizeMac(macOui.getPrefix())
                    .toLowerCase()
                    .startsWith(filterPrefix.toLowerCase());
              }
              return false;
            })
        .findAny();
  }

  private String sanitizeMac(@NotNull final String mac) {
    return mac.replaceAll("-", "").replaceAll(":", "");
  }
}
