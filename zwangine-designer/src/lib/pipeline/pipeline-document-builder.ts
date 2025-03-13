import { Document } from 'yaml'


export class PipelineDocumentUtils {
    static newObject():{[index: string]:any}{
        return {}
    }
}

/*
* write pipeline flows in memory
* */
export class PipelineNodeBuilder {

    public static buildFrom(uri:string):{[index: string]:any} {
        const from = PipelineDocumentUtils.newObject()
        from["from"] = PipelineDocumentUtils.newObject()
        from["from"]["uri"] = uri
        from["from"]["steps"] = []
        return from
    }

    public static buildTo(uri:string):{[index: string]:any}{
        const tooNode = PipelineDocumentUtils.newObject()
        tooNode["to"] = uri
        return tooNode
    }

    public static buildWhenSimpleCondition(whenCondition:string){
        const simpleCondition  = PipelineDocumentUtils.newObject()
        simpleCondition["simple"] = whenCondition
        simpleCondition["steps"] = []
        const choiceNode= PipelineDocumentUtils.newObject()
        choiceNode["choice"] = PipelineDocumentUtils.newObject()
        choiceNode["choice"]["when"] = []
        choiceNode["choice"]["when"].push(simpleCondition)
        return choiceNode
    }

    public static appendOtherwise(choice:{[index: string]:any}){
        choice["choice"]["otherwise"] = PipelineDocumentUtils.newObject()
        choice["choice"]["otherwise"]["steps"] = []
        return choice
    }

    public static  getDocumentAsJsonString(document:{[index:string]:any}){
        return JSON.stringify(document)
    }

    public static getDocumentAsYamlString(document:{[index:string]:any}):string{
        const yaml =  new Document([])
        yaml.add(document)
        return String(yaml)
    }
}