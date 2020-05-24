package org.rm3l.macoui.resources;

import javax.inject.Inject;
import javax.json.bind.annotation.JsonbNillable;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.rm3l.macoui.exceptions.ItemNotFoundException;
import org.rm3l.macoui.services.MacOuiService;
import org.rm3l.macoui.services.data.MacOui;

@Path("/")
public class MacOuiResource {

  @Inject MacOuiService macOuiService;

  @GET
  @Path("/{macAddressOrPrefix}")
  @Produces(MediaType.APPLICATION_JSON)
  public MacOuiResourceResponse lookup(@PathParam("macAddressOrPrefix") String macAddressOrPrefix) {
    return getMacOuiResourceResponse(macAddressOrPrefix);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public MacOuiResourceResponse lookupByMac(@QueryParam("mac") String mac) {
    return getMacOuiResourceResponse(mac);
  }

  private MacOuiResourceResponse getMacOuiResourceResponse(@NotNull String macAddressOrPrefix) {
    return macOuiService
        .lookup(macAddressOrPrefix)
        .map(macOui -> new MacOuiResourceResponse().setData(macOui))
        .orElseThrow(() -> new ItemNotFoundException("Not found: " + macAddressOrPrefix));
  }

  @JsonbNillable
  public static class MacOuiResourceResponse {

    private MacOui data;

    public MacOui getData() {
      return data;
    }

    public MacOuiResourceResponse setData(MacOui data) {
      this.data = data;
      return this;
    }
  }
}
