package net.orpiske.camel.catalog.mcp;

import jakarta.inject.Singleton;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.function.Supplier;
import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
public class ListTool {
    CamelCatalog catalog = new DefaultCamelCatalog(true);

    @ConfigProperty(name = "page.size", defaultValue = "50")
    int pageSize;

    private ToolResponse list(Supplier<String> listSupplier) {
        try {
            return ToolResponse.success(listSupplier.get());
        } catch (Exception e) {
            return ToolResponse.error(e.getMessage());
        }
    }

    @Tool(description = "Calculates the total number of pages for a 'listComponents' query")
    ToolResponse calculateComponentPages(@ToolArg(description = "Version (unused)") String version) {
        final String s = catalog.listComponentsAsJson();
        JsonArray catalogInformation = new JsonArray(s);

        int numPages = catalogInformation.size() % pageSize;

        return ToolResponse.success(String.valueOf(numPages));
    }


    @Tool(description = "Lists available Apache Camel components from the latest stable version. The results are paginated.")
    ToolResponse listComponents(@ToolArg(description = "Optional: The page number for the results, starting from 0. Defaults to 0 (the first page) if omitted.", required = false) Integer pageObj) {
        final String s = catalog.listComponentsAsJson();
        JsonArray catalogInformation = new JsonArray(s);
        JsonObject reply = new JsonObject();
        JsonArray smallerArray = new JsonArray();

        int page = pageObj == null ? 0 : pageObj;

        int numPages = catalogInformation.size() % pageSize;
        if (page > numPages) {
            ToolResponse.error("Requested more pages than available");
        }

        int start = page * pageSize;
        for (int i = 0; (i + start < catalogInformation.size() && i < pageSize); i++) {
            final JsonObject originalObject = catalogInformation.getJsonObject(i + start);
            JsonObject simplerObject = new JsonObject();

            simplerObject.put("name", originalObject.getValue("name"));
            simplerObject.put("description", originalObject.getValue("description"));
            smallerArray.add(simplerObject);
        }
        reply.put("components", smallerArray);
        reply.put("numPages", numPages);

        return ToolResponse.success(reply.toString());
    }

    @Tool(description = "Retrieves a list of all available Apache Camel data formats from the latest stable version. This tool does not support filtering")
    ToolResponse listDataFormats(@ToolArg(description = "Version (unused)") String version) {
        return list(catalog::listDataFormatsAsJson);
    }

    @Tool(description = "Retrieves a list of all available Apache Camel Domain-specific languages (DSL) from the latest stable version. This tool does not support filtering")
    ToolResponse listLanguages(@ToolArg(description = "Version (unused)") String version) {
        return list(catalog::listLanguagesAsJson);
    }

    @Tool(description = "Retrieves a list of all available Apache Camel transformers from the latest stable version. This tool does not support filtering")
    ToolResponse listTransformers(@ToolArg(description = "Version (unused)") String version) {
        return list(catalog::listTransformersAsJson);
    }
}
