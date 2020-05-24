package org.rm3l.macoui.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ItemNotFoundException extends MacOuiException {

  public ItemNotFoundException() {}

  public ItemNotFoundException(String message) {
    super(message);
  }

  public ItemNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ItemNotFoundException(Throwable cause) {
    super(cause);
  }

  public ItemNotFoundException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  @Override
  public Response.Status status() {
    return Status.NOT_FOUND;
  }
}
