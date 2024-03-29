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
package org.rm3l.macoui.resources;

import jakarta.inject.Inject;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.rm3l.macoui.services.MacOuiService;
import org.rm3l.macoui.services.data.MacOui;

@Path("/")
public class MacOuiResource {

  @Inject MacOuiService macOuiService;

  @GET
  @Path("/{macAddressOrPrefix}")
  @Produces(MediaType.APPLICATION_JSON)
  public MacOuiResourceResponse lookup(
      @NotNull
          @Parameter(required = true, description = "MAC Address or OUI Prefix")
          @PathParam("macAddressOrPrefix")
          String macAddressOrPrefix) {
    return getMacOuiResourceResponse(macAddressOrPrefix);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public MacOuiResourceResponse lookupByMac(
      @Parameter(required = true, description = "MAC Address or OUI Prefix")
          @DefaultValue("")
          @QueryParam("mac")
          String mac) {
    return getMacOuiResourceResponse(mac);
  }

  private MacOuiResourceResponse getMacOuiResourceResponse(@NotNull String macAddressOrPrefix) {
    return macOuiService
        .lookup(macAddressOrPrefix)
        .map(macOui -> new MacOuiResourceResponse().setData(macOui))
        .orElseThrow(() -> new NotFoundException("Not found: " + macAddressOrPrefix));
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
