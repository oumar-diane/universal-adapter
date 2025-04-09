import {writeFile} from "fs"

export class SchemaWriter{

    static writeToFile(schema: {[index:string]:any}, fullPath: string) {

        writeFile(fullPath, JSON.stringify(schema, null, 2), (err: any) => {
            if (err) {
                console.error(`Error writing schema to file: ${err}`);
            } else {
                console.log(`Schema written to ${fullPath}`);
            }
        });
    }
}