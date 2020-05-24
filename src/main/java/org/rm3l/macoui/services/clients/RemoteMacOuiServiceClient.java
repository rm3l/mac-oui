package org.rm3l.macoui.services.clients;

import java.util.Set;
import javax.validation.constraints.NotNull;
import org.rm3l.macoui.services.data.MacOui;

public interface RemoteMacOuiServiceClient {

  @NotNull
  Set<MacOui> fetchData();
}
