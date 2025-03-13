import {useLocation, useNavigate} from "react-router";
import {useEffect} from "react";
import logo from "@/assets/zenithbloxLogo.jpg";
import {BsInfoCircle} from "react-icons/bs";


export function MainLayout({children}: {children: React.ReactNode}) {

    return (
        <div className="grid grid-rows-[60px_auto] gap-1 w-full h-full">
            {children}
        </div>
    )
}

export function MainContent({children}: {children: React.ReactNode}) {

    return (
        <>
            {children}
        </>
    )
}

export function MainHeader() {

    const currentLocation = useLocation()
    const navigate = useNavigate()

    useEffect(() => {
        toggleActive()
    })

    function toggleActive() {
        const navbar = document.getElementById("navbar");
        for(const navItem of navbar!.children) {
            if(currentLocation.pathname === "/" && navItem.id === "designer" || currentLocation.pathname === "/"+navItem.id){
                navItem?.classList.add("bg-primary");
                navItem?.classList.add("text-white");
            }else{
                navItem?.classList.remove("bg-primary");
                navItem?.classList.remove("text-white");
            }
        }
    }


    return (
        <div className="flex flex-row px-1 py-1 justify-between">
            <div className="flex flex-row items-center space-x-1">
                <img src={logo}  className="w-[50px] rounded-full" />
                <h2 className="font-sans font-bold uppercase">Zenithblox</h2>
            </div>
            <div className="flex flex-row items-center space-x-2" id="navbar">
                <span className="flex flex-row justify-center cursor-pointer w-[150px] font-sans font-bold" id="designer" onClick={()=>navigate("/")}>Designer</span>
                <span className="flex flex-row justify-center cursor-pointer w-[150px] font-sans font-bold" id="manager" onClick={()=>navigate("/manager")}>Manager</span>
                <span className="flex flex-row justify-center cursor-pointer w-[150px] font-sans font-bold" id="dashboard" onClick={()=>navigate("/dashboard")}>Dashboard</span>
            </div>
            <div className="flex flex-row items-center">
                <BsInfoCircle />
            </div>

        </div>
    )
}
