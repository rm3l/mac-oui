package org.rm3l.macoui.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class MacOuiException extends RuntimeException {

  public MacOuiException() {}

  public MacOuiException(String message) {
    super(message);
  }

  public MacOuiException(String message, Throwable cause) {
    super(message, cause);
  }

  public MacOuiException(Throwable cause) {
    super(cause);
  }

  public MacOuiException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public Response.Status status() {
    return Status.INTERNAL_SERVER_ERROR;
  }
}
