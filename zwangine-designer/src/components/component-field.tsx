import {Label} from "@radix-ui/react-label";
import {Input} from "@/components/ui/input.tsx";
import {Switch} from "@/components/ui/switch.tsx";
import {useDispatch, useSelector} from "react-redux";
import {PipelineAction, PipelineStore} from "@/lib/store.ts";
import {FaInfoCircle} from "react-icons/fa";
import {HoverCard, HoverCardContent, HoverCardTrigger} from "@/components/ui/hover-card.tsx";
import {patch} from "@/lib/state/configuration-store.ts";

export function ComponentField(propertyConf:{[key:string]:any}) {

    const pipelineState = useSelector((state:PipelineStore) => state.pipelinesNodeConfigs)
    const dispatch:PipelineAction = useDispatch()

    function handleInputChange() {
        dispatch(patch(propertyConf))
    }

    console.log("component field: ", propertyConf)

    switch (propertyConf["type"]) {
        case "string":
            return (
                <div className="flex flex-row space-x-2 items-center" key={propertyConf["index"]}>
                    <div className="flex flex-row items-center space-x-1">
                        <Label className="font-mono flex flex-row items-center" htmlFor={propertyConf["displayName"]}>
                            {propertyConf["displayName"]}
                            {propertyConf["required"] == true && <p className="text-[red]">*</p>}
                        </Label>
                        <HoverCard>
                            <HoverCardTrigger asChild>
                                <FaInfoCircle className="cursor-pointer"/>
                            </HoverCardTrigger>
                            <HoverCardContent className="bg-primary text-white">
                                <p className="text-[8px]">{propertyConf["description"]}</p>
                            </HoverCardContent>
                        </HoverCard>
                    </div>
                    {propertyConf["secret"]===true && <Input className="w-[300px]" type="password" id={propertyConf["displayName"]}
                            placeholder={propertyConf["displayName"]} onInput={(e) => console.log(e)}/>}
                    {propertyConf["secret"]===false && <Input className="w-[300px]" type="text" id={propertyConf["displayName"]}
                                                             placeholder={propertyConf["displayName"]} onInput={handleInputChange}/>}
                </div>
            )
        case "boolean":
            return (
                <div className="flex flex-row space-x-2 items-center"  key={propertyConf["index"]}>
                    <div className="flex flex-row items-center space-x-1">
                        <Label className="font-mono flex flex-row" htmlFor={propertyConf["displayName"]}>
                            {propertyConf["displayName"]}
                            {propertyConf["required"] == true && <p className="text-[red]">*</p>}
                        </Label>
                        <HoverCard>
                            <HoverCardTrigger asChild>
                                <FaInfoCircle className="cursor-pointer"/>
                            </HoverCardTrigger>
                            <HoverCardContent className="bg-primary text-white">
                                <p className="text-[8px]">{propertyConf["description"]}</p>
                            </HoverCardContent>
                        </HoverCard>
                    </div>
                    <Switch id={propertyConf["displayName"]} onInput={handleInputChange} />
                </div>
            )
        default:
            return (
                <div className="flex flex-row space-x-2 items-center"  key={propertyConf["index"]}>
                   <div className="flex flex-row items-center space-x-1">
                       <Label className="font-mono flex flex-row" htmlFor={propertyConf["displayName"]}>
                           {propertyConf["displayName"]}
                           {propertyConf["required"] == true && <p className="text-[red]">*</p>}
                       </Label>
                       <HoverCard>
                           <HoverCardTrigger asChild>
                               <FaInfoCircle className="cursor-pointer"/>
                           </HoverCardTrigger>
                           <HoverCardContent className="bg-primary text-white">
                               <p className="text-[8px]">{propertyConf["description"]}</p>
                           </HoverCardContent>
                       </HoverCard>
                   </div>
                    <Input type="text" placeholder="Email" onInput={handleInputChange}  />
                </div>
            )

    }

}