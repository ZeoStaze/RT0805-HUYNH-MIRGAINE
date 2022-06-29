package Rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CorsResponseFilter implements ContainerResponseFilter {

    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String origin = requestContext.getHeaderString("Origin");
        if (origin != null) {
            if (origin.startsWith("http://localhost") || origin.startsWith("http://localhost:8080")) {
                allowExceptionCors(requestContext, responseContext, origin);
            }
        }
    }

    private void allowExceptionCors(ContainerRequestContext requestContext, ContainerResponseContext responseContext,
            String origin) {
        String methodHeader = requestContext.getHeaderString("Access-Control-Request-Method");
        String requestHeaders = requestContext.getHeaderString("Access-Control-Request-Headers");

        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.putSingle("Access-Control-Allow-Origin", origin);
        headers.putSingle("Access-Control-Allow-Credentials", "true");
        headers.putSingle("Access-Control-Allow-Methods", methodHeader);
        headers.putSingle("Access-Control-Allow-Headers",
                "x-requested-with," + (requestHeaders == null ? "" : requestHeaders));
    }
}