import {SchemaGenerator} from "./catalog_schema_generator";
// @ts-ignore
import component_catalog from "./component-catalog.json";
import {SchemaWriter} from "./catalog_schema_writer";


const schemaGenerator = new SchemaGenerator(component_catalog);

schemaGenerator.generateSchema().then((schema)=>{
    SchemaWriter.writeToFile(schema, "./public/resources/component-catalog.json");
    SchemaWriter.writeToFile(schema, "./src/assets/resources/component-catalog.json");
}).catch((error) => {
    console.error("Error generating schema: ", error);
})

