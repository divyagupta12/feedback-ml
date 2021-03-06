package com.feedback.back.app;

import com.feedback.back.resources.RecordResource;
import io.swagger.jaxrs.config.BeanConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import java.util.EnumSet;


/**
 * Main class.
 */
public class Main
{

    private static final int PORT = 9999;


    public static void main( String[] args ) throws Exception
    {

        int serverPort = PORT;


        // Workaround for resources from JAR files
        Resource.setDefaultUseCaches( false );

        // Build the Swagger Bean.
        buildSwagger();

        // Holds handlers
        final HandlerList handlers = new HandlerList();

        // Handler for Reviews Service and Swagger
        handlers.addHandler( enableCors( buildContext() ) );

        // Start server
        Server server = new Server( serverPort );
        server.setHandler( handlers );
        server.start();
        server.join();
    }


    private static void buildSwagger()
    {
        // This configures Swagger
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion( "1.0.0" );
        beanConfig.setResourcePackage( RecordResource.class.getPackage().getName() );
        beanConfig.setScan( true );
        beanConfig.setBasePath( "/" );
        beanConfig.setDescription( "Entity Browser API to demonstrate Swagger with Jersey2 in an "
            + "embedded Jetty instance, with no web.xml or Spring MVC." );
        beanConfig.setTitle( "Entity Browser" );
    }


    private static ContextHandler buildContext()
    {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register( JacksonFeature.class );
        // Replace EntityBrowser with your resource class
        // com.wordnik.swagger.jaxrs.listing loads up Swagger resources
        resourceConfig.packages( RecordResource.class.getPackage().getName(), "com.wordnik.swagger.jaxrs.listing" );
        ServletContainer servletContainer = new ServletContainer( resourceConfig );
        ServletHolder entityBrowser = new ServletHolder( servletContainer );
        ServletContextHandler entityBrowserContext = new ServletContextHandler( ServletContextHandler.SESSIONS );
        entityBrowserContext.setContextPath( "/" );
        entityBrowserContext.addServlet( entityBrowser, "/*" );

        return entityBrowserContext;
    }


    // This starts the Swagger UI at http://localhost:9999/docs
    //    private static ContextHandler buildSwaggerUI() throws Exception {
    //        final ResourceHandler swaggerUIResourceHandler = new ResourceHandler();
    //        swaggerUIResourceHandler.setResourceBase(
    //            App.class.getClassLoader().getResource("webapp").toURI().toString());
    //        final ContextHandler swaggerUIContext = new ContextHandler();
    //        swaggerUIContext.setContextPath("/docs/");
    //        swaggerUIContext.setHandler(swaggerUIResourceHandler);
    //
    //        return swaggerUIContext;
    //    }


    public static ContextHandler enableCors( ContextHandler handler )
    {
        FilterHolder cors = ( (ServletContextHandler) handler )
            .addFilter( CrossOriginFilter.class, "/*", EnumSet.of( DispatcherType.REQUEST ) );
        cors.setInitParameter( CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*" );
        cors.setInitParameter( CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*" );
        cors.setInitParameter( CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD" );
        cors.setInitParameter( CrossOriginFilter.ALLOWED_HEADERS_PARAM,
            "X-Requested-With,Accept,Origin,customer-key,Content-type,X-Authurization-Header" );
        cors.setInitParameter( CrossOriginFilter.ACCESS_CONTROL_ALLOW_HEADERS_HEADER,
            "customer-key,Content-type,X-Authurization-Header" );
        return handler;
    }


    //    public static ContextHandler enableAuth( ContextHandler handler )
    //    {
    //        ( (ServletContextHandler) handler ).addFilter( AuthFilter.class, "/*", EnumSet.of( DispatcherType.REQUEST ) );
    //        return handler;
    //    }

}

