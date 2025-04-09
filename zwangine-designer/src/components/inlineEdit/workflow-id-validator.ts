import {BaseVisualEntity} from "@/core/model/entity/base-visual-entity.ts";
import {ValidationResult, ValidationStatus} from "@/core/model/validation.ts";

export class WorkflowIdValidator {
    private static URI_REGEXP = /^[a-z\d]([-a-z\d]*[a-z\d])?(\.[a-z\d]([-a-z\d]*[a-z\d])?)*$/gm;

    /**
     * Verifies that the provided name is valid
     * Regex: [a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*
     * @param name
     */
    static isNameValidCheck(name: string): boolean {
        const isValid = WorkflowIdValidator.URI_REGEXP.test(name);
        WorkflowIdValidator.URI_REGEXP.lastIndex = 0;

        return isValid;
    }

    static validateUniqueName(flowName: string, visualEntities: BaseVisualEntity[]): ValidationResult {
        const errMessages = [];
        const flowsIds = visualEntities.map((flow) => flow.getId());

        const isValidURI = WorkflowIdValidator.isNameValidCheck(flowName);
        if (!isValidURI) {
            errMessages.push('Name should only contain lowercase letters, numbers, and dashes');
        }

        const isUnique = !flowsIds.includes(flowName);
        if (!isUnique) {
            errMessages.push('Name must be unique');
        }

        return {
            status: isValidURI && isUnique ? ValidationStatus.Success : ValidationStatus.Error,
            errMessages,
        };
    }
}
