import {useNavigate} from "react-router";
import {useState} from "react";
import logo from "@/assets/zenithbloxLogo.jpg";
import {BsInfoCircle} from "react-icons/bs";
import {UaAboutModal} from "@/layouts/universal-adapter-modal.tsx";
import {DropdownItem, Icon} from "@patternfly/react-core";
import {ExternalLinkAltIcon, FireIcon} from '@patternfly/react-icons'
import {useMainHeaderActiveItem} from "@/hooks/use-main-header-active-item.tsx";
import {NavItem} from "@/layouts/layout-model.ts";


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

    const navigate = useNavigate()
    const mainHeaderActiveItem = useMainHeaderActiveItem()
    const [isAboutModalOpen, setIsAboutModalOpen] = useState(false);

    const toggleAboutModal = () => {
        setIsAboutModalOpen(!isAboutModalOpen);
    };

    function handleClick(item:NavItem, route:string) {
        mainHeaderActiveItem.setActiveTabHandler(item);
        navigate(route);
    }

    function isActive(item: NavItem) {
        return mainHeaderActiveItem.activeTab === item;
    }


    return (
        <div className="flex flex-row px-1 py-1 justify-between">
            <div className="flex flex-row items-center space-x-1">
                <img src={logo}  className="w-[50px] rounded-full" />
                <h2 className="font-bold font-sans uppercase">Zenithblox</h2>
            </div>
            <div className="flex flex-row items-center space-x-2" id="navbar">
                <span className={`flex flex-row justify-center cursor-pointer w-[150px] font-sans font-bold ${isActive(NavItem.DESIGNER)? 'bg-primary text-white':''}`}  onClick={()=>handleClick(NavItem.DESIGNER, "/")}>Designer</span>
                <span className={`flex flex-row justify-center cursor-pointer w-[150px] font-sans font-bold ${isActive(NavItem.MANAGER)? 'bg-primary text-white':''}`}  onClick={()=>handleClick(NavItem.MANAGER, "/manager")}>Manager</span>
                <span className={`flex flex-row justify-center cursor-pointer w-[150px] font-sans font-bold ${isActive(NavItem.DASHBOARD)? 'bg-primary text-white':''}`}  onClick={()=>handleClick(NavItem.DASHBOARD, "/dashboard")}>Dashboard</span>
            </div>
            <div className="flex flex-row items-center space-x-2">
                <a href="https://www.zenithblox.com" target="_blank" rel="noopener noreferrer">
                    <DropdownItem key="tutorial">
                        <Icon isInline>
                            <ExternalLinkAltIcon />
                        </Icon>
                        &nbsp;<span className="pf-u-mr-lg">Tutorials</span>
                    </DropdownItem>
                </a>
                <a href="https://www.zenithblox.com" target="_blank" rel="noopener noreferrer">
                    <DropdownItem key="hawtio">
                        <Icon isInline>
                            <FireIcon />
                        </Icon>
                        &nbsp;<span className="pf-u-mr-lg">Doc</span>
                    </DropdownItem>
                </a>
                <BsInfoCircle onClick={toggleAboutModal} className="cursor-pointer" />
            </div>
            <UaAboutModal
                handleCloseModal={() => {
                    toggleAboutModal();
                }}
                isModalOpen={isAboutModalOpen}
            />
        </div>
    )
}
