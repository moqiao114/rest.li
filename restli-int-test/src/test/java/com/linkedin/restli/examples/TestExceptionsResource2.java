/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.restli.examples;


import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.linkedin.data.DataMap;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.ActionRequest;
import com.linkedin.restli.client.ErrorHandlingBehavior;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.ResponseFuture;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.client.RestLiResponseException;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.client.Exceptions2Builders;
import com.linkedin.restli.internal.server.util.DataMapUtils;


public class TestExceptionsResource2 extends RestLiIntegrationTest
{
  private static final Client CLIENT = new TransportClientAdapter(new HttpClientFactory().getClient(Collections.<String, String>emptyMap()));
  private static final String URI_PREFIX = "http://localhost:1338/";
  private static final RestClient REST_CLIENT = new RestClient(CLIENT, URI_PREFIX);
  private static final Exceptions2Builders EXCEPTIONS_2_BUILDERS = new Exceptions2Builders();

  @BeforeClass
  public void initClass() throws Exception
  {
    super.init();
  }

  @AfterClass
  public void shutDown() throws Exception
  {
    super.shutdown();
  }

  @Test(dataProvider = "exceptionHandlingModes")
  public void testGet(boolean explicit, ErrorHandlingBehavior errorHandlingBehavior) throws RemoteInvocationException
  {
    Response<Greeting> response = null;
    RestLiResponseException exception = null;

    try
    {
      final GetRequest<Greeting> req = new Exceptions2Builders().get().id(1L).build();
      ResponseFuture<Greeting> future;

      if (explicit)
      {
       future = REST_CLIENT.sendRequest(req, errorHandlingBehavior);
      }
      else
      {
        future = REST_CLIENT.sendRequest(req);
      }

      response = future.getResponse();

      if (!explicit || errorHandlingBehavior == ErrorHandlingBehavior.FAIL_ON_ERROR)
      {
        Assert.fail("expected exception");
      }
    }
    catch (RestLiResponseException e)
    {
      if (!explicit || errorHandlingBehavior == ErrorHandlingBehavior.FAIL_ON_ERROR)
      {
        exception = e;
      }
      else
      {
        Assert.fail("not expected exception");
      }
    }

    if (explicit && errorHandlingBehavior == ErrorHandlingBehavior.TREAT_SERVER_ERROR_AS_SUCCESS)
    {
      Assert.assertNotNull(response);
      Assert.assertTrue(response.hasError());
      exception = response.getError();
      Assert.assertEquals(response.getStatus(), HttpStatus.S_500_INTERNAL_SERVER_ERROR.getCode());
      Assert.assertNotNull(response.getEntity());
      Assert.assertEquals(response.getEntity(), new Greeting().setMessage("Hello, sorry for the mess"));
    }

    Assert.assertNotNull(exception);
    Assert.assertTrue(exception.hasDecodedResponse());
    Assert.assertEquals(exception.getStatus(), HttpStatus.S_500_INTERNAL_SERVER_ERROR.getCode());
    final DataMap respEntityMap = DataMapUtils.readMap(exception.getResponse());
    Assert.assertEquals(respEntityMap, new Greeting().setMessage("Hello, sorry for the mess").data());
  }

  @Test(dataProvider = "exceptionHandlingModes")
  public void testExceptionWithValue(boolean explicit, ErrorHandlingBehavior errorHandlingBehavior) throws RemoteInvocationException
  {
    Response<Integer> response = null;
    RestLiResponseException exception = null;

    final ActionRequest<Integer> req = EXCEPTIONS_2_BUILDERS.actionExceptionWithValue().build();
    try
    {
      ResponseFuture<Integer> future;

      if (explicit)
      {
        future = REST_CLIENT.sendRequest(req, errorHandlingBehavior);
      }
      else
      {
        future = REST_CLIENT.sendRequest(req);
      }

      response = future.getResponse();

      if (!explicit || errorHandlingBehavior == ErrorHandlingBehavior.FAIL_ON_ERROR)
      {
        Assert.fail("expected exception");
      }
    }
    catch (RestLiResponseException e)
    {
      if (!explicit || errorHandlingBehavior == ErrorHandlingBehavior.FAIL_ON_ERROR)
      {
        exception = e;
      }
      else
      {
        Assert.fail("not expected exception");
      }
    }

    if (explicit && errorHandlingBehavior == ErrorHandlingBehavior.TREAT_SERVER_ERROR_AS_SUCCESS)
    {
      Assert.assertNotNull(response);
      Assert.assertTrue(response.hasError());
      exception = response.getError();
      Assert.assertEquals(response.getStatus(), HttpStatus.S_500_INTERNAL_SERVER_ERROR.getCode());
      Assert.assertNotNull(response.getEntity());
      Assert.assertSame(response.getEntity(), 42);
    }

    Assert.assertNotNull(exception);
    Assert.assertTrue(exception.hasDecodedResponse());
    Assert.assertEquals(exception.getStatus(), HttpStatus.S_500_INTERNAL_SERVER_ERROR.getCode());
    final DataMap respEntityMap = DataMapUtils.readMap(exception.getResponse());
    Assert.assertSame(respEntityMap.getInteger("value"), 42);
  }

  @Test(dataProvider = "exceptionHandlingModes")
  public void testExceptionWithoutValue(boolean explicit, ErrorHandlingBehavior errorHandlingBehavior) throws RemoteInvocationException
  {
    Response<Void> response = null;
    RestLiResponseException exception = null;

    final ActionRequest<Void> req = EXCEPTIONS_2_BUILDERS.actionExceptionWithoutValue().build();
    try
    {
      ResponseFuture<Void> future;

      if (explicit)
      {
        future = REST_CLIENT.sendRequest(req, errorHandlingBehavior);
      }
      else
      {
        future = REST_CLIENT.sendRequest(req);
      }

      response = future.getResponse();

      if (!explicit || errorHandlingBehavior == ErrorHandlingBehavior.FAIL_ON_ERROR)
      {
        Assert.fail("expected exception");
      }
    }
    catch (RestLiResponseException e)
    {
      if (!explicit || errorHandlingBehavior == ErrorHandlingBehavior.FAIL_ON_ERROR)
      {
        exception = e;
      }
      else
      {
        Assert.fail("not expected exception");
      }
    }

    if (explicit && errorHandlingBehavior == ErrorHandlingBehavior.TREAT_SERVER_ERROR_AS_SUCCESS)
    {
      Assert.assertNotNull(response);
      Assert.assertTrue(response.hasError());
      exception = response.getError();
      Assert.assertEquals(response.getStatus(), HttpStatus.S_500_INTERNAL_SERVER_ERROR.getCode());
      Assert.assertNull(response.getEntity());
    }

    Assert.assertNotNull(exception);
    Assert.assertFalse(exception.hasDecodedResponse());
    Assert.assertEquals(exception.getStatus(), HttpStatus.S_500_INTERNAL_SERVER_ERROR.getCode());
  }

  @Test(dataProvider = "exceptionHandlingModes")
  public void testNonRestException(boolean explicit, ErrorHandlingBehavior errorHandlingBehavior)
  {
    Response<Greeting> response = null;
    RestClient brokenClient = new RestClient(CLIENT, "http://localhost:8888/");
    try
    {
      final GetRequest<Greeting> req = new Exceptions2Builders().get().id(1L).build();
      ResponseFuture<Greeting> future;

      if (explicit)
      {
        future = brokenClient.sendRequest(req, errorHandlingBehavior);
      }
      else
      {
        future = brokenClient.sendRequest(req);
      }

      response = future.getResponse();

      Assert.fail("expected exception");
    }
    catch (RemoteInvocationException e)
    {
      Assert.assertEquals(e.getClass(), RemoteInvocationException.class);
    }
  }

  @DataProvider(name = "exceptionHandlingModes")
  public Object[][] listFactories()
  {
    return new Object[][] {
        { true, ErrorHandlingBehavior.FAIL_ON_ERROR},
        { true, ErrorHandlingBehavior.TREAT_SERVER_ERROR_AS_SUCCESS },
        { false, null }
    };
  }
}
