import {
    CHAIN_ID,
    encrypt, getTitleEscrowAddress,
    getTokenId,
    signW3C,
    SUPPORTED_CHAINS, v5Contracts,
    verifyDocument
} from "@trustvc/trustvc";
import {SUPPORTED_DOCUMENT} from "../configs";
import {getRPCUrl} from "../util/provider-utils";
import {DocumentModel, DocumentTransferabilityModel, TransferabilityActions} from "../model/document-model";
import {ethers, Wallet} from "ethers";
import {TradeTrustToken__factory} from "@trustvc/trustvc/token-registry-v5/contracts";

export class DocumentService {
    private static CHAINID: CHAIN_ID = process.env.NET as CHAIN_ID ?? CHAIN_ID.amoy;
    private  static CHAININFO = SUPPORTED_CHAINS[this.CHAINID];
    private static SYSTEM_TOKEN_REGISTRY_ADDRESS = process.env.TOKEN_REGISTRY_ADDRESS || '';

    async applyVerification(signedW3CDocument: any) {
        // Validate required parameters
        if (!signedW3CDocument) {
            throw new Error('Signed W3C document is required');
        }
        // Verify the document
        return await verifyDocument(signedW3CDocument);
    }

    async applyDocumentTransferabilityAction(transferabilityData:DocumentTransferabilityModel , vc:any, action:TransferabilityActions) {
        // Validate required parameters
        if (!vc) {
            throw new Error('Signed W3C document is required');
        }
        if (!action) {
            throw new Error('action is required');
        }

        Object.assign(DocumentService.CHAININFO, {
            rpcUrl:getRPCUrl(DocumentService.CHAINID) ?? DocumentService.CHAININFO.rpcUrl
        })

        const _provider =  this.getProvider(vc);
        if (!_provider) return;

        const titleEscrowAddress = await getTitleEscrowAddress(
            (vc.credentialStatus as any ).tokenRegistry,
            "0x" + (vc.credentialStatus as any)?.tokenId,
            _provider,
        );

        const params =  await this.getActionParams(action, vc, transferabilityData) as any[]
        const contract = new ethers.Contract(titleEscrowAddress,  v5Contracts.TitleEscrow__factory.abi, _provider);
        const tx = await contract[action](...params);
        console.log("Transaction sent:", tx.hash);

        // Wait for transaction confirmation
        await tx.wait();
        console.log("Transaction confirmed");
        return {
            transactionHash: tx.hash,
            action: action,
        }

    }

    async createDocument(rawDocument: DocumentModel) {

        Object.assign(DocumentService.CHAININFO, {
            rpcUrl:getRPCUrl(DocumentService.CHAINID) ?? DocumentService.CHAININFO.rpcUrl
        })
        // Remove escaped characters before parsing
        const cleanedJsonString = process.env.DID_KEY_PAIRS?.replace(/\\(?=["])/g, '') || '';
        const DID_KEY_PAIRS = JSON.parse(cleanedJsonString);

        // Prepare the document
        const issuanceDate = new Date();
        const expirationDate = this.applyExpirationDate(rawDocument.expirationDate as string);
        const document = this.getDocumentSchema(rawDocument, issuanceDate, expirationDate, DID_KEY_PAIRS);

        // Sign the document
        const { error, signed: signedW3CDocument } = await signW3C(document, DID_KEY_PAIRS);
        if (error) {
            throw new Error(error);
        }

        // Issue the document on chain:
        const tokenId = getTokenId(signedW3CDocument!);
        const unconnectedWallet = new Wallet(process.env.WALLET_PRIVATE_KEY!);
        const provider = this.getProvider(signedW3CDocument)
        const wallet = unconnectedWallet.connect(provider);
        const tokenRegistry = new ethers.Contract(
            DocumentService.SYSTEM_TOKEN_REGISTRY_ADDRESS,
            TradeTrustToken__factory.abi,
            wallet
        );

        // Encrypt remarks
        const encryptedRemarks = rawDocument.remarks && encrypt(rawDocument.remarks ?? '', signedW3CDocument?.id!) || '0x'

        // mint the document
        try {
            const mintTx = await tokenRegistry.mint.staticCall(rawDocument.owner, rawDocument.holder, tokenId, encryptedRemarks);
        } catch (error) {
            console.error(error);
            throw new Error('Failed to mint token');
        }
        const receipt = await this.mineDocument(tokenRegistry, rawDocument, tokenId, encryptedRemarks);
        console.log(`Document ${rawDocument.documentId} minted on tx hash ${receipt?.hash}`);
        return {
            signedW3CDocument: signedW3CDocument,
            txHash: receipt.hash,
        }
    }

    private applyExpirationDate(expirationDate:string){
        const defaultExpirationDate = new Date();
        defaultExpirationDate.setMonth(defaultExpirationDate.getMonth() + 3);
        return (expirationDate != undefined && expirationDate != '') ? new Date(expirationDate): defaultExpirationDate;
    }

    private getProvider(signedW3CDocument?:any){
        // Issue the document on chain:
        const tokenId = getTokenId(signedW3CDocument as any);
        const unconnectedWallet = new Wallet(process.env.WALLET_PRIVATE_KEY!);
        let provider;
        if (ethers.version.startsWith('6.')) {
            provider = new (ethers as any).JsonRpcProvider(DocumentService.CHAININFO.rpcUrl);
        } else if (ethers.version.includes('/5.')) {
            provider = new (ethers as any).providers.JsonRpcProvider(DocumentService.CHAININFO.rpcUrl);
        }
        return provider;
    }

    private async mineDocument(tokenRegistry: ethers.Contract, documentModel:DocumentModel, tokenId:string, encryptedRemarks:string){
        let tx;
        // query gas station
        if (DocumentService.CHAININFO.gasStation) {
            const gasFees = await DocumentService.CHAININFO.gasStation();
            console.log('gasFees', gasFees);

            tx = await tokenRegistry.mint(documentModel.owner, documentModel.holder, tokenId, encryptedRemarks, {
                maxFeePerGas: gasFees!.maxFeePerGas?.toBigInt() ?? 0,
                maxPriorityFeePerGas: gasFees!.maxPriorityFeePerGas?.toBigInt() ?? 0,
            });
        } else {
            tx = await tokenRegistry.mint(documentModel.owner, documentModel.holder, tokenId, encryptedRemarks);
        }

        // Long polling for the transaction to be mined, can be optimized to skip the wait for transaction to be confirmed in 1 block
        return {
            receipt: await tx.wait(),
            hash: tx.hash,
        }
    }

    private getDocumentSchema(documentModel: DocumentModel, issuanceDate:Date, expirationDate:Date, DID_KEY_PAIRS:any) {
        return {
            "@context": [
                "https://www.w3.org/2018/credentials/v1",
                "https://w3id.org/security/bbs/v1",
                "https://trustvc.io/context/transferable-records-context.json",
                "https://trustvc.io/context/render-method-context.json",
                "https://trustvc.io/context/attachments-context.json",
                SUPPORTED_DOCUMENT[documentModel.documentId],
            ],
            type: ["VerifiableCredential"],
            "credentialStatus": {
                "type": "TransferableRecords",
                "tokenNetwork": {
                    "chain": DocumentService.CHAININFO.currency,
                    "chainId": DocumentService.CHAINID
                },
                "tokenRegistry": DocumentService.SYSTEM_TOKEN_REGISTRY_ADDRESS,
            },
            "renderMethod": [
                {
                    "id": "https://generic-templates.tradetrust.io",
                    "type": "EMBEDDED_RENDERER",
                    "templateName": documentModel.documentId
                }
            ],
            credentialSubject:documentModel.credentialSubject,
            "issuanceDate": issuanceDate.toISOString(),
            "expirationDate": expirationDate.toISOString(),
            "issuer": DID_KEY_PAIRS.id?.split('#')?.[0],
        }
    }

    private isAddress(address: string): boolean {
        return ethers.version.startsWith("6.") ? (ethers as any).isAddress(address) : (ethers as any).utils.isAddress(address);
    }

    private async getActionParams(action:string, vc:any, transferabilityData:DocumentTransferabilityModel){
        let params:any[] = [];
        const encryptedRemark = "0x" + encrypt(transferabilityData.remarks as string, vc.id);

        switch (action) {
            case "transferHolder" :
                if (!this.isAddress(transferabilityData.newHolder as string)) {
                    console.error("Invalid Ethereum address:", transferabilityData.newHolder);
                    return params;
                }
                params = [transferabilityData.newHolder, encryptedRemark];
                break
            case "transferBeneficiary" :
                if (!this.isAddress(transferabilityData.newBeneficiary as string)) {
                    console.error("Invalid Ethereum address:", transferabilityData.newBeneficiary);
                    return params;
                }
                params = [transferabilityData.newBeneficiary, encryptedRemark];
                break
            case "nominate" :
                if (!this.isAddress(transferabilityData.newBeneficiary as string)) {
                    console.error("Invalid Ethereum address:", transferabilityData.newBeneficiary);
                    return params;
                }
                params = [transferabilityData.newBeneficiary, encryptedRemark];
                break
            case "transferOwners" :
                if (!this.isAddress(transferabilityData.newBeneficiary as string) || !this.isAddress(transferabilityData.newHolder as string)) {
                    console.error("Invalid Ethereum address:", transferabilityData.newBeneficiary, transferabilityData.newHolder);
                    return params;
                }
                params = [transferabilityData.newBeneficiary, encryptedRemark];
                break
            default:
                params = [encryptedRemark];
                console.error("Invalid action:", action);
            return params
        }
    }
}