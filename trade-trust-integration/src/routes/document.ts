import express from "express";
const router = express.Router();
import { SUPPORTED_DOCUMENT } from "../configs";
import {
    SignedVerifiableCredential,
    verifyDocument
} from "@trustvc/trustvc";
import {DocumentService} from "../service/document-service";
import {DocumentModel, DocumentTransferabilityModel, TransferabilityActions} from "../model/document-model";


const documentService = new DocumentService()

router.post("/transferability/:actionName", async function(req, res, next) {
    try {
        let { actionName } = req.params;
        const body = req.body as { signedW3CDocument: SignedVerifiableCredential, transferabilityData: DocumentTransferabilityModel };
        const transferabilityResult = await documentService.applyDocumentTransferabilityAction(body.transferabilityData, body.signedW3CDocument, actionName as TransferabilityActions);

        return res.json({
            ...transferabilityResult,
        });
    } catch (error) {
        console.error(error);
        next(error);
    }
})

router.post("/verify", async function(req, res, next) {
    try {
        const { signedW3CDocument } = req.body as { signedW3CDocument: SignedVerifiableCredential };
        const verificationFragments = await documentService.applyVerification(signedW3CDocument);

        return res.json({
            verificationFragments,
        });
    } catch (error) {
        console.error(error);
        next(error);
    }
})

router.post("/create/:documentId", async function(req, res, next) {
    try {
        let { documentId } = req.params;
        documentId = documentId?.toUpperCase() || '';

        // Validate documentId
        if (!SUPPORTED_DOCUMENT[documentId]) {
            throw new Error('Document not supported');
        }

        const rawDocument = req.body as DocumentModel;
        rawDocument.documentId = documentId;

        // Validate required parameters for tradeTrust

        if (!process.env.WALLET_PRIVATE_KEY) {
            throw new Error('Wallet private key not found in environment variables');
        }

        if (!process.env.DID_KEY_PAIRS) {
            throw new Error('DID key pairs not found in environment variables');
        }

        if (!process.env.TOKEN_REGISTRY_ADDRESS) {
            throw new Error('Token registry address not found in environment variables');
        }

        const result = await documentService.createDocument(rawDocument)

        return res.json(result);

    } catch (error) {
        console.error(error);
        next(error);
    }
});




export { router as documentRouter };