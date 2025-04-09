import { parse } from 'yaml';
import {WorkflowDefinition} from "@/@types";


/**
 * This is a stub Workflow in YAML format.
 * It is used to test the Canvas component.
 */
export const workflowYaml = `
- workflow:
    id: workflow-8888
    from:
      uri: timer
      parameters:
        timerName: tutorial
      steps:
      - set-header:
          simple: "\${random(2)}"
          name: myChoice
      - choice:
          when:
          - simple: "\${header.myChoice} == 1"
            steps:
            - log:
                id: log-1
                message: We got a one.
          otherwise:
            steps:
            - to:
                uri: 'amqp:queue:'
            - to:
                uri: 'amqp:queue:'
            - log:
                id: log-2
                message: "We got a \${body}"
      - to:
          uri: direct:my-route
          parameters:
            bridgeErrorHandler: true
`;

/**
 * This is a stub Workflow in JSON format.
 * It is used to test the Canvas component.
 */
export const workflowAbstractRepresentation: { workflow: WorkflowDefinition } = parse(workflowYaml)[0];

export const workflowWithDisabledSteps: { route: WorkflowDefinition } = parse(`
workflow:
  id: route-8888
  from:
    uri: timer
    steps:
      - log:
          disabled: true
          message: \${body}
      - to:
          uri: direct
          disabled: true
`);

export const doTryWorkflowJson = {
    workflow: {
        id: 'workflow-1137',
        from: {
            uri: 'direct:start',
            steps: [
                {
                    doTry: {
                        steps: [
                            {
                                process: {
                                    ref: 'processorFail',
                                },
                            },
                            {
                                to: {
                                    uri: 'mock:result',
                                },
                            },
                        ],
                        doCatch: [
                            {
                                id: 'first',
                                steps: [
                                    {
                                        to: {
                                            uri: 'mock:catch',
                                        },
                                    },
                                ],
                                exception: ['java.io.IOException', 'java.lang.IllegalStateException'],
                                onWhen: {
                                    simple: {
                                        expression: "${exception.message} contains 'Damn'",
                                    },
                                },
                            },
                            {
                                id: 'second',
                                steps: [
                                    {
                                        to: {
                                            uri: 'mock:catchCamel',
                                        },
                                    },
                                ],
                                exception: ['org.apache.camel.CamelExchangeException'],
                            },
                        ],
                        doFinally: {
                            steps: [
                                {
                                    to: {
                                        uri: 'mock:finally',
                                    },
                                },
                            ],
                        },
                    },
                },
            ],
        },
    },
};

