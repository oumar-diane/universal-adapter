import { getRandomId } from "@/lib/ua-utils";

export const workflowTemplate = () => {
    return `- workflow:
    id: ${getRandomId('workflow')}
    from:
      id: ${getRandomId('from')}
      uri: timer
      parameters:
        period: "1000"
        timerName: template
      steps:
        - log:
            id: ${getRandomId('log')}
            message: \${body}`;
};
