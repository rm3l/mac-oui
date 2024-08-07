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
package org.rm3l.macoui.exceptions;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class MacOuiExceptionMapper implements ExceptionMapper<Exception> {

  private static final Logger logger = LoggerFactory.getLogger(MacOuiExceptionMapper.class);

  @Override
  public Response toResponse(Exception exception) {
    int code = 500;
    if (exception instanceof MacOuiException) {
      code = ((MacOuiException) exception).status().getStatusCode();
    } else if (exception instanceof IllegalArgumentException
        || exception instanceof BadRequestException) {
      code = 400;
    } else if (exception instanceof NotFoundException) {
      code = 404;
    }
    final var rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
    if (logger.isDebugEnabled()) {
      logger.debug("Exception caught: {} {}", exception.getClass(), rootCauseMessage);
      logger.debug(exception.getMessage(), exception);
    }
    return Response.status(code).entity(Map.of("code", code, "error", rootCauseMessage)).build();
  }
}
