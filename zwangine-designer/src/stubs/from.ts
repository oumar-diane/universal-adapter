import {FromDefinition} from "@/@types";

/**
 * This is a stub  From in YAML format.
 * It is used to test the Canvas component.
 */
export const fromYaml = `
- from:
    uri: timer
    parameters:
      timerName: tutorial
    steps:
    - to:
        uri: direct
        parameters:
          name: my-route
`;

/**
 * This is a stub  From in JSON format.
 * It is used to test the Canvas component.
 */
export const fromJson: { from: FromDefinition } = {
    from: {
        uri: 'timer',
        parameters: {
            timerName: 'tutorial',
        },
        steps: [
            {
                to: {
                    uri: 'direct:my-route',
                },
            },
        ],
    },
};