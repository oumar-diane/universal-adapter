import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import {
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger,
} from "@/components/ui/tabs"
import {ComponentField} from "@/components/component-field.tsx";
import {ScrollArea} from "@/components/ui/scroll-area.tsx";

export function ScheduleNodeTab({props}:{[index:string]:any}) {
    return (
        <Tabs defaultValue="properties" >
            <TabsList className="grid w-full grid-cols-2 text-primary">
                <TabsTrigger value="properties">Properties</TabsTrigger>
                <TabsTrigger value="info">Info</TabsTrigger>
            </TabsList>
            <TabsContent value="properties">
                <Card>
                    <CardHeader>
                        <CardTitle className='text-primary'>Properties</CardTitle>
                        <CardDescription >
                            {props.data.component["description"]}
                        </CardDescription>
                    </CardHeader>
                    <ScrollArea className="h-[370px]">
                        <CardContent className="space-y-2">
                            <div className="flex flex-col gap-2 text-primary">
                                {Object.values(props.data.component["properties"])?.map((property) => ComponentField(property as {[index:string]:any}))}
                            </div>
                        </CardContent>
                    </ScrollArea>
                </Card>
            </TabsContent>
            <TabsContent value="info">
                <Card>
                    <CardHeader>
                        <CardTitle>Info</CardTitle>
                        <CardDescription>
                            Make changes to your account here. Click save when you're done.
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-2">
                        info
                    </CardContent>
                </Card>
            </TabsContent>
        </Tabs>
    )
}
