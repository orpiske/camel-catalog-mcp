
1. `export PREVIOUS_VERSION=0.0.0 CURRENT_DEVELOPMENT_VERSION=0.0.1 NEXT_DEVELOPMENT_VERSION=0.0.2`
2. `mvn release:clean`
3. `mvn --batch-mode -Dtag=camel-catalog-mcp-${CURRENT_DEVELOPMENT_VERSION} release:prepare -DreleaseVersion=${CURRENT_DEVELOPMENT_VERSION} -DdevelopmentVersion=${NEXT_DEVELOPMENT_VERSION}-SNAPSHOT`
4. `mvn -Pdist release:perform -Dgoals=install`
5. `git checkout camel-catalog-mcp-${CURRENT_DEVELOPMENT_VERSION} && mvn -Pdist clean package`