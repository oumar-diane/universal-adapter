import { CHAIN_ID, SUPPORTED_CHAINS, v5ContractAddress, v5Contracts } from "@trustvc/trustvc";
import { utils as v5Utils } from "@trustvc/trustvc/token-registry-v5";
import { ethers } from "ethers";
import { writeEnvVariable } from "./utils";
import {getRPCUrl} from "../util/provider-utils";



const main = async () => {
    if (!process.env.WALLET_PRIVATE_KEY) {
        throw new Error("Wallet private key not found in environment variables");
    }

    const chainId: CHAIN_ID = (process.env.NET as CHAIN_ID) ?? CHAIN_ID.amoy;
    const CHAININFO = SUPPORTED_CHAINS[chainId];
    Object.assign(CHAININFO, {
        rpcUrl:getRPCUrl(chainId) ?? CHAININFO.rpcUrl
    })

    const unconnectedWallet = new ethers.Wallet(process.env.WALLET_PRIVATE_KEY);
    const JsonRpcProvider = ethers.version.startsWith("6.")
        ? (ethers as any).JsonRpcProvider
        : (ethers as any).providers.JsonRpcProvider;
    const provider = new JsonRpcProvider(CHAININFO.rpcUrl);
    const wallet = unconnectedWallet.connect(provider);
    const walletAddress = await wallet.getAddress();

    const { TDocDeployer__factory } = v5Contracts;

    const { TokenImplementation, Deployer } = v5ContractAddress;
    const deployerContract = new ethers.Contract(Deployer[chainId], TDocDeployer__factory.abi, wallet);
    const initParam = v5Utils.encodeInitParams({
        name: "DemoTokenRegistry",
        symbol: "DTR",
        deployer: walletAddress,
    });

    let tx;
    if (CHAININFO.gasStation) {
        const gasFees = await CHAININFO.gasStation();
        console.log("gasFees", gasFees);

        tx = await deployerContract.deploy(TokenImplementation[chainId], initParam, {
            maxFeePerGas: gasFees!.maxFeePerGas?.toBigInt() ?? 0,
            maxPriorityFeePerGas: gasFees!.maxPriorityFeePerGas?.toBigInt() ?? 0,
        });
    } else {
        tx = await deployerContract.deploy(TokenImplementation[chainId], initParam);
    }
    const receipt = await tx.wait();
    console.log("receipt:", JSON.stringify(receipt, null, 2));

    let registryAddress;
    if (ethers.version.includes("/5.")) {
        registryAddress = v5Utils.getEventFromReceipt<any>(
            receipt,
            (deployerContract.interface as any).getEventTopic("Deployment"),
            deployerContract.interface,
        ).args.deployed;
    } else if (ethers.version.startsWith("6.")) {
        registryAddress = v5Utils.getEventFromReceipt<any>(receipt, "Deployment", deployerContract.interface).args.deployed;
    } else {
        throw new Error("Unsupported ethers version");
    }

    console.log(`Contract Address: ${registryAddress}`);
    console.log(`Transaction: ${JSON.stringify(receipt, null, 2)}`);

    // write registryAddress to .env file as TOKEN_REGISTRY_ADDRESS key
    writeEnvVariable("TOKEN_REGISTRY_ADDRESS", registryAddress);
};
main();