package org.rm3l.macoui.resources.management;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.rm3l.macoui.services.MacOuiService;

@Path("/management")
public class ManagementResource {

  MacOuiService macOuiService;

  public ManagementResource(MacOuiService macOuiService) {
    this.macOuiService = macOuiService;
  }

  @GET
  @Path("/ping")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ping() {
    return Response.noContent().build();
  }

  @GET
  @Path("/health")
  @Produces(MediaType.APPLICATION_JSON)
  public Response health() {
    final var status =
        macOuiService
            .lookup("00-00-00")
            .map(macOui -> Status.NO_CONTENT)
            .orElse(Status.SERVICE_UNAVAILABLE);
    return Response.status(status).build();
  }
}
