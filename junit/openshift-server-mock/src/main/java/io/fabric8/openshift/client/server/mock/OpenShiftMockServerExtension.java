/*
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.openshift.client.server.mock;

import io.fabric8.kubernetes.client.server.mock.KubernetesMixedDispatcher;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServerExtension;
import io.fabric8.mockwebserver.Context;
import io.fabric8.mockwebserver.MockWebServer;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import io.fabric8.openshift.client.NamespacedOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * The class that implements JUnit5 extension mechanism. You can use it directly in your JUnit test
 * by annotating it with <code>@ExtendWith(OpenShiftMockServerExtension.class)</code> or through
 * <code>@EnableOpenShiftMockClient</code> annotation
 */
public class OpenShiftMockServerExtension extends KubernetesMockServerExtension {

  private OpenShiftMockServer staticOpenShiftMockServer;
  private NamespacedOpenShiftClient staticOpenShiftClient;
  private OpenShiftMockServer instantOpenShiftMockServer;
  private NamespacedOpenShiftClient instantOpenShiftClient;

  @Override
  protected void destroyStatic() {
    staticOpenShiftMockServer.destroy();
    staticOpenShiftClient.close();
  }

  @Override
  protected void destroy() {
    if (instantOpenShiftMockServer != null) {
      instantOpenShiftMockServer.destroy();
    }
    if (instantOpenShiftClient != null) {
      instantOpenShiftClient.close();
    }
  }

  @Override
  protected Class<?> getKubernetesMockServerType() {
    return OpenShiftMockServer.class;
  }

  @Override
  protected void initializeKubernetesClientAndMockServer(Class<?> testClass, boolean isStatic) {
    EnableOpenShiftMockClient a = testClass.getAnnotation(EnableOpenShiftMockClient.class);
    final Map<ServerRequest, Queue<ServerResponse>> responses = new HashMap<>();
    OpenShiftMockServer openShiftMockServer = a.crud()
        ? new OpenShiftMockServer(new Context(), new MockWebServer(), responses, new KubernetesMixedDispatcher(responses),
            a.https())
        : new OpenShiftMockServer(a.https());
    openShiftMockServer.init();
    NamespacedOpenShiftClient openShiftClient = openShiftMockServer.createOpenShiftClient();

    if (isStatic) {
      staticOpenShiftMockServer = openShiftMockServer;
      staticOpenShiftClient = openShiftClient;
    } else {
      instantOpenShiftMockServer = openShiftMockServer;
      instantOpenShiftClient = openShiftClient;
    }
  }

  @Override
  protected void setFieldIfKubernetesClientOrMockServer(ExtensionContext context, boolean isStatic, Field field)
      throws IllegalAccessException {
    final NamespacedOpenShiftClient openShiftClient;
    final OpenShiftMockServer openShiftMockServer;
    if (isStatic) {
      openShiftClient = staticOpenShiftClient;
      openShiftMockServer = staticOpenShiftMockServer;
    } else {
      openShiftClient = instantOpenShiftClient;
      openShiftMockServer = instantOpenShiftMockServer;
    }
    setFieldIfEqualsToProvidedType(context, isStatic, field, OpenShiftClient.class, (i, f) -> f.set(i, openShiftClient));
    setFieldIfEqualsToProvidedType(context, isStatic, field, getKubernetesMockServerType(),
        (i, f) -> f.set(i, openShiftMockServer));
  }
}
