package org.rm3l.macoui.exceptions;

import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MacOuiExceptionMapper implements ExceptionMapper<Exception> {

  @Override
  public Response toResponse(Exception exception) {
    int code = 500;
    if (exception instanceof MacOuiException) {
      code = ((MacOuiException) exception).status().getStatusCode();
    } else if (exception instanceof IllegalArgumentException) {
      code = 400;
    }
    return Response.status(code)
        .entity(Map.of("error", exception.getMessage(), "code", code))
        .build();
  }
}
