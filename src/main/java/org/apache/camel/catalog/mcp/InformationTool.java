package org.apache.camel.catalog.mcp;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.camel.tooling.model.ComponentModel;

public class InformationTool {
    CamelCatalog catalog = new DefaultCamelCatalog(true);

    @Tool(description = "Provide information about an specific Apache Camel component")
    public ToolResponse getInformationAboutComponent(@ToolArg(description = "The name of the component to get information for") String componentName) {
        final String s = catalog.listComponentsAsJson();
        final ComponentModel componentModel = catalog.componentModel(componentName.toLowerCase());

        JsonObject reply = new JsonObject();
        reply.put("kind", componentModel.getKind().toString());
        reply.put("name", componentModel.getName());
        reply.put("title", componentModel.getTitle());
        reply.put("description", componentModel.getDescription());
        reply.put("deprecatedSince", componentModel.getDeprecatedSince());
        reply.put("supportLevel", componentModel.getSupportLevel());
        reply.put("groupId", componentModel.getGroupId());
        reply.put("artifactId", componentModel.getArtifactId());
        reply.put("version", componentModel.getVersion());

        return ToolResponse.success(reply.toString());
    }

    @Tool(description = "Provide information about the configuration options provided by an Apache Camel component")
    public ToolResponse getInformationAboutComponentOptions(@ToolArg(description = "The name of the component to get information for") String componentName) {
        final String s = catalog.listComponentsAsJson();
        final ComponentModel componentModel = catalog.componentModel(componentName.toLowerCase());

        JsonObject reply = new JsonObject();
        JsonArray array = new JsonArray();
        final List<ComponentModel.ComponentOptionModel> componentOptions = componentModel.getComponentOptions();
        for (var option : componentOptions) {
            JsonObject optionObj = new JsonObject();
            optionObj.put("name", option.getName());
            optionObj.put("description", option.getDescription());

            array.add(optionObj);
        }

        reply.put("options", array);

        return ToolResponse.success(reply.toString());
    }

    @Tool(description = "Provide information about an specific configuration option provided by an Apache Camel component")
    public ToolResponse getInformationAboutSpecificComponentOptions(
            @ToolArg(description = "The name of the component to get information for") String componentName,
            @ToolArg(description = "The name of the option to get information for") String optionName) {
        final String s = catalog.listComponentsAsJson();
        final ComponentModel componentModel = catalog.componentModel(componentName.toLowerCase());

        JsonObject reply = new JsonObject();
        final List<ComponentModel.ComponentOptionModel> componentOptions = componentModel.getComponentOptions();
        final ComponentModel.ComponentOptionModel option =
                componentOptions.stream().filter(c -> c.getName().equals(optionName)).findFirst().get();

        reply.put("name", option.getName());
        reply.put("description", option.getDescription());
        reply.put("kind", option.getKind());
        reply.put("type", option.getType());
        reply.put("defaultValue", option.getDefaultValue());

        return ToolResponse.success(reply.toString());
    }
}
