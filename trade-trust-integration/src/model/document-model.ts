import {CredentialSubjects} from "@trustvc/trustvc/w3c/vc";


export interface DocumentModel {
    documentId: string,
    credentialSubject?: CredentialSubjects,
    owner: string,
    holder: string,
    remarks?: string,
    expirationDate?: string,
}

export interface DocumentTransferabilityModel {
    remarks?: string,
    newHolder?: string,
    newBeneficiary?: string,
}

export enum TransferabilityActions{
    TRANSFER_OWNERS = 'transferOwners',
    TRANSFER_HOLDER = 'transferHolder',
    TRANSFER_BENEFICIARY = 'transferBeneficiary',
    NOMINATE = 'nominate'
}