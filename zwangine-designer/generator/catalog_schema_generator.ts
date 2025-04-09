
export class SchemaGenerator {
    private readonly schema:  {[index:string]:any}
    constructor(schema:  {[index:string]:any}) {
        this.schema = schema;
    }

    async generateSchema() {
        for (const [key, value] of Object.entries(this.schema)) {
            const propertiesSchema:{[index:string]:any} = {};
            propertiesSchema["properties"] = {}
            propertiesSchema["$schema"]="http://json-schema.org/draft-07/schema#"
            propertiesSchema["type"]="object"
            propertiesSchema["required"]=[]
            const properties = this.getProperties(value);
            this.buildPropertiesSchema(properties, propertiesSchema);
            console.log("propertiesSchema", propertiesSchema);
            this.schema[key]["propertiesSchema"] = propertiesSchema;
        }
        return this.schema;
    }


    private getProperties(schema: { [index: string]: any }): { [index: string]: any } {
        return schema["properties"] as { [index: string]: any };
    }

    private buildPropertiesSchema(proprieties: { [index: string]: any} , propertiesSchema:{[index:string]:any}){
        for (const propertyEntry of Object.entries(proprieties)) {
            this.buildPropertySchema(propertyEntry, propertiesSchema);
        }
    }


    private buildPropertySchema(propertyEntry:[string, any], propertiesSchema: { [index: string]: any }){
        propertiesSchema["properties"][propertyEntry[0]] = {};
        propertiesSchema["properties"][propertyEntry[0]]["title"] = propertyEntry[1]["displayName"];
        propertiesSchema["properties"][propertyEntry[0]]["description"] = propertyEntry[1]["description"];
        propertiesSchema["properties"][propertyEntry[0]]["type"] = propertyEntry[1]["type"];
        propertiesSchema["properties"][propertyEntry[0]]["$comment"] = "group:"+propertyEntry[1]["group"];
        const _enum = propertyEntry[1]["enum"];
        if(_enum){
            if(propertiesSchema["properties"][propertyEntry[0]]["type"] !== "string"){
                propertiesSchema["properties"][propertyEntry[0]]["type"] = "string";
            }
            propertiesSchema["properties"][propertyEntry[0]]["enum"] = _enum;
        }
        const isRequired = propertyEntry[1]["required"];
        if(isRequired){
            propertiesSchema["required"].push(propertyEntry[0]);
        }

    }
}

