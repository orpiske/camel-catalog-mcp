package net.orpiske.camel.catalog.mcp;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.orpiske.camel.catalog.mcp.exceptions.ComponentNotFoundException;
import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.camel.tooling.model.ComponentModel;

public class InformationTool {
    CamelCatalog catalog = new DefaultCamelCatalog(true);

    @Tool(description = "Provide information about an specific Apache Camel component")
    public ToolResponse getInformationAboutComponent(@ToolArg(description = "The name of the component to get information for") String componentName) {
        final ComponentModel componentModel;
        try {
            componentModel = findComponent(componentName);
        } catch (ComponentNotFoundException e) {
            return ToolResponse.error(e.getMessage());
        }

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
        final ComponentModel componentModel;
        try {
            componentModel = findComponent(componentName);
        } catch (ComponentNotFoundException e) {
            return ToolResponse.error(e.getMessage());
        }

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
        final ComponentModel componentModel;
        try {
            componentModel = findComponent(componentName);
        } catch (ComponentNotFoundException e) {
            return ToolResponse.error(e.getMessage());
        }

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


    @Tool(description = "Provide information about the endpoint options provided by an Apache Camel component")
    public ToolResponse getInformationAboutQueryParameterOptions(@ToolArg(description = "The name of the component to get information for") String componentName) {
        final ComponentModel componentModel;
        try {
            componentModel = findComponent(componentName);
        } catch (ComponentNotFoundException e) {
            return ToolResponse.error(e.getMessage());
        }

        JsonObject reply = new JsonObject();
        JsonArray array = new JsonArray();
        final List<ComponentModel.EndpointOptionModel> componentOptions = componentModel.getEndpointOptions();
        for (var option : componentOptions) {
            JsonObject optionObj = new JsonObject();
            optionObj.put("name", option.getName());
            optionObj.put("kind", option.getKind());
            optionObj.put("description", option.getDescription());

            array.add(optionObj);
        }

        reply.put("options", array);

        return ToolResponse.success(reply.toString());
    }

    @Tool(description = "Provide information about an specific configuration option provided by an Apache Camel endpoint option")
    public ToolResponse getInformationAboutSpecificEndpointOptions(
            @ToolArg(description = "The name of the component to get information for") String componentName,
            @ToolArg(description = "The name of the option to get information for") String optionName) {
        final ComponentModel componentModel;
        try {
            componentModel = findComponent(componentName);
        } catch (ComponentNotFoundException e) {
            return ToolResponse.error(e.getMessage());
        }

        JsonObject reply = new JsonObject();
        final List<ComponentModel.EndpointOptionModel> componentOptions = componentModel.getEndpointOptions();
        final ComponentModel.EndpointOptionModel option =
                componentOptions.stream().filter(c -> c.getName().equals(optionName)).findFirst().get();

        reply.put("name", option.getName());
        reply.put("description", option.getDescription());
        reply.put("kind", option.getKind());
        reply.put("type", option.getType());
        reply.put("defaultValue", option.getDefaultValue());

        return ToolResponse.success(reply.toString());
    }

    @Tool(description = "Provide dependency information about component for using with Maven or Gradle")
    public ToolResponse getDependency(
            @ToolArg(description = "The name of the component to get the dependency information for") String componentName) {
        final ComponentModel componentModel;
        try {
            componentModel = findComponent(componentName);
        } catch (ComponentNotFoundException e) {
            return ToolResponse.error(e.getMessage());
        }

        JsonObject reply = new JsonObject();
        reply.put("groupId", componentModel.getGroupId());
        reply.put("artifactId", componentModel.getArtifactId());
        reply.put("version", componentModel.getVersion());

        return ToolResponse.success(reply.toString());
    }

    private ComponentModel findComponent(String componentName) throws ComponentNotFoundException {
        final String adjustedComponentName = componentName.toLowerCase();

        ComponentModel componentModel = catalog.componentModel(adjustedComponentName);

        if (componentModel == null) {
            if (componentName.startsWith("camel-")) {
                componentModel =  catalog.componentModel(adjustedComponentName.replace("camel-", ""));
                if (componentModel == null) {
                    throw new ComponentNotFoundException("The component name " + componentName + " does not exist");
                }
            }
        }

        return componentModel;
    }

    @Tool(description = "Get the URL for the documentation of an Apache Camel component")
    public ToolResponse getComponentURL(
            @ToolArg(description = "The name of the component to get the documentation for") String componentName) {
        final ComponentModel componentModel;
        try {
            componentModel = findComponent(componentName);
        } catch (ComponentNotFoundException e) {
            return ToolResponse.error(e.getMessage());
        }



        String page = "https://camel.apache.org/components/4.14.x/" + componentModel.getName() + "-component.html";

        JsonObject reply = new JsonObject();
        reply.put("page", page);

        return ToolResponse.success(reply.toString());
    }
}
