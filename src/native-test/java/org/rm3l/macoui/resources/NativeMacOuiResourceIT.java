package org.rm3l.macoui.resources;

import io.quarkus.test.junit.NativeImageTest;
import org.rm3l.macoui.resources.management.MacOuiManagementResourceTest;

@NativeImageTest
public class NativeMacOuiResourceIT extends MacOuiManagementResourceTest {

  // Execute the same tests but in native mode.
}
