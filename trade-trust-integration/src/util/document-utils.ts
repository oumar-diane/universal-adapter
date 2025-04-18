import {DocumentService} from "../service/document-service";


export function prepareDocumentData(documentModel: DocumentService) {
    return {
        "@context": [
            "https://www.w3.org/2018/credentials/v1",
            "https://w3id.org/security/bbs/v1",
            "https://trustvc.io/context/transferable-records-context.json",
            "https://trustvc.io/context/render-method-context.json",
            "https://trustvc.io/context/attachments-context.json",
            documentModel.documentSchema,
        ],
        type: ["VerifiableCredential"],
        "credentialStatus": {
            "type": "TransferableRecords",
            "tokenNetwork": {
                "chain": documentModel.chain,
                "chainId": documentModel.chainId
            },
            "tokenRegistry": documentModel.tokenRegistry,
        },
        "renderMethod": documentModel.renderMethod,
        ...documentModel.credentialSubject,
        "issuanceDate": documentModel.issuanceDate,
        "expirationDate": documentModel.expirationDate,
        "issuer": documentModel.issuer,
    };
}

export function documentMapper(){

}
