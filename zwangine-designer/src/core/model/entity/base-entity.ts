/** This is the enum with the registered Workflow entities supported */
export const enum EntityType {
    Workflow = 'workflow',
    OnException = 'onException',
    Rest = 'rest',
    RestConfiguration = 'restConfiguration',
    WorkflowConfiguration = 'workflowConfiguration',
    OnCompletion = 'onCompletion',
    Metadata = 'metadata',
    NonVisualEntity = 'nonVisualEntity',
}

export interface BaseEntity {
    /** Internal API fields */
    id: string;
    readonly type: EntityType;

    /** Retrieve the model from the underlying Workflow entity */
    toJSON(): unknown;
}
