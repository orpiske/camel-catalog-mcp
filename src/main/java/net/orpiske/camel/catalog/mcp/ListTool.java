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

    @Tool(description = "Calculate the number of pages required to retrieve all results of a certain type")
    ToolResponse calculateComponentPages(@ToolArg(description = "Version (unused)") String version) {
        final String s = catalog.listComponentsAsJson();
        JsonArray catalogInformation = new JsonArray(s);

        int numPages = catalogInformation.size() % pageSize;

        return ToolResponse.success(String.valueOf(numPages));
    }


    @Tool(description = "List Apache Camel components available in the latest version")
    ToolResponse listComponents(@ToolArg(description = "Page number (to set the starting page for the results - starts with 0)", required = false) Integer pageObj) {
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

    @Tool(description = "List Apache Camel dataFormats for the latest version")
    ToolResponse listDataFormats(@ToolArg(description = "Version (unused)") String version) {
        return list(catalog::listDataFormatsAsJson);
    }

    @Tool(description = "List Apache Camel languages (DSLs)")
    ToolResponse listLanguages(@ToolArg(description = "Version (unused)") String version) {
        return list(catalog::listLanguagesAsJson);
    }

    @Tool(description = "List Apache Camel transformers")
    ToolResponse listTransformers(@ToolArg(description = "Version (unused)") String version) {
        return list(catalog::listTransformersAsJson);
    }
}
