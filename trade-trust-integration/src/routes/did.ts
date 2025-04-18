import fs from "fs";
import path from "path";
import express, {NextFunction} from "express";
const router = express.Router();


router.get("/did.json", function(req, res, next){
    try {
        const didJsonPath = path.join(__dirname, "../../did.json");
        const didJson = fs.readFileSync(didJsonPath, "utf-8");
        res.json(JSON.parse(didJson));
    } catch (error) {
        console.error(error);
        next(error);
    }
});

export {router as didRouter};
